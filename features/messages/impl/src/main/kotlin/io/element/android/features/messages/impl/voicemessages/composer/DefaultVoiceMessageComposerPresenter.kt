/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.voicemessages.composer

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import im.vector.app.features.analytics.plan.Composer
import io.element.android.features.messages.api.MessageComposerContext
import io.element.android.features.messages.api.timeline.voicemessages.composer.VoiceMessageComposerEvent
import io.element.android.features.messages.api.timeline.voicemessages.composer.VoiceMessageComposerPresenter
import io.element.android.features.messages.api.timeline.voicemessages.composer.VoiceMessageComposerState
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.mediaupload.api.MediaSenderFactory
import io.element.android.libraries.permissions.api.PermissionsEvent
import io.element.android.libraries.permissions.api.PermissionsPresenter
import io.element.android.libraries.textcomposer.model.VoiceMessagePlayerEvent
import io.element.android.libraries.textcomposer.model.VoiceMessageRecorderEvent
import io.element.android.libraries.textcomposer.model.VoiceMessageState
import io.element.android.libraries.voiceplayer.api.VoiceMessageException
import io.element.android.libraries.voicerecorder.api.VoiceRecorder
import io.element.android.libraries.voicerecorder.api.VoiceRecorderState
import io.element.android.services.analytics.api.AnalyticsService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * 默认语音消息Composer Presenter
 *
 * 负责处理语音消息录制和发送的业务逻辑和状态管理。
 * 管理录音权限、录制状态、播放状态和消息发送流程。
 *
 * @property sessionCoroutineScope 会话级别的协程作用域
 * @property timelineMode 时间线模式
 * @property voiceRecorder 语音录制器
 * @property analyticsService 分析服务
 * @property mediaSenderFactory 媒体发送器工厂
 * @property player 语音消息播放器
 * @property messageComposerContext 消息Composer上下文
 * @property permissionsPresenterFactory 权限Presenter工厂
 */
@AssistedInject
class DefaultVoiceMessageComposerPresenter(
    @SessionCoroutineScope private val sessionCoroutineScope: CoroutineScope,
    @Assisted private val timelineMode: Timeline.Mode,
    private val voiceRecorder: VoiceRecorder,
    private val analyticsService: AnalyticsService,
    mediaSenderFactory: MediaSenderFactory,
    private val player: VoiceMessageComposerPlayer,
    private val messageComposerContext: MessageComposerContext,
    permissionsPresenterFactory: PermissionsPresenter.Factory
) : VoiceMessageComposerPresenter {
    /**
     * 默认语音消息Composer Presenter工厂接口
     *
     * 用于创建DefaultVoiceMessageComposerPresenter实例。
     */
    @ContributesBinding(RoomScope::class)
    @AssistedFactory
    interface Factory : VoiceMessageComposerPresenter.Factory {
        /**
         * 创建 Presenter 实例
         *
         * @param timelineMode 时间线模式
         * @return DefaultVoiceMessageComposerPresenter 实例
         */
        override fun create(timelineMode: Timeline.Mode): DefaultVoiceMessageComposerPresenter
    }

    private val permissionsPresenter = permissionsPresenterFactory.create(Manifest.permission.RECORD_AUDIO)
    private val mediaSender = mediaSenderFactory.create(timelineMode)

    /**
     * 生成界面状态
     *
     * @return VoiceMessageComposerState 语音消息Composer状态
     */
    @Composable
    override fun present(): VoiceMessageComposerState {
        val localCoroutineScope = rememberCoroutineScope()
        val recorderState by voiceRecorder.state.collectAsState(initial = VoiceRecorderState.Idle)
        val playerState by player.state.collectAsState(initial = VoiceMessageComposerPlayer.State.Initial)
        val keepScreenOn by remember { derivedStateOf { recorderState is VoiceRecorderState.Recording } }

        val permissionState = permissionsPresenter.present()
        var isSending by remember { mutableStateOf(false) }
        var showSendFailureDialog by remember { mutableStateOf(false) }

        LaunchedEffect(recorderState) {
            val recording = recorderState as? VoiceRecorderState.Finished
                ?: return@LaunchedEffect
            player.setMedia(recording.file.path)
        }

        /**
         * 处理生命周期事件
         *
         * @param event 生命周期事件
         */
        fun handleLifecycleEvent(event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    sessionCoroutineScope.finishRecording()
                    player.pause()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    sessionCoroutineScope.cancelRecording()
                }
                else -> {}
            }
        }

        /**
         * 处理语音消息录制器事件
         *
         * @param event 录制器事件
         */
        fun handleVoiceMessageRecorderEvent(event: VoiceMessageRecorderEvent) {
            when (event) {
                VoiceMessageRecorderEvent.Start -> {
                    Timber.v("Voice message record button pressed")
                    when {
                        permissionState.permissionGranted -> {
                            localCoroutineScope.startRecording()
                        }
                        else -> {
                            Timber.i("Voice message permission needed")
                            permissionState.eventSink(PermissionsEvent.RequestPermissions)
                        }
                    }
                }
                VoiceMessageRecorderEvent.Stop -> {
                    Timber.v("Voice message stop button pressed")
                    localCoroutineScope.finishRecording()
                }
                VoiceMessageRecorderEvent.Cancel -> {
                    Timber.v("Voice message cancel button tapped")
                    localCoroutineScope.cancelRecording()
                }
            }
        }

        /**
         * 处理语音消息播放器事件
         *
         * @param event 播放器事件
         */
        fun handleVoiceMessagePlayerEvent(event: VoiceMessagePlayerEvent) {
            localCoroutineScope.launch {
                when (event) {
                    VoiceMessagePlayerEvent.Play -> player.play()
                    VoiceMessagePlayerEvent.Pause -> player.pause()
                    is VoiceMessagePlayerEvent.Seek -> player.seek(event.position)
                }
            }
        }

        /** 发送语音消息 */
        fun sendVoiceMessage() {
            val finishedState = recorderState as? VoiceRecorderState.Finished
            if (finishedState == null) {
                val exception = VoiceMessageException.FileException("No file to send")
                analyticsService.trackError(exception)
                Timber.e(exception)
                return
            }
            if (isSending) {
                return
            }
            isSending = true
            player.pause()
            analyticsService.captureComposerEvent()
            sessionCoroutineScope.launch {
                val result = sendMessage(
                    file = finishedState.file,
                    mimeType = finishedState.mimeType,
                    waveform = finishedState.waveform,
                )
                if (result.isFailure) {
                    showSendFailureDialog = true
                }
            }.invokeOnCompletion {
                isSending = false
            }
        }

        /**
         * 处理语音消息Composer事件
         *
         * @param event 语音消息Composer事件
         */
        fun handleEvent(event: VoiceMessageComposerEvent) {
            when (event) {
                is VoiceMessageComposerEvent.RecorderEvent -> handleVoiceMessageRecorderEvent(event.recorderEvent)
                is VoiceMessageComposerEvent.PlayerEvent -> handleVoiceMessagePlayerEvent(event.playerEvent)
                is VoiceMessageComposerEvent.SendVoiceMessage -> localCoroutineScope.launch {
                    sendVoiceMessage()
                }
                VoiceMessageComposerEvent.DeleteVoiceMessage -> {
                    player.pause()
                    localCoroutineScope.deleteRecording()
                }
                VoiceMessageComposerEvent.DismissPermissionsRationale -> {
                    permissionState.eventSink(PermissionsEvent.CloseDialog)
                }
                VoiceMessageComposerEvent.AcceptPermissionRationale -> {
                    permissionState.eventSink(PermissionsEvent.OpenSystemSettingAndCloseDialog)
                }
                is VoiceMessageComposerEvent.LifecycleEvent -> handleLifecycleEvent(event.event)
                VoiceMessageComposerEvent.DismissSendFailureDialog -> {
                    showSendFailureDialog = false
                }
            }
        }

        return VoiceMessageComposerState(
            voiceMessageState = when (val state = recorderState) {
                is VoiceRecorderState.Recording -> VoiceMessageState.Recording(
                    duration = state.elapsedTime,
                    levels = state.levels
                        // 只保留最后128个样本用于显示，否则可能导致崩溃
                        .takeLast(128)
                        .toImmutableList(),
                )
                is VoiceRecorderState.Finished ->
                    previewState(
                        playerState = playerState,
                        recorderState = recorderState,
                        isSending = isSending
                    )
                else -> VoiceMessageState.Idle
            },
            showPermissionRationaleDialog = permissionState.showDialog,
            showSendFailureDialog = showSendFailureDialog,
            keepScreenOn = keepScreenOn,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 预览状态计算
     *
     * @param playerState 播放器状态
     * @param recorderState 录制器状态
     * @param isSending 是否正在发送
     * @return VoiceMessageState 语音消息状态
     */
    @Composable
    private fun previewState(
        playerState: VoiceMessageComposerPlayer.State,
        recorderState: VoiceRecorderState,
        isSending: Boolean,
    ): VoiceMessageState {
        val showCursor by remember(playerState.isStopped, isSending) { derivedStateOf { !playerState.isStopped && !isSending } }
        val playerTime by remember(playerState, recorderState) { derivedStateOf { displayTime(playerState, recorderState) } }
        val waveform by remember(recorderState) { derivedStateOf { recorderState.finishedWaveform() } }

        return VoiceMessageState.Preview(
            isSending = isSending,
            isPlaying = playerState.isPlaying,
            showCursor = showCursor,
            playbackProgress = playerState.progress,
            time = playerTime,
            waveform = waveform,
        )
    }

    /** 开始录制 */
    private fun CoroutineScope.startRecording() = launch {
        try {
            voiceRecorder.startRecord()
        } catch (e: SecurityException) {
            Timber.e(e, "Voice message error")
            analyticsService.trackError(VoiceMessageException.PermissionMissing("Expected permission to record but none", e))
        }
    }

    /** 结束录制 */
    private fun CoroutineScope.finishRecording() = launch {
        voiceRecorder.stopRecord()
    }

    /** 取消录制 */
    private fun CoroutineScope.cancelRecording() = launch {
        voiceRecorder.stopRecord(cancelled = true)
    }

    /** 删除录制 */
    private fun CoroutineScope.deleteRecording() = launch {
        voiceRecorder.deleteRecording()
    }

    /**
     * 发送语音消息
     *
     * @param file 语音文件
     * @param mimeType MIME类型
     * @param waveform 波形数据
     * @return Result<Unit> 发送结果
     */
    private suspend fun sendMessage(
        file: File,
        mimeType: String,
        waveform: List<Float>,
    ): Result<Unit> {
        val result = mediaSender.sendVoiceMessage(
            uri = file.toUri(),
            mimeType = mimeType,
            waveForm = waveform,
        )

        if (result.isFailure) {
            Timber.e(result.exceptionOrNull(), "Voice message error")
            return result
        }

        voiceRecorder.deleteRecording()

        return result
    }

    /** 捕获Composer事件用于分析 */
    private fun AnalyticsService.captureComposerEvent() =
        capture(
            Composer(
                inThread = messageComposerContext.composerMode.inThread,
                isEditing = messageComposerContext.composerMode.isEditing,
                isReply = messageComposerContext.composerMode.isReply,
                messageType = Composer.MessageType.VoiceMessage,
            )
        )
}

/** 从录制器状态中获取波形数据 */
private fun VoiceRecorderState.finishedWaveform(): ImmutableList<Float> =
    (this as? VoiceRecorderState.Finished)
        ?.waveform
        .orEmpty()
        .toImmutableList()

/**
 * 根据播放器状态显示时间
 *
 * 显示当前播放位置或总时长。
 *
 * @param playerState 播放器状态
 * @param recording 录制器状态
 * @return Duration 显示的时间
 */
private fun displayTime(
    playerState: VoiceMessageComposerPlayer.State,
    recording: VoiceRecorderState
): Duration = when {
    !playerState.isStopped ->
        playerState.currentPosition.milliseconds
    recording is VoiceRecorderState.Finished ->
        recording.duration
    else ->
        0.milliseconds
}
