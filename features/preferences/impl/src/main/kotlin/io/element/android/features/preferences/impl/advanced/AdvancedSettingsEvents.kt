/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.advanced

import io.element.android.libraries.matrix.api.media.MediaPreviewValue
import io.element.android.libraries.preferences.api.store.VideoCompressionPreset

/**
 * 高级设置事件密封接口
 *
 * 定义高级设置页面中可能发生的各种用户交互事件。
 */
sealed interface AdvancedSettingsEvents {
    /** 设置开发者模式启用状态 */
    data class SetDeveloperModeEnabled(val enabled: Boolean) : AdvancedSettingsEvents
    /** 设置分享在线状态启用状态 */
    data class SetSharePresenceEnabled(val enabled: Boolean) : AdvancedSettingsEvents
    /** 设置媒体压缩启用状态 */
    data class SetCompressMedia(val compress: Boolean) : AdvancedSettingsEvents
    /** 设置图片压缩启用状态 */
    data class SetCompressImages(val compress: Boolean) : AdvancedSettingsEvents
    /** 设置视频上传质量 */
    data class SetVideoUploadQuality(val videoPreset: VideoCompressionPreset) : AdvancedSettingsEvents
    /** 设置主题 */
    data class SetTheme(val theme: ThemeOption) : AdvancedSettingsEvents
    /** 设置时间线媒体预览值 */
    data class SetTimelineMediaPreviewValue(val value: MediaPreviewValue) : AdvancedSettingsEvents
    /** 设置隐藏邀请中的头像 */
    data class SetHideInviteAvatars(val value: Boolean) : AdvancedSettingsEvents
}
