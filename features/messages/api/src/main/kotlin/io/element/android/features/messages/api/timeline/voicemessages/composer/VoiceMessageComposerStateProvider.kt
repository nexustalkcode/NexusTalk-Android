/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.api.timeline.voicemessages.composer

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.designsystem.components.media.WaveFormSamples
import io.element.android.libraries.textcomposer.model.VoiceMessageState
import kotlin.time.Duration.Companion.seconds

/**
 * 语音消息撰写器状态预览参数提供器
 *
 * 用于在预览环境中生成测试用的语音消息撰写器状态数据。
 * 继承自 PreviewParameterProvider，支持 Compose 预览功能。
 *
 * @see PreviewParameterProvider 预览参数提供器基类
 * @see VoiceMessageComposerState 语音消息撰写器状态
 */
open class VoiceMessageComposerStateProvider : PreviewParameterProvider<VoiceMessageComposerState> {
    override val values: Sequence<VoiceMessageComposerState>
        get() = sequenceOf(
            aVoiceMessageComposerState(voiceMessageState = VoiceMessageState.Recording(duration = 61.seconds, levels = WaveFormSamples.allRangeWaveForm)),
        )
}

/**
 * 创建测试用语音消息撰写器状态的辅助函数
 *
 * @param voiceMessageState 语音消息状态，默认为空闲状态
 * @param keepScreenOn 是否保持屏幕常亮，默认为 false
 * @param showPermissionRationaleDialog 是否显示权限理由对话框，默认为 false
 * @param showSendFailureDialog 是否显示发送失败对话框，默认为 false
 * @return 配置好的 VoiceMessageComposerState 实例
 */
fun aVoiceMessageComposerState(
    voiceMessageState: VoiceMessageState = VoiceMessageState.Idle,
    keepScreenOn: Boolean = false,
    showPermissionRationaleDialog: Boolean = false,
    showSendFailureDialog: Boolean = false,
) = VoiceMessageComposerState(
    voiceMessageState = voiceMessageState,
    showPermissionRationaleDialog = showPermissionRationaleDialog,
    showSendFailureDialog = showSendFailureDialog,
    keepScreenOn = keepScreenOn,
    eventSink = {},
)

/**
 * 创建测试用语音消息预览状态的辅助函数
 *
 * 用于生成预览环境中语音消息的播放状态数据。
 *
 * @return 预览状态的 VoiceMessageState.Preview 实例
 */
fun aVoiceMessagePreviewState() = VoiceMessageState.Preview(
    isSending = false,
    isPlaying = false,
    showCursor = false,
    playbackProgress = 0f,
    time = 10.seconds,
    waveform = WaveFormSamples.realisticWaveForm,
)
