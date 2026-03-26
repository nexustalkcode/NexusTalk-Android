/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.api.timeline.voicemessages.composer

import androidx.lifecycle.Lifecycle
import io.element.android.libraries.textcomposer.model.VoiceMessagePlayerEvent
import io.element.android.libraries.textcomposer.model.VoiceMessageRecorderEvent

/**
 * 语音消息撰写器事件密封接口
 *
 * 定义语音消息录制和播放过程中的各种事件类型。
 * 包括录制事件、播放事件、发送、删除、权限管理等操作。
 *
 * @see VoiceMessageRecorderEvent 录音事件
 * @see VoiceMessagePlayerEvent 播放事件
 * @see Lifecycle.Event 生命周期事件
 */
sealed interface VoiceMessageComposerEvent {
    /**
     * 录音器事件
     *
     * @property recorderEvent 录音事件详情
     */
    data class RecorderEvent(
        val recorderEvent: VoiceMessageRecorderEvent
    ) : VoiceMessageComposerEvent

    /**
     * 播放器事件
     *
     * @property playerEvent 播放事件详情
     */
    data class PlayerEvent(
        val playerEvent: VoiceMessagePlayerEvent,
    ) : VoiceMessageComposerEvent

    /** 发送语音消息事件 */
    data object SendVoiceMessage : VoiceMessageComposerEvent

    /** 删除语音消息事件 */
    data object DeleteVoiceMessage : VoiceMessageComposerEvent

    /** 接受权限说明对话框事件 */
    data object AcceptPermissionRationale : VoiceMessageComposerEvent

    /** 关闭权限说明对话框事件 */
    data object DismissPermissionsRationale : VoiceMessageComposerEvent

    /**
     * 生命周期事件
     *
     * @property event 生命周期事件类型
     */
    data class LifecycleEvent(val event: Lifecycle.Event) : VoiceMessageComposerEvent

    /** 关闭发送失败对话框事件 */
    data object DismissSendFailureDialog : VoiceMessageComposerEvent
}
