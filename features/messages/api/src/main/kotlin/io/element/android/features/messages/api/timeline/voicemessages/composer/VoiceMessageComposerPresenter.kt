/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.api.timeline.voicemessages.composer

import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.timeline.Timeline

/**
 * 语音消息撰写器Presenter函数接口
 *
 * 负责管理语音消息录制、播放和发送的业务逻辑。
 * 继承自 Presenter 接口，用于构建语音消息撰写器的UI状态。
 *
 * @see Presenter Presenter基类
 * @see VoiceMessageComposerState 语音消息撰写器状态
 * @see Timeline.Mode 时间线模式
 */
fun interface VoiceMessageComposerPresenter : Presenter<VoiceMessageComposerState> {
    /**
     * 语音消息撰写器工厂接口
     *
     * 用于创建不同时间线模式下的语音消息撰写器实例。
     */
    interface Factory {
        /**
         * 创建语音消息撰写器实例
         *
         * @param timelineMode 时间线模式（实时或离线）
         * @return 语音消息撰写器实例
         */
        fun create(timelineMode: Timeline.Mode): VoiceMessageComposerPresenter
    }
}
