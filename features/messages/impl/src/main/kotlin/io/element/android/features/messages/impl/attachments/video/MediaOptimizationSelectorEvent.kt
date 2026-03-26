/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments.video

import io.element.android.libraries.preferences.api.store.VideoCompressionPreset

/**
 * 媒体优化选择器事件密封接口
 *
 * 定义媒体优化选择器界面的用户交互事件。
 *
 * 事件类型：
 * - [SelectImageOptimization]: 选择图片优化开关状态
 * - [SelectVideoPreset]: 选择视频压缩预设
 * - [OpenVideoPresetSelectorDialog]: 打开视频预设选择对话框
 * - [DismissVideoPresetSelectorDialog]: 关闭视频预设选择对话框
 */
sealed interface MediaOptimizationSelectorEvent {
    /**
     * 选择图片优化开关状态事件
     *
     * @property enabled 是否启用图片优化压缩
     */
    data class SelectImageOptimization(val enabled: Boolean) : MediaOptimizationSelectorEvent

    /**
     * 选择视频压缩预设事件
     *
     * @property preset 选中的视频压缩预设
     */
    data class SelectVideoPreset(val preset: VideoCompressionPreset) : MediaOptimizationSelectorEvent

    /**
     * 打开视频预设选择对话框事件
     *
     * 当用户点击视频质量选项时触发
     */
    data object OpenVideoPresetSelectorDialog : MediaOptimizationSelectorEvent

    /**
     * 关闭视频预设选择对话框事件
     *
     * 当用户取消或完成选择时触发
     */
    data object DismissVideoPresetSelectorDialog : MediaOptimizationSelectorEvent
}
