/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.PowerManager
import androidx.annotation.VisibleForTesting
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.element.android.appconfig.ElementCallConfig
import io.element.android.features.call.api.CallType
import io.element.android.features.call.api.CurrentCall
import io.element.android.features.call.impl.R
import io.element.android.features.call.impl.notifications.CallNotificationData
import io.element.android.features.call.impl.notifications.RingingCallNotificationCreator
import io.element.android.features.call.impl.notifications.hasSameRingingIdentityAs
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.di.annotations.ApplicationContext
import io.element.android.libraries.matrix.api.MatrixClientProvider
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.ui.media.ImageLoaderHolder
import io.element.android.libraries.push.api.notifications.ForegroundServiceType
import io.element.android.libraries.push.api.notifications.NotificationIdProvider
import io.element.android.libraries.push.api.notifications.OnMissedCallNotificationHandler
import io.element.android.services.appnavstate.api.AppForegroundStateService
import io.element.android.services.toolbox.api.systemclock.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import io.element.android.libraries.matrix.api.room.JoinedRoom
import kotlin.math.min

private const val incomingCallTraceTag = "IncomingCallTrace"

/**
 * 活动通话管理器接口
 *
 * 负责管理当前活动通话的状态，包括来电注册、通话状态更新、挂断等操作。
 */
interface ActiveCallManager {
    /**
     * 当前已接起或通话中的活动通话状态。
     */
    val activeCall: StateFlow<ActiveCall?>

    /**
     * 当前所有仍处于振铃中的来电列表。
     *
     * 这里和 [activeCall] 分离维护，目的是让“已接通/通话中”的状态与“仍待处理的来电集合”解耦，
     * 从而支持多个来电同时存在且彼此独立接听/拒绝。
     */
    val ringingCalls: StateFlow<List<CallNotificationData>>

    suspend fun registerIncomingCall(notificationData: CallNotificationData)

    suspend fun clearIncomingCallNotification()

    suspend fun setIncomingCallUiVisible(isVisible: Boolean)

    suspend fun hangUpCall(
        callType: CallType,
        notificationData: CallNotificationData? = null,
    )

    suspend fun joinedCall(callType: CallType)
}

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultActiveCallManager(
    @ApplicationContext private val context: Context,
    @AppCoroutineScope
    private val coroutineScope: CoroutineScope,
    private val onMissedCallNotificationHandler: OnMissedCallNotificationHandler,
    private val ringingCallNotificationCreator: RingingCallNotificationCreator,
    private val notificationManagerCompat: NotificationManagerCompat,
    private val matrixClientProvider: MatrixClientProvider,
    private val defaultCurrentCallService: DefaultCurrentCallService,
    private val appForegroundStateService: AppForegroundStateService,
    private val imageLoaderHolder: ImageLoaderHolder,
    private val systemClock: SystemClock,
) : ActiveCallManager {
    private val tag = "ActiveCallManager"
    private var incomingCallUiVisible = false
    private val ringingTimeoutJobs = mutableMapOf<String, Job>()
    private val ringingObservationJobs = mutableMapOf<String, Job>()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val activeWakeLock: PowerManager.WakeLock? = context.getSystemService<PowerManager>()
        ?.takeIf { it.isWakeLockLevelSupported(PowerManager.PARTIAL_WAKE_LOCK) }
        ?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "${context.packageName}:IncomingCallWakeLock")
        ?.apply {
            setReferenceCounted(false)
        }

    override val activeCall = MutableStateFlow<ActiveCall?>(null)
    override val ringingCalls = MutableStateFlow<List<CallNotificationData>>(emptyList())

    private val mutex = Mutex()

    init {
        observeRingingCall()
        observeCurrentCall()
    }

    override suspend fun registerIncomingCall(notificationData: CallNotificationData) {
        var shouldShowNotification = false
        var shouldUseFullScreenIntent = false
        var shouldPrepareAvatarLoader = false
        mutex.withLock {
            val nowMillis = systemClock.epochMillis()
            val ringDuration =
                min(
                    notificationData.expirationTimestamp - nowMillis,
                    ElementCallConfig.RINGING_CALL_DURATION_SECONDS * 1000L
                )

            Timber.tag(incomingCallTraceTag).w(
                "ActiveCallManager.registerIncomingCall sessionId=%s roomId=%s eventId=%s ringDurationMs=%s nowMs=%s expirationMs=%s activeCall=%s",
                notificationData.sessionId,
                notificationData.roomId,
                notificationData.eventId,
                ringDuration,
                nowMillis,
                notificationData.expirationTimestamp,
                activeCall.value,
            )
            if (ringDuration < 0) {
                Timber.tag(tag).d("Received timed-out incoming ringing call for room id: ${notificationData.roomId}, cancel ringing")
                Timber.tag(incomingCallTraceTag).w(
                    "ActiveCallManager ignored expired incoming call roomId=%s eventId=%s ringDurationMs=%s",
                    notificationData.roomId,
                    notificationData.eventId,
                    ringDuration,
                )
                return
            }

            val currentActiveCall = activeCall.value
            if (currentActiveCall?.callState == CallState.InCall && currentActiveCall.callType.matchesIncomingCall(notificationData)) {
                /**
                 * 中文说明：
                 * 当前房间已经处于通话中时，后续同步到的 m.call.notify 只是同一条通话链路里的状态广播，
                 * 不能再被回流成“新的来电”塞进 ringingCalls，否则通话页顶部会错误叠加来电 overlay。
                 */
                Timber.tag(incomingCallTraceTag).i(
                    "ActiveCallManager ignored incoming call because the same room is already in call sessionId=%s roomId=%s eventId=%s",
                    notificationData.sessionId,
                    notificationData.roomId,
                    notificationData.eventId,
                )
                return
            }

            Timber.tag(tag).d("Received incoming call for room id: ${notificationData.roomId}, ringDuration(ms): $ringDuration")
            if (ringingCalls.value.any { it.eventId == notificationData.eventId }) {
                Timber.tag(tag).d("Ignoring duplicate incoming call event: ${notificationData.eventId}")
                Timber.tag(incomingCallTraceTag).i("ActiveCallManager ignored duplicate incoming call eventId=%s", notificationData.eventId)
                return
            }
            val previousMatchingCall = ringingCalls.value.firstOrNull { existingCall ->
                existingCall.hasSameRingingIdentityAs(notificationData)
            }
            val hadNoRingingCall = ringingCalls.value.isEmpty()
            if (previousMatchingCall != null) {
                /**
                 * 同一房间里同一发起者连续发多条 call-notify 时，UI 上应视为同一通振铃来电，
                 * 这里必须原子替换旧条目，避免中间发出空列表导致全屏来电页误判来电已结束。
                 */
                Timber.tag(incomingCallTraceTag).i(
                    "ActiveCallManager replacing previous ringing call oldEventId=%s newEventId=%s roomId=%s senderId=%s",
                    previousMatchingCall.eventId,
                    notificationData.eventId,
                    notificationData.roomId,
                    notificationData.senderId,
                )
                ringingTimeoutJobs.remove(previousMatchingCall.eventId.value)?.cancel()
                ringingObservationJobs.remove(previousMatchingCall.eventId.value)?.cancel()
                cancelIncomingCallNotification(previousMatchingCall)
            }
            ringingCalls.value = if (previousMatchingCall == null) {
                ringingCalls.value + notificationData
            } else {
                ringingCalls.value.map { existingCall ->
                    if (existingCall.eventId == previousMatchingCall.eventId) notificationData else existingCall
                }
            }
            if (previousMatchingCall != null) {
                Timber.tag(incomingCallTraceTag).w(
                    "ActiveCallManager replaced ringing call oldEventId=%s newEventId=%s ringingCount=%s",
                    previousMatchingCall.eventId,
                    notificationData.eventId,
                    ringingCalls.value.size,
                )
            }
            startRingingLifecycleLocked(notificationData = notificationData, ringDuration = ringDuration)
            updateRingingCallFlagLocked()
            refreshWakeLockLocked()
            shouldPrepareAvatarLoader = true
            shouldShowNotification = true
            shouldUseFullScreenIntent = hadNoRingingCall
            Timber.tag(incomingCallTraceTag).w(
                "ActiveCallManager stored ringing call eventId=%s ringingCount=%s firstEventId=%s",
                notificationData.eventId,
                ringingCalls.value.size,
                ringingCalls.value.firstOrNull()?.eventId,
            )
        }

        if (shouldPrepareAvatarLoader) {
            setUpCoil(notificationData.sessionId)
        }
        if (shouldShowNotification) {
            showIncomingCallNotification(
                notificationData = notificationData,
                useFullScreenIntent = shouldUseFullScreenIntent,
            )
        }
    }

    override suspend fun clearIncomingCallNotification() = mutex.withLock {
        cancelIncomingCallNotificationsLocked()
    }

    override suspend fun setIncomingCallUiVisible(isVisible: Boolean) = mutex.withLock {
        Timber.tag(incomingCallTraceTag).i("ActiveCallManager.setIncomingCallUiVisible %s -> %s", incomingCallUiVisible, isVisible)
        incomingCallUiVisible = isVisible
        if (isVisible) {
            cancelIncomingCallNotificationsLocked()
        }
    }

    @OptIn(DelicateCoilApi::class)
    private suspend fun setUpCoil(sessionId: SessionId) {
        val matrixClient = matrixClientProvider.getOrRestore(sessionId).getOrNull() ?: return
        SingletonImageLoader.setUnsafe(imageLoaderHolder.get(matrixClient))
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun incomingCallTimedOut(
        displayMissedCallNotification: Boolean,
        eventId: io.element.android.libraries.matrix.api.core.EventId? = null,
    ) {
        Timber.tag(tag).d("Incoming call timed out")
        removeRingingCall(
            eventId = eventId ?: ringingCalls.value.firstOrNull()?.eventId ?: return,
            displayMissedCallNotification = displayMissedCallNotification,
        )
    }

    override suspend fun hangUpCall(
        callType: CallType,
        notificationData: CallNotificationData?,
    ) = mutex.withLock {
        Timber.tag(tag).d("Hang up call: $callType")
        val latestMatchingNotificationData = notificationData?.let { staleNotificationData ->
            ringingCalls.value.firstOrNull { it.hasSameRingingIdentityAs(staleNotificationData) }
        }
        val ringingCallToDecline = latestMatchingNotificationData
            ?: notificationData
            ?: ringingCalls.value.firstOrNull { callType.matchesIncomingCall(it) }
        val currentActiveCall = activeCall.value
        val shouldSendBusyMessage = ringingCallToDecline != null &&
            ringingCallToDecline.isDm &&
            currentActiveCall?.callState is CallState.InCall

        if (ringingCallToDecline != null) {
            Timber.tag(incomingCallTraceTag).w(
                "ActiveCallManager decline ringing call requestedEventId=%s latestEventId=%s roomId=%s senderId=%s",
                notificationData?.eventId,
                ringingCallToDecline.eventId,
                ringingCallToDecline.roomId,
                ringingCallToDecline.senderId,
            )
            val room = matrixClientProvider.getOrRestore(ringingCallToDecline.sessionId).getOrNull()
                ?.getJoinedRoom(ringingCallToDecline.roomId)
            if (room == null) {
                Timber.tag(incomingCallTraceTag).w(
                    "ActiveCallManager could not find session or room to decline incoming call eventId=%s",
                    ringingCallToDecline.eventId,
                )
            } else {
                room.declineCall(ringingCallToDecline.eventId)
                    .onSuccess {
                        if (shouldSendBusyMessage) {
                            sendBusyMessage(room, ringingCallToDecline)
                        }
                    }
                    .onFailure {
                        Timber.tag(incomingCallTraceTag).w(it, "ActiveCallManager failed to decline incoming call eventId=%s", ringingCallToDecline.eventId)
                    }
            }
            removeRingingCallLocked(
                eventId = ringingCallToDecline.eventId,
                displayMissedCallNotification = false,
            )
        }

        if (currentActiveCall == null) {
            if (ringingCallToDecline == null) {
                Timber.tag(tag).w("No active or ringing call, ignoring hang up")
            }
            return@withLock
        }
        if (ringingCallToDecline != null && currentActiveCall.callState is CallState.InCall) {
            return@withLock
        }
        if (!currentActiveCall.callType.isSameCallIdentity(callType)) {
            if (ringingCallToDecline == null) {
                Timber.tag(tag).w("Call type $callType does not match the active call type, ignoring")
            }
            return@withLock
        }
        activeCall.value = null
    }

    private suspend fun sendBusyMessage(
        room: JoinedRoom,
        notificationData: CallNotificationData,
    ) {
        /**
         * 中文说明：
         * 这里只有在“用户已经处于另一通通话中，并且当前挂断的是私聊来电”时，
         * 才补发一条普通文本消息，让来电方能在同一个 DM 房间里看到忙碌提示。
         */
        val busyMessage = context.getString(R.string.common_call_declined_busy_message)
        room.liveTimeline.sendMessage(
            body = busyMessage,
            htmlBody = null,
            intentionalMentions = emptyList(),
        ).onFailure {
            Timber.tag(incomingCallTraceTag).w(
                it,
                "ActiveCallManager failed to send busy message roomId=%s eventId=%s",
                notificationData.roomId,
                notificationData.eventId,
            )
        }
    }

    override suspend fun joinedCall(callType: CallType) = mutex.withLock {
        Timber.tag(tag).d("Joined call: $callType")
        ringingCalls.value.firstOrNull { callType.matchesIncomingCall(it) }?.let { matchingCall ->
            removeRingingCallLocked(
                eventId = matchingCall.eventId,
                displayMissedCallNotification = false,
            )
        }

        activeCall.value = ActiveCall(
            callType = callType,
            callState = CallState.InCall,
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun showIncomingCallNotification(
        notificationData: CallNotificationData,
        useFullScreenIntent: Boolean,
    ) {
        if (isIncomingCallNotificationSuppressed()) {
            Timber.tag(tag).d("Skipping ringing call notification because the app can show the incoming call in UI")
            Timber.tag(incomingCallTraceTag).i(
                "ActiveCallManager skip notification because foreground UI can show incoming call eventId=%s appForeground=%s incomingCallUiVisible=%s",
                notificationData.eventId,
                appForegroundStateService.isInForeground.value,
                incomingCallUiVisible,
            )
            return
        }
        Timber.tag(tag).d("Displaying ringing call notification")
        Timber.tag(incomingCallTraceTag).i("ActiveCallManager creating ringing notification eventId=%s", notificationData.eventId)
        val notification = ringingCallNotificationCreator.createNotification(
            sessionId = notificationData.sessionId,
            roomId = notificationData.roomId,
            eventId = notificationData.eventId,
            senderId = notificationData.senderId,
            roomName = notificationData.roomName,
            senderDisplayName = notificationData.senderName ?: notificationData.senderId.value,
            roomAvatarUrl = notificationData.avatarUrl,
            notificationChannelId = notificationData.notificationChannelId,
            timestamp = notificationData.timestamp,
            textContent = notificationData.textContent,
            expirationTimestamp = notificationData.expirationTimestamp,
            useFullScreenIntent = useFullScreenIntent,
            isDm = notificationData.isDm,
        ) ?: run {
            Timber.tag(incomingCallTraceTag).w("ActiveCallManager failed to create ringing notification eventId=%s", notificationData.eventId)
            return
        }
        mutex.withLock {
            if (isIncomingCallNotificationSuppressedLocked()) {
                Timber.tag(tag).d("Skipping ringing call notification because the app became able to show the incoming call in UI")
                Timber.tag(incomingCallTraceTag).i(
                    "ActiveCallManager skip final notification post because foreground UI can show incoming call " +
                        "eventId=%s appForeground=%s incomingCallUiVisible=%s",
                    notificationData.eventId,
                    appForegroundStateService.isInForeground.value,
                    incomingCallUiVisible,
                )
                cancelIncomingCallNotificationsLocked()
                return@withLock
            }
            runCatchingExceptions {
                notificationManagerCompat.notify(
                    getIncomingCallNotificationId(notificationData),
                    notification,
                )
                Timber.tag(incomingCallTraceTag).i("ActiveCallManager posted ringing notification eventId=%s", notificationData.eventId)
            }.onFailure {
                Timber.e(it, "Failed to publish notification for incoming call")
                Timber.tag(incomingCallTraceTag).w(it, "ActiveCallManager failed to post ringing notification eventId=%s", notificationData.eventId)
            }
        }
    }

    private suspend fun isIncomingCallNotificationSuppressed(): Boolean = mutex.withLock {
        isIncomingCallNotificationSuppressedLocked()
    }

    private fun isIncomingCallNotificationSuppressedLocked(): Boolean {
        // App 在前台时由内部 UI 响应来电，跳过系统通知以避免顶部 heads-up。
        return incomingCallUiVisible || appForegroundStateService.isInForeground.value
    }

    private fun cancelIncomingCallNotificationsLocked() {
        Timber.tag(tag).d("Ringing call notification cancelled")
        Timber.tag(incomingCallTraceTag).i("ActiveCallManager cancel all incoming call notifications")
        ringingCalls.value.forEach { notificationData ->
            notificationManagerCompat.cancel(getIncomingCallNotificationId(notificationData))
        }
        // 清理旧版本固定 ID 发布的通知，避免升级或竞态后残留一条不可管理的响铃通知。
        notificationManagerCompat.cancel(NotificationIdProvider.getForegroundServiceNotificationId(ForegroundServiceType.INCOMING_CALL))
    }

    private fun cancelIncomingCallNotification(notificationData: CallNotificationData) {
        Timber.tag(tag).d("Ringing call notification cancelled")
        Timber.tag(incomingCallTraceTag).i("ActiveCallManager cancel incoming call notification eventId=%s", notificationData.eventId)
        notificationManagerCompat.cancel(getIncomingCallNotificationId(notificationData))
    }

    private fun displayMissedCallNotification(notificationData: CallNotificationData) {
        Timber.tag(tag).d("Displaying missed call notification")
        coroutineScope.launch {
            onMissedCallNotificationHandler.addMissedCallNotification(
                sessionId = notificationData.sessionId,
                roomId = notificationData.roomId,
                eventId = notificationData.eventId,
            )
        }
    }

    private fun startRingingLifecycleLocked(
        notificationData: CallNotificationData,
        ringDuration: Long,
    ) {
        val eventKey = notificationData.eventId.value
        ringingTimeoutJobs.remove(eventKey)?.cancel()
        ringingObservationJobs.remove(eventKey)?.cancel()

        ringingTimeoutJobs[eventKey] = coroutineScope.launch {
            delay(timeMillis = ringDuration)
            incomingCallTimedOut(
                displayMissedCallNotification = true,
                eventId = notificationData.eventId,
            )
        }
        ringingObservationJobs[eventKey] = observeRingingCall(notificationData)
    }

    private fun observeRingingCall(notificationData: CallNotificationData): Job = coroutineScope.launch {
        val callType = CallType.RoomCall(notificationData.sessionId, notificationData.roomId)
        val client = matrixClientProvider.getOrRestore(callType.sessionId).getOrNull() ?: run {
            Timber.tag(tag).d("Couldn't find session for incoming call: %s", notificationData.eventId)
            return@launch
        }
        val room = client.getRoom(callType.roomId) ?: run {
            Timber.tag(tag).d("Couldn't find room for incoming call: %s", notificationData.eventId)
            return@launch
        }

        launch {
            room.subscribeToCallDecline(notificationData.eventId)
                .filter { decliner ->
                    Timber.tag(tag).d("Call event %s was declined by %s", notificationData.eventId, decliner)
                    decliner == client.sessionId
                }
                .collect {
                    removeRingingCall(
                        eventId = notificationData.eventId,
                        displayMissedCallNotification = false,
                    )
                }
        }

        launch {
            room.roomInfoFlow
                .map {
                    Timber.tag(tag).d("Has room call status changed for ringing call event=%s hasCall=%s", notificationData.eventId, it.hasRoomCall)
                    it.hasRoomCall to (callType.sessionId in it.activeRoomCallParticipants)
                }
                .distinctUntilChanged()
                .drop(1)
                .collect { (roomHasActiveCall, userIsInTheCall) ->
                    if (!roomHasActiveCall) {
                        removeRingingCall(
                            eventId = notificationData.eventId,
                            displayMissedCallNotification = true,
                        )
                    } else if (userIsInTheCall) {
                        removeRingingCall(
                            eventId = notificationData.eventId,
                            displayMissedCallNotification = false,
                        )
                    }
                }
        }
    }

    private suspend fun removeRingingCall(
        eventId: io.element.android.libraries.matrix.api.core.EventId,
        displayMissedCallNotification: Boolean,
    ) = mutex.withLock {
        removeRingingCallLocked(eventId = eventId, displayMissedCallNotification = displayMissedCallNotification)
    }

    /**
     * 统一从振铃列表中移除某一路来电，并同步清理它独有的超时/观察任务。
     * 这里故意不直接影响 [activeCall]，因为用户可能已经接起另一通电话，二者需要独立存在。
     */
    private fun removeRingingCallLocked(
        eventId: io.element.android.libraries.matrix.api.core.EventId,
        displayMissedCallNotification: Boolean,
    ) {
        val removedCall = ringingCalls.value.firstOrNull { it.eventId == eventId } ?: return

        ringingCalls.value = ringingCalls.value.filterNot { it.eventId == eventId }
        Timber.tag(incomingCallTraceTag).w(
            "ActiveCallManager removed ringing call eventId=%s remainingCount=%s displayMissed=%s",
            eventId,
            ringingCalls.value.size,
            displayMissedCallNotification,
        )
        ringingTimeoutJobs.remove(eventId.value)?.cancel()
        ringingObservationJobs.remove(eventId.value)?.cancel()
        updateRingingCallFlagLocked()
        refreshWakeLockLocked()

        cancelIncomingCallNotification(removedCall)
        if (displayMissedCallNotification) {
            displayMissedCallNotification(removedCall)
        }
    }

    private fun getIncomingCallNotificationId(notificationData: CallNotificationData): Int {
        return NotificationIdProvider.getIncomingCallNotificationId(notificationData.sessionId, notificationData.eventId)
    }

    private fun updateRingingCallFlagLocked() {
        val hasRingingCall = ringingCalls.value.isNotEmpty()
        appForegroundStateService.updateHasRingingCall(hasRingingCall)
        Timber.tag(incomingCallTraceTag).i(
            "ActiveCallManager updated hasRingingCall=%s ringingCount=%s",
            hasRingingCall,
            ringingCalls.value.size,
        )
    }

    private fun refreshWakeLockLocked() {
        val maxRemainingRingDuration = ringingCalls.value
            .map { it.expirationTimestamp - systemClock.epochMillis() }
            .maxOrNull()
            ?.coerceAtLeast(0L)
            ?: 0L
        if (maxRemainingRingDuration <= 0L) {
            releaseWakeLockIfHeld()
            return
        }
        activeWakeLock?.let { wakeLock ->
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
            Timber.tag(tag).d("Refreshing partial wakelock for %s ms", maxRemainingRingDuration)
            wakeLock.acquire(maxRemainingRingDuration)
        }
    }

    private fun releaseWakeLockIfHeld() {
        if (activeWakeLock?.isHeld == true) {
            Timber.tag(tag).d("Releasing partial wakelock")
            activeWakeLock.release()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeRingingCall() {
        // 多路振铃改造后，每一路来电都会在注册时单独启动观察协程。
        // 这里保留 init 调用点，是为了让整体生命周期入口不变，避免未来误删初始化逻辑。
    }

    private fun observeCurrentCall() {
        activeCall
            .onEach { value ->
                if (value == null) {
                    defaultCurrentCallService.onCallEnded()
                } else {
                    when (value.callState) {
                        is CallState.Ringing -> {
                            // Nothing to do
                        }
                        is CallState.InCall -> {
                            when (val callType = value.callType) {
                                is CallType.ExternalUrl -> defaultCurrentCallService.onCallStarted(CurrentCall.ExternalUrl(callType.url))
                                is CallType.RoomCall -> defaultCurrentCallService.onCallStarted(CurrentCall.RoomCall(callType.roomId))
                            }
                        }
                    }
                }
            }
            .launchIn(coroutineScope)
    }
}

private fun CallType.isSameCallIdentity(other: CallType): Boolean {
    return when {
        this is CallType.RoomCall && other is CallType.RoomCall -> sessionId == other.sessionId && roomId == other.roomId
        else -> this == other
    }
}

private fun CallType.matchesIncomingCall(notificationData: CallNotificationData): Boolean {
    return this is CallType.RoomCall &&
        sessionId == notificationData.sessionId &&
        roomId == notificationData.roomId
}

data class ActiveCall(
    val callType: CallType,
    val callState: CallState,
)

sealed interface CallState {
    data class Ringing(val notificationData: CallNotificationData) : CallState

    data object InCall : CallState
}
