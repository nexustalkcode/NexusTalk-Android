/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.core.net.toUri
import io.element.android.features.messages.impl.attachments.Attachment
import io.element.android.features.messages.impl.attachments.video.MediaOptimizationSelectorState
import io.element.android.features.messages.impl.attachments.video.VideoUploadEstimation
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.core.mimetype.MimeTypes
import io.element.android.libraries.matrix.api.media.ImageInfo
import io.element.android.libraries.mediaupload.api.MediaUploadInfo
import io.element.android.libraries.mediaviewer.api.MediaInfo
import io.element.android.libraries.mediaviewer.api.aVideoMediaInfo
import io.element.android.libraries.mediaviewer.api.anImageMediaInfo
import io.element.android.libraries.mediaviewer.api.local.LocalMedia
import io.element.android.libraries.preferences.api.store.VideoCompressionPreset
import io.element.android.libraries.textcomposer.model.TextEditorState
import io.element.android.libraries.textcomposer.model.aTextEditorStateMarkdown
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.io.File

/**
 * 附件预览状态提供器
 *
 * 用于预览（Preview）功能的测试状态生成器。
 * 继承自 PreviewParameterProvider，为 Compose 预览提供多种状态变体。
 *
 * 使用方法：
 * 在 @Composable 函数上使用 @PreviewParameter 注解引入此提供器
 *
 * @example
 * @Preview
 * @Composable
 * fun MyPreview(@PreviewParameter(AttachmentsPreviewStateProvider::class) state: AttachmentsPreviewState) {
 *     // 使用 state 进行预览
 * }
 */
open class AttachmentsPreviewStateProvider : PreviewParameterProvider<AttachmentsPreviewState> {
    /**
     * 生成预览状态序列
     *
     * 包含多种场景的状态变体，用于全面测试UI表现：
     * - 空闲状态
     * - 处理中状态（不显示进度）
     * - 处理中状态（显示进度）
     * - 准备上传状态
     * - 上传中状态
     * - 失败状态
     * - 文件太大错误状态
     * - 视频附件状态
     * - 视频预设选择器对话框状态
     */
    override val values: Sequence<AttachmentsPreviewState>
        get() = sequenceOf(
            anAttachmentsPreviewState(),
            anAttachmentsPreviewState(
                sendActionState = SendActionState.Sending.Processing(displayProgress = false),
                textEditorState = aTextEditorStateMarkdown(
                    initialText = "This is a caption!"
                )
            ),
            anAttachmentsPreviewState(sendActionState = SendActionState.Sending.Processing(displayProgress = true)),
            anAttachmentsPreviewState(sendActionState = SendActionState.Sending.ReadyToUpload(aMediaUploadInfo())),
            anAttachmentsPreviewState(sendActionState = SendActionState.Sending.Uploading(aMediaUploadInfo())),
            anAttachmentsPreviewState(sendActionState = SendActionState.Failure(RuntimeException("error"), aMediaUploadInfo())),
            anAttachmentsPreviewState(displayFileTooLargeError = true),
            anAttachmentsPreviewState(
                mediaInfo = aVideoMediaInfo(),
                mediaOptimizationSelectorState = aMediaOptimisationSelectorState(
                    selectedVideoPreset = VideoCompressionPreset.STANDARD,
                    videoSizeEstimations = aVideoSizeEstimationList(),
                )
            ),
            anAttachmentsPreviewState(
                mediaInfo = aVideoMediaInfo(),
                mediaOptimizationSelectorState = aMediaOptimisationSelectorState(
                    videoSizeEstimations = aVideoSizeEstimationList(),
                    displayVideoPresetSelectorDialog = true,
                )
            ),
        )
}

/**
 * 创建附件预览状态的辅助函数
 *
 * 用于测试中快速创建 AttachmentsPreviewState 实例
 *
 * @param mediaInfo 媒体信息，默认为图片媒体信息
 * @param textEditorState 文本编辑器状态，默认为空的Markdown状态
 * @param sendActionState 发送操作状态，默认为空闲状态
 * @param mediaOptimizationSelectorState 媒体优化选择器状态
 * @param displayFileTooLargeError 是否显示文件太大错误
 * @return AttachmentsPreviewState 实例
 */
fun anAttachmentsPreviewState(
    mediaInfo: MediaInfo = anImageMediaInfo(),
    textEditorState: TextEditorState = aTextEditorStateMarkdown(),
    sendActionState: SendActionState = SendActionState.Idle,
    mediaOptimizationSelectorState: MediaOptimizationSelectorState = aMediaOptimisationSelectorState(),
    displayFileTooLargeError: Boolean = false,
) = AttachmentsPreviewState(
    attachment = Attachment.Media(
        localMedia = LocalMedia("file://path".toUri(), mediaInfo),
    ),
    sendActionState = sendActionState,
    textEditorState = textEditorState,
    mediaOptimizationSelectorState = mediaOptimizationSelectorState,
    displayFileTooLargeError = displayFileTooLargeError,
    eventSink = {}
)

/**
 * 创建媒体上传信息的辅助函数
 *
 * 用于测试中创建模拟的 MediaUploadInfo 实例
 *
 * @param filePath 文件路径
 * @param thumbnailFilePath 缩略图文件路径（可选）
 * @return MediaUploadInfo.Image 实例
 */
fun aMediaUploadInfo(
    filePath: String = "file://path",
    thumbnailFilePath: String? = null,
) = MediaUploadInfo.Image(
    file = File(filePath),
    imageInfo = ImageInfo(
        height = 100,
        width = 100,
        mimetype = MimeTypes.Jpeg,
        size = 1000,
        thumbnailInfo = null,
        thumbnailSource = null,
        blurhash = null,
    ),
    thumbnailFile = thumbnailFilePath?.let { File(it) },
)

/**
 * 创建媒体优化选择器状态的辅助函数
 *
 * 用于测试中创建模拟的 MediaOptimizationSelectorState 实例
 *
 * @param maxUploadSize 最大上传大小，默认为100MB
 * @param videoSizeEstimations 视频大小估算列表
 * @param isImageOptimizationEnabled 是否启用图片优化
 * @param selectedVideoPreset 选中的视频预设
 * @param displayMediaSelectorViews 是否显示媒体选择器视图
 * @param displayVideoPresetSelectorDialog 是否显示视频预设选择器对话框
 * @return MediaOptimizationSelectorState 实例
 */
fun aMediaOptimisationSelectorState(
    maxUploadSize: Long = 100 * 1024 * 1024,
    videoSizeEstimations: AsyncData<ImmutableList<VideoUploadEstimation>> = AsyncData.Success(persistentListOf()),
    isImageOptimizationEnabled: Boolean = true,
    selectedVideoPreset: VideoCompressionPreset = VideoCompressionPreset.STANDARD,
    displayMediaSelectorViews: Boolean = true,
    displayVideoPresetSelectorDialog: Boolean = false,
) = MediaOptimizationSelectorState(
    maxUploadSize = AsyncData.Success(maxUploadSize),
    videoSizeEstimations = videoSizeEstimations,
    isImageOptimizationEnabled = isImageOptimizationEnabled,
    selectedVideoPreset = selectedVideoPreset,
    displayMediaSelectorViews = displayMediaSelectorViews,
    displayVideoPresetSelectorDialog = displayVideoPresetSelectorDialog,
    eventSink = {},
)

/**
 * 创建视频大小估算列表的辅助函数
 *
 * 包含HIGH和STANDARD两种预设的估算数据，用于测试
 *
 * @return 包含视频大小估算的AsyncData
 */
internal fun aVideoSizeEstimationList(): AsyncData<ImmutableList<VideoUploadEstimation>> = AsyncData.Success(
    persistentListOf(
        VideoUploadEstimation(
            preset = VideoCompressionPreset.HIGH,
            sizeInBytes = 8_200_000L,
            canUpload = false,
        ),
        VideoUploadEstimation(
            preset = VideoCompressionPreset.STANDARD,
            sizeInBytes = 4_200_000L,
            canUpload = true,
        ),
    )
)
