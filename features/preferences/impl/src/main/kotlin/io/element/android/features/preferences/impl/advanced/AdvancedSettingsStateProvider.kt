/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.advanced

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.media.MediaPreviewValue
import io.element.android.libraries.preferences.api.store.VideoCompressionPreset

/**
 * 高级设置状态提供者
 *
 * 用于在预览模式下提供高级设置页面的示例状态数据。
 *
 * @see AdvancedSettingsState 高级设置状态
 */
open class AdvancedSettingsStateProvider : PreviewParameterProvider<AdvancedSettingsState> {
    override val values: Sequence<AdvancedSettingsState>
        get() = sequenceOf(
            aAdvancedSettingsState(),
            aAdvancedSettingsState(isDeveloperModeEnabled = true),
            aAdvancedSettingsState(isSharePresenceEnabled = true),
            aAdvancedSettingsState(mediaOptimizationState = MediaOptimizationState.AllMedia(isEnabled = true)),
            aAdvancedSettingsState(hideInviteAvatars = true),
            aAdvancedSettingsState(timelineMediaPreviewValue = MediaPreviewValue.Off),
            aAdvancedSettingsState(setHideInviteAvatarsAction = AsyncAction.Loading),
            aAdvancedSettingsState(setTimelineMediaPreviewAction = AsyncAction.Loading),
            aAdvancedSettingsState(mediaOptimizationState = MediaOptimizationState.Split(
                compressImages = true,
                videoPreset = VideoCompressionPreset.HIGH,
            )),
        )
}

/**
 * 创建示例 AdvancedSettingsState 对象
 *
 * @param isDeveloperModeEnabled 是否启用开发者模式
 * @param isSharePresenceEnabled 是否启用分享在线状态
 * @param mediaOptimizationState 媒体优化状态
 * @param theme 主题选项
 * @param hideInviteAvatars 是否隐藏邀请中的头像
 * @param timelineMediaPreviewValue 时间线媒体预览值
 * @param setTimelineMediaPreviewAction 设置时间线媒体预览操作状态
 * @param setHideInviteAvatarsAction 设置隐藏邀请头像操作状态
 * @param eventSink 事件处理函数
 * @return AdvancedSettingsState 示例状态
 */
fun aAdvancedSettingsState(
    isDeveloperModeEnabled: Boolean = false,
    isSharePresenceEnabled: Boolean = false,
    mediaOptimizationState: MediaOptimizationState = MediaOptimizationState.AllMedia(isEnabled = false),
    theme: ThemeOption = ThemeOption.System,
    hideInviteAvatars: Boolean = false,
    timelineMediaPreviewValue: MediaPreviewValue = MediaPreviewValue.On,
    setTimelineMediaPreviewAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    setHideInviteAvatarsAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (AdvancedSettingsEvents) -> Unit = {},
) = AdvancedSettingsState(
    isDeveloperModeEnabled = isDeveloperModeEnabled,
    isSharePresenceEnabled = isSharePresenceEnabled,
    mediaOptimizationState = mediaOptimizationState,
    theme = theme,
    mediaPreviewConfigState = MediaPreviewConfigState(
        hideInviteAvatars = hideInviteAvatars,
        timelineMediaPreviewValue = timelineMediaPreviewValue,
        setTimelineMediaPreviewAction = setTimelineMediaPreviewAction,
        setHideInviteAvatarsAction = setHideInviteAvatarsAction
    ),
    eventSink = eventSink
)
