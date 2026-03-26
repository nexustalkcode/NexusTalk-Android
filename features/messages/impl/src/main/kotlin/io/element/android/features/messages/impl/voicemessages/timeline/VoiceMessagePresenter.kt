/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.voicemessages.timeline

import androidx.compose.runtime.Composable
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap
import io.element.android.features.messages.impl.timeline.di.TimelineItemEventContentKey
import io.element.android.features.messages.impl.timeline.di.TimelineItemPresenterFactory
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemVoiceContent
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.voiceplayer.api.VoiceMessagePresenterFactory
import io.element.android.libraries.voiceplayer.api.VoiceMessageState

/**
 * 语音消息 Presenter 模块绑定接口
 *
 * 用于将语音消息 Presenter 工厂绑定到时间线项事件内容映射中。
 * 通过@TimelineItemEventContentKey注解，将TimelineItemVoiceContent类型
 * 映射到对应的VoiceMessagePresenterFactory。
 *
 * @see VoiceMessagePresenter 语音消息Presenter
 * @see TimelineItemVoiceContent 语音消息内容
 * @see TimelineItemPresenterFactory 时间线项Presenter工厂
 */
@BindingContainer
@ContributesTo(RoomScope::class)
interface VoiceMessagePresenterModule {
    /**
     * 绑定语音消息 Presenter 工厂
     *
     * @param factory 语音消息 Presenter 工厂
     * @return TimelineItemPresenterFactory
     */
    @Binds
    @IntoMap
    @TimelineItemEventContentKey(TimelineItemVoiceContent::class)
    fun bindVoiceMessagePresenterFactory(factory: VoiceMessagePresenter.Factory): TimelineItemPresenterFactory<*, *>
}

/**
 * 语音消息时间线Presenter
 *
 * 负责处理时间线中语音消息播放的业务逻辑和状态管理。
 * 封装语音消息播放器，提供语音消息的播放状态。
 *
 * @property voiceMessagePresenterFactory 语音消息Presenter工厂
 * @property content 语音消息内容
 */
@AssistedInject
class VoiceMessagePresenter(
    voiceMessagePresenterFactory: VoiceMessagePresenterFactory,
    @Assisted private val content: TimelineItemVoiceContent,
) : Presenter<VoiceMessageState> {
    /**
     * 工厂接口
     */
    @AssistedFactory
    fun interface Factory : TimelineItemPresenterFactory<TimelineItemVoiceContent, VoiceMessageState> {
        /**
         * 创建 Presenter 实例
         *
         * @param content 语音消息内容
         * @return VoiceMessagePresenter 实例
         */
        override fun create(content: TimelineItemVoiceContent): VoiceMessagePresenter
    }

    private val presenter = voiceMessagePresenterFactory.createVoiceMessagePresenter(
        eventId = content.eventId,
        mediaSource = content.mediaSource,
        mimeType = content.mimeType,
        filename = content.filename,
        duration = content.duration,
    )

    /**
     * 生成界面状态
     *
     * @return VoiceMessageState 语音消息状态
     */
    @Composable
    override fun present(): VoiceMessageState {
        return presenter.present()
    }
}
