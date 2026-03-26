/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.advanced

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import io.element.android.libraries.designsystem.components.preferences.DropdownOption
import io.element.android.libraries.preferences.api.store.VideoCompressionPreset
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 高级设置页面状态数据类
 *
 * @property isDeveloperModeEnabled 是否启用开发者模式
 * @property isSharePresenceEnabled 是否启用分享在线状态
 * @property mediaOptimizationState 媒体优化状态
 * @property theme 当前主题选项
 * @property mediaPreviewConfigState 媒体预览配置状态
 * @property eventSink 事件处理函数
 */
data class AdvancedSettingsState(
    val isDeveloperModeEnabled: Boolean,
    val isSharePresenceEnabled: Boolean,
    val mediaOptimizationState: MediaOptimizationState?,
    val theme: ThemeOption,
    val mediaPreviewConfigState: MediaPreviewConfigState,
    val eventSink: (AdvancedSettingsEvents) -> Unit
)

/**
 * 媒体优化状态密封接口
 */
sealed interface MediaOptimizationState {
    /** 统一媒体优化设置 */
    data class AllMedia(val isEnabled: Boolean) : MediaOptimizationState
    /** 分离媒体优化设置（图片和视频分开设置） */
    data class Split(
        val compressImages: Boolean,
        val videoPreset: VideoCompressionPreset,
    ) : MediaOptimizationState

    /** 是否应该压缩图片 */
    val shouldCompressImages: Boolean get() = when (this) {
        is AllMedia -> isEnabled
        is Split -> compressImages
    }
}

/**
 * 主题选项枚举
 *
 * 定义应用支持的主题模式：跟随系统、深色主题、浅色主题
 */
enum class ThemeOption : DropdownOption {
    /** 跟随系统主题 */
    System {
        @Composable
        @ReadOnlyComposable
        override fun getText(): String = stringResource(CommonStrings.common_system)
    },
    /** 深色主题 */
    Dark {
        @Composable
        @ReadOnlyComposable
        override fun getText(): String = stringResource(CommonStrings.common_dark)
    },
    /** 浅色主题 */
    Light {
        @Composable
        @ReadOnlyComposable
        override fun getText(): String = stringResource(CommonStrings.common_light)
    }
}
