/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.api.timeline.voicemessages.composer

import androidx.compose.runtime.Stable
import io.element.android.libraries.textcomposer.model.VoiceMessageState

/**
 * 语音消息撰写器状态数据类
 *
 * @property voiceMessageState 语音消息状态
 * @property showPermissionRationaleDialog 是否显示权限理由对话框
 * @property showSendFailureDialog 是否显示发送失败对话框
 * @property keepScreenOn 是否保持屏幕常亮
 * @property eventSink 事件处理函数
 */
@Stable
data class VoiceMessageComposerState(
    val voiceMessageState: VoiceMessageState,
    val showPermissionRationaleDialog: Boolean,
    val showSendFailureDialog: Boolean,
    val keepScreenOn: Boolean,
    val eventSink: (VoiceMessageComposerEvent) -> Unit,
)
