/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import dev.zacsweers.metro.Inject
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.features.call.api.CallType
import io.element.android.features.call.api.ElementCallEntryPoint
import io.element.android.features.call.impl.R
import io.element.android.features.call.impl.di.CallBindings
import io.element.android.features.call.impl.notifications.CallNotificationData
import io.element.android.features.call.impl.notifications.hasSameRingingIdentityAs
import io.element.android.features.call.impl.utils.ActiveCallManager
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.libraries.architecture.bindings
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.designsystem.theme.ElementThemeApp
import io.element.android.libraries.di.annotations.AppCoroutineScope
import io.element.android.libraries.preferences.api.store.AppPreferencesStore
import io.element.android.libraries.push.api.notifications.NotificationCleaner
import io.element.android.libraries.push.api.notifications.NotificationIdProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 应用内全屏来电页。
 *
 * 该 Activity 会在用户位于应用前台时承接房间来电，
 * 同时与 [ActiveCallManager] 协调当前是否正在显示全屏 UI，
 * 以便通知 overlay、响铃控制和挂断逻辑保持一致。
 */
class IncomingCallActivity : AppCompatActivity() {
    companion object {
        private val incomingCallVibrationPattern = longArrayOf(0L, 1_000L, 1_000L)
        const val EXTRA_NOTIFICATION_DATA = "EXTRA_NOTIFICATION_DATA"
    }

    @Inject
    lateinit var elementCallEntryPoint: ElementCallEntryPoint

    @Inject
    lateinit var activeCallManager: ActiveCallManager

    @Inject
    lateinit var notificationCleaner: NotificationCleaner

    @Inject
    lateinit var appPreferencesStore: AppPreferencesStore

    @Inject
    lateinit var enterpriseService: EnterpriseService

    @Inject
    lateinit var buildMeta: BuildMeta

    @AppCoroutineScope
    @Inject lateinit var appCoroutineScope: CoroutineScope

    private var incomingCallMediaPlayer: MediaPlayer? = null
    private val audioManager: AudioManager? by lazy {
        getSystemService(AudioManager::class.java)
    }
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)
        }
    }

    /**
     * 初始化全屏来电页并接管当前来电通知。
     *
     * 优先使用 Intent 传入的 [CallNotificationData]，如果没有则退回到
     * [ActiveCallManager] 中当前仍处于响铃状态的来电记录。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindings<CallBindings>().inject(this)

        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val notificationData = intent?.let { IntentCompat.getParcelableExtra(it, EXTRA_NOTIFICATION_DATA, CallNotificationData::class.java) }
            ?: activeCallManager.ringingCalls.value.firstOrNull()
        if (notificationData != null) {
            dismissIncomingCallNotifications(notificationData)
            lifecycleScope.launch {
                activeCallManager.setIncomingCallUiVisible(true)
            }
            setContent {
                val colors by remember {
                    enterpriseService.semanticColorsFlow(sessionId = notificationData.sessionId)
                }.collectAsState(SemanticColorsLightDark.default)
                ElementThemeApp(
                    appPreferencesStore = appPreferencesStore,
                    compoundLight = colors.light,
                    compoundDark = colors.dark,
                    buildMeta = buildMeta,
                ) {
                    val ringingCalls by activeCallManager.ringingCalls.collectAsState()
                    val incomingCallOverlayState = remember(notificationData, ringingCalls) {
                        ringingCalls.toIncomingCallOverlayState(
                            excludedCall = notificationData,
                            onAnswerClick = ::onAnswer,
                            onDeclineClick = ::onOverlayCancel,
                        )
                    }
                    Box(modifier = Modifier.fillMaxSize()) {
                        IncomingCallScreen(
                            notificationData = notificationData,
                            onAnswer = ::onAnswer,
                            onCancel = { onCancel(notificationData) },
                        )
                        // 当前 Activity 已经用全屏页展示 notificationData，overlay 只显示其它并发来电，避免顶部重复当前来电信息。
                        IncomingCallOverlayHost(
                            state = incomingCallOverlayState,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        } else {
            finish()
            return
        }

        activeCallManager.ringingCalls
            .map { calls -> calls.any { it.hasSameRingingIdentityAs(notificationData) } }
            .filter { isStillRinging -> !isStillRinging }
            .onEach { finish() }
            .launchIn(lifecycleScope)
    }

    /**
     * 页面进入前台时显式标记来电 UI 可见，并启动声音与震动提醒。
     */
    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            activeCallManager.setIncomingCallUiVisible(true)
        }
        startIncomingCallAlert()
    }

    /**
     * 页面离开前台时停止提醒，并撤销「来电 UI 可见」标记。
     */
    override fun onStop() {
        stopIncomingCallAlert()
        lifecycleScope.launch {
            activeCallManager.setIncomingCallUiVisible(false)
        }
        super.onStop()
    }

    override fun onDestroy() {
        stopIncomingCallAlert()
        super.onDestroy()
    }

    /**
     * 清理与当前全屏来电重复的系统通知。
     *
     * @param notificationData 当前正在展示的来电数据。
     */
    private fun dismissIncomingCallNotifications(notificationData: CallNotificationData) {
        // 进入全屏来电页后，系统 heads-up 通知和同房间的“来电”消息通知都已经变成重复信息；这里同步清理避免压在页面顶部。
        NotificationManagerCompat.from(this).cancel(
            NotificationIdProvider.getIncomingCallNotificationId(notificationData.sessionId, notificationData.eventId)
        )
        notificationCleaner.clearMessagesForRoom(
            sessionId = notificationData.sessionId,
            roomId = notificationData.roomId,
        )
    }

    /**
     * 处理用户在全屏来电页点击接听的动作。
     *
     * @param notificationData 被接听的来电数据。
     */
    private fun onAnswer(notificationData: CallNotificationData) {
        Timber.tag("IncomingCallTrace").w(
            "IncomingCallActivity answer clicked eventId=%s roomId=%s",
            notificationData.eventId,
            notificationData.roomId,
        )
        stopIncomingCallAlert()
        elementCallEntryPoint.startCall(CallType.RoomCall(notificationData.sessionId, notificationData.roomId))
    }

    /**
     * 处理用户在全屏来电页点击挂断的动作。
     *
     * @param notificationData 被拒绝的来电数据。
     */
    private fun onCancel(notificationData: CallNotificationData) {
        Timber.tag("IncomingCallTrace").w(
            "IncomingCallActivity decline clicked eventId=%s roomId=%s",
            notificationData.eventId,
            notificationData.roomId,
        )
        stopIncomingCallAlert()
        hangUpIncomingCall(notificationData)
    }

    /**
     * 处理 overlay 中对并发来电的拒绝操作。
     *
     * 该分支不会关闭当前全屏来电页，只会挂断被点击的那一路来电。
     *
     * @param notificationData 需要挂断的并发来电数据。
     */
    private fun onOverlayCancel(notificationData: CallNotificationData) {
        Timber.tag("IncomingCallTrace").w(
            "IncomingCallActivity overlay decline clicked eventId=%s roomId=%s",
            notificationData.eventId,
            notificationData.roomId,
        )
        hangUpIncomingCall(notificationData)
    }

    /**
     * 异步执行来电挂断。
     *
     * @param notificationData 需要结束的来电数据。
     */
    private fun hangUpIncomingCall(notificationData: CallNotificationData) {
        appCoroutineScope.launch {
            activeCallManager.hangUpCall(
                callType = CallType.RoomCall(notificationData.sessionId, notificationData.roomId),
                notificationData = notificationData,
            )
        }
    }

    /**
     * 启动当前来电页的本地提醒能力。
     */
    private fun startIncomingCallAlert() {
        startIncomingCallSound()
        startIncomingCallVibration()
    }

    /**
     * 停止当前来电页已经启动的声音和震动提醒。
     */
    private fun stopIncomingCallAlert() {
        incomingCallMediaPlayer?.run {
            runCatching { stop() }
            release()
        }
        incomingCallMediaPlayer = null
        vibrator?.cancel()
    }

    private fun startIncomingCallSound() {
        if (incomingCallMediaPlayer != null) return
        val audioManager = audioManager ?: return
        val ringVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING)
        if (!shouldPlayIncomingCallSound(audioManager.ringerMode, ringVolume)) {
            // 全屏来电页使用自定义 MediaPlayer 播放铃声，因此这里需要在启动前主动对齐系统响铃模式，
            // 避免系统已经切到静音/振动或铃声音量为 0 时，页面仍然继续发声。
            Timber.tag("IncomingCallTrace").i(
                "Skip incoming call sound due to system ringer settings mode=%s ringVolume=%s",
                audioManager.ringerMode,
                ringVolume,
            )
            return
        }
        incomingCallMediaPlayer = runCatching {
            MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .build()
                )
                isLooping = true
                // 直接引用当前模块的 raw 资源，让 release 资源收缩能够静态识别该铃声仍然在使用中，
                // 避免像字符串拼装 android.resource Uri 那样被误判为“未引用”后从正式版产物中裁掉。
                resources.openRawResourceFd(R.raw.video_request).use { assetFileDescriptor ->
                    setDataSource(
                        assetFileDescriptor.fileDescriptor,
                        assetFileDescriptor.startOffset,
                        assetFileDescriptor.length,
                    )
                }
                prepare()
                start()
            }
        }.onFailure {
            Timber.e(it, "Failed to start incoming call sound")
        }.getOrNull()
    }

    private fun startIncomingCallVibration() {
        val vibrator = vibrator ?: return
        if (!vibrator.hasVibrator()) return
        vibrator.vibrate(VibrationEffect.createWaveform(incomingCallVibrationPattern, 0))
    }
}
