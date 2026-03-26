/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments.video

import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.preferences.api.store.VideoCompressionPreset
import kotlinx.collections.immutable.ImmutableList

/**
 * 媒体优化选择器状态数据类
 *
 * 存储媒体优化选择器的所有状态信息。
 * 用于管理图片优化开关和视频压缩预设的选择状态。
 *
 * @property maxUploadSize 服务器允许的最大上传大小（异步加载）
 * @property videoSizeEstimations 各种视频压缩预设的估算文件大小（异步加载）
 * @property isImageOptimizationEnabled 是否启用图片优化压缩
 * @property selectedVideoPreset 当前选中的视频压缩预设
 * @property displayMediaSelectorViews 是否显示媒体优化选择器视图
 * @property displayVideoPresetSelectorDialog 是否显示视频预设选择对话框
 * @property eventSink 事件处理函数，用于将用户交互传递给Presenter
 */
data class MediaOptimizationSelectorState(
    val maxUploadSize: AsyncData<Long>,
    val videoSizeEstimations: AsyncData<ImmutableList<VideoUploadEstimation>>,
    val isImageOptimizationEnabled: Boolean?,
    val selectedVideoPreset: VideoCompressionPreset?,
    val displayMediaSelectorViews: Boolean?,
    val displayVideoPresetSelectorDialog: Boolean,
    val eventSink: (MediaOptimizationSelectorEvent) -> Unit
)

/**
 * 视频上传估算数据类
 *
 * 存储单个视频压缩预设的估算信息。
 *
 * @property preset 视频压缩预设类型
 * @property sizeInBytes 估算的输出文件大小（字节）
 * @property canUpload 是否可以在当前上传限制内上传
 */
data class VideoUploadEstimation(
    val preset: VideoCompressionPreset,
    val sizeInBytes: Long,
    val canUpload: Boolean,
)
