/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments.preview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.messages.impl.R
import io.element.android.features.messages.impl.attachments.Attachment
import io.element.android.features.messages.impl.attachments.preview.error.sendAttachmentError
import io.element.android.features.messages.impl.attachments.video.MediaOptimizationSelectorEvent
import io.element.android.features.messages.impl.attachments.video.MediaOptimizationSelectorState
import io.element.android.features.messages.impl.attachments.video.VideoUploadEstimation
import io.element.android.libraries.core.bool.orFalse
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeImage
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeVideo
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.ProgressDialogType
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.dialogs.AlertDialog
import io.element.android.libraries.designsystem.components.dialogs.ListDialog
import io.element.android.libraries.designsystem.components.dialogs.RetryDialog
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.modifiers.niceClickable
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.ElementPreviewDark
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Switch
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.designsystem.utils.CommonDrawables
import io.element.android.libraries.mediaviewer.api.local.LocalMedia
import io.element.android.libraries.mediaviewer.api.local.LocalMediaRenderer
import io.element.android.libraries.preferences.api.store.VideoCompressionPreset
import io.element.android.libraries.textcomposer.TextComposer
import io.element.android.libraries.textcomposer.model.MessageComposerMode
import io.element.android.libraries.textcomposer.model.VoiceMessageState
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.libraries.ui.utils.formatter.rememberFileSizeFormatter
import io.element.android.wysiwyg.display.TextDisplay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 附件预览视图
 *
 * 附件预览界面的主要Composable函数。
 * 负责渲染附件预览的UI，包括媒体显示、优化选项和发送控制。
 *
 * @param state 附件预览状态，包含所有渲染所需数据
 * @param localMediaRenderer 本地媒体渲染器，用于显示媒体内容
 * @param modifier 视图修饰符，用于配置布局和样式
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentsPreviewView(
    state: AttachmentsPreviewState,
    localMediaRenderer: LocalMediaRenderer,
    modifier: Modifier = Modifier,
) {
    /**
     * 发送附件事件
     * 将发送操作事件发送到状态处理流程
     */
    fun postSendAttachment() {
        state.eventSink(AttachmentsPreviewEvents.SendAttachment)
    }

    /**
     * 取消并关闭事件
     * 将取消操作事件发送到状态处理流程
     */
    fun postCancel() {
        state.eventSink(AttachmentsPreviewEvents.CancelAndDismiss)
    }

    /**
     * 清除发送状态事件
     * 将清除状态操作事件发送到状态处理流程
     */
    fun postClearSendState() {
        state.eventSink(AttachmentsPreviewEvents.CancelAndClearSendState)
    }

    // 处理返回键事件
    BackHandler(enabled = state.sendActionState !is SendActionState.Sending.Uploading && state.sendActionState !is SendActionState.Done) {
        postCancel()
    }

    // 脚手架布局
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(
                        imageVector = CompoundIcons.Close(),
                        onClick = ::postCancel,
                    )
                },
                title = {},
            )
        }
    ) { paddingValues ->
        AttachmentPreviewContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            localMediaRenderer = localMediaRenderer,
            onSendClick = ::postSendAttachment,
        )
    }
    // 发送状态视图，显示处理/上传/错误对话框
    AttachmentSendStateView(
        sendActionState = state.sendActionState,
        onDismissClick = ::postClearSendState,
        onRetryClick = ::postSendAttachment
    )
}

/**
 * 发送状态视图
 *
 * 根据发送操作的不同状态显示相应的对话框。
 * 处理进度显示、上传状态和错误提示。
 *
 * @param sendActionState 发送操作状态
 * @param onDismissClick 关闭对话框的回调
 * @param onRetryClick 重试发送的回调
 */
@Composable
private fun AttachmentSendStateView(
    sendActionState: SendActionState,
    onDismissClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    when (sendActionState) {
        // 处理中状态
        is SendActionState.Sending.Processing -> {
            if (sendActionState.displayProgress) {
                ProgressDialog(
                    type = ProgressDialogType.Indeterminate,
                    text = stringResource(CommonStrings.common_preparing),
                    showCancelButton = true,
                    onDismissRequest = onDismissClick,
                )
            }
        }
        // 上传中状态
        is SendActionState.Sending.Uploading -> {
            ProgressDialog(
                type = ProgressDialogType.Indeterminate,
                text = stringResource(id = CommonStrings.common_sending),
                showCancelButton = true,
                onDismissRequest = onDismissClick,
            )
        }
        // 失败状态，显示重试对话框
        is SendActionState.Failure -> {
            RetryDialog(
                content = stringResource(sendAttachmentError(sendActionState.error)),
                onDismiss = onDismissClick,
                onRetry = onRetryClick
            )
        }
        else -> Unit
    }
}

/**
 * 附件预览内容区域
 *
 * 包含媒体预览视图、图片/视频优化选择器和底部操作栏。
 *
 * @param state 附件预览状态
 * @param localMediaRenderer 本地媒体渲染器
 * @param onSendClick 发送按钮点击回调
 * @param modifier 视图修饰符
 */
@Composable
private fun AttachmentPreviewContent(
    state: AttachmentsPreviewState,
    localMediaRenderer: LocalMediaRenderer,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        // 媒体预览区域
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (val attachment = state.attachment) {
                is Attachment.Media -> {
                    localMediaRenderer.Render(attachment.localMedia)
                }
            }
        }

        // 根据媒体类型显示相应的优化选择器
        val mimeType = (state.attachment as? Attachment.Media)?.localMedia?.info?.mimeType
        if (mimeType?.isMimeTypeImage() == true) {
            ImageOptimizationSelector(state.mediaOptimizationSelectorState)
        } else if (mimeType?.isMimeTypeVideo() == true) {
            VideoPresetSelector(state = state.mediaOptimizationSelectorState)
        }

        // 文件太大错误提示对话框
        val sizeFormatter = rememberFileSizeFormatter()
        if (state.displayFileTooLargeError) {
            val maxFileUploadSize = state.mediaOptimizationSelectorState.maxUploadSize.dataOrNull()
            if (maxFileUploadSize != null) {
                val content = stringResource(CommonStrings.dialog_file_too_large_to_upload_subtitle, sizeFormatter.format(maxFileUploadSize, true))
                AlertDialog(
                    title = stringResource(CommonStrings.dialog_file_too_large_to_upload_title),
                    content = content,
                    onDismiss = { state.eventSink(AttachmentsPreviewEvents.CancelAndDismiss) },
                )
            }
        }

        // 底部操作栏
        AttachmentsPreviewBottomActions(
            state = state,
            onSendClick = onSendClick,
            modifier = Modifier
                .fillMaxWidth()
                .background(ElementTheme.colors.bgCanvasDefault)
                .height(IntrinsicSize.Min)
                .imePadding(),
        )
    }
}

/**
 * 图片优化选择器
 *
 * 显示图片质量优化选项的开关组件。
 * 允许用户选择是否启用图片压缩优化。
 *
 * @param state 媒体优化选择器状态
 */
@Composable
private fun ImageOptimizationSelector(state: MediaOptimizationSelectorState) {
    if (state.displayMediaSelectorViews == true) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .niceClickable {
                    state.isImageOptimizationEnabled?.let { value ->
                        state.eventSink(MediaOptimizationSelectorEvent.SelectImageOptimization(!value))
                    }
                }
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                text = stringResource(R.string.screen_media_upload_preview_optimize_image_quality_title),
                style = ElementTheme.typography.fontBodyLgRegular,
            )
            Switch(
                modifier = Modifier.height(32.dp),
                checked = state.isImageOptimizationEnabled.orFalse(),
                onCheckedChange = { value -> state.eventSink(MediaOptimizationSelectorEvent.SelectImageOptimization(value)) },
            )
        }
    }
}

/**
 * 视频预设选择器
 *
 * 显示视频压缩质量选项的组件。
 * 允许用户选择不同压缩级别的视频预设（高/标准/低）。
 *
 * @param state 媒体优化选择器状态
 */
@Composable
private fun VideoPresetSelector(
    state: MediaOptimizationSelectorState,
) {
    val videoPresets = state.videoSizeEstimations.dataOrNull()
    var selectedPreset by remember(state.selectedVideoPreset) { mutableStateOf(state.selectedVideoPreset) }

    val displayDialog = state.displayVideoPresetSelectorDialog

    val sizeFormatter = rememberFileSizeFormatter()

    // 显示视频质量选择区域
    if (state.displayMediaSelectorViews == true && videoPresets != null && state.selectedVideoPreset != null) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .niceClickable { state.eventSink(MediaOptimizationSelectorEvent.OpenVideoPresetSelectorDialog) }
        ) {
            val estimation = videoPresets.find { it.preset == selectedPreset }
            val estimationMb = estimation?.sizeInBytes?.let { sizeFormatter.format(it, true) }
            val title = buildString {
                append(state.selectedVideoPreset.title())
                if (estimationMb != null) {
                    append(" ($estimationMb)")
                }
            }
            Text(text = title, style = ElementTheme.typography.fontBodyLgMedium)
            Text(
                text = stringResource(R.string.screen_media_upload_preview_change_video_quality_prompt),
                style = ElementTheme.typography.fontBodyLgMedium,
                color = ElementTheme.colors.textSecondary,
            )
        }
    }

    // 显示视频质量选择对话框
    if (displayDialog) {
        VideoQualitySelectorDialog(
            selectedPreset = selectedPreset ?: VideoCompressionPreset.STANDARD,
            videoSizeEstimations = videoPresets ?: persistentListOf(),
            maxFileUploadSize = state.maxUploadSize.dataOrNull(),
            onSubmit = { preset ->
                selectedPreset = preset
                state.eventSink(MediaOptimizationSelectorEvent.SelectVideoPreset(preset))
            },
            onDismiss = { state.eventSink(MediaOptimizationSelectorEvent.DismissVideoPresetSelectorDialog) }
        )
    }
}

/**
 * 视频质量选择对话框
 *
 * 允许用户选择视频压缩预设的对话框。
 * 显示每种预设的估算文件大小和描述。
 *
 * @param selectedPreset 当前选中的视频压缩预设
 * @param videoSizeEstimations 视频大小估算列表
 * @param maxFileUploadSize 最大上传大小限制
 * @param onSubmit 确认选择的回调
 * @param onDismiss 关闭对话框的回调
 */
@Composable
private fun VideoQualitySelectorDialog(
    selectedPreset: VideoCompressionPreset,
    videoSizeEstimations: ImmutableList<VideoUploadEstimation>,
    maxFileUploadSize: Long?,
    onSubmit: (VideoCompressionPreset) -> Unit,
    onDismiss: () -> Unit,
) {
    val sizeFormatter = rememberFileSizeFormatter()

    var localSelectedPreset by remember(selectedPreset) { mutableStateOf(selectedPreset) }
    val subtitlePartNoFileSize = stringResource(CommonStrings.dialog_video_quality_selector_subtitle_no_file_size)
    val subtitlePartWithFileSize = stringResource(CommonStrings.dialog_video_quality_selector_subtitle_file_size)
    val subtitle = remember(maxFileUploadSize) {
        buildString {
            append(subtitlePartNoFileSize)
            if (maxFileUploadSize != null) {
                append(String.format(subtitlePartWithFileSize, sizeFormatter.format(maxFileUploadSize, true)))
            }
        }
    }
    ListDialog(
        title = stringResource(CommonStrings.dialog_video_quality_selector_title),
        subtitle = subtitle,
        onSubmit = { onSubmit(localSelectedPreset) },
        onDismissRequest = onDismiss,
        applyPaddingToContents = false,
    ) {
        for (videoEstimation in videoSizeEstimations) {
            val preset = videoEstimation.preset
            val isSelected = preset == localSelectedPreset
            item(
                key = preset,
                contentType = preset,
            ) {
                val estimationMb = sizeFormatter.format(videoEstimation.sizeInBytes, true)
                val title = "${preset.title()} ($estimationMb)"
                ListItem(
                    headlineContent = {
                        Text(
                            text = title,
                            style = ElementTheme.typography.fontBodyLgMedium,
                        )
                    },
                    supportingContent = {
                        Text(
                            text = preset.subtitle(),
                            style = ElementTheme.typography.fontBodyMdRegular,
                            color = ElementTheme.colors.textSecondary,
                        )
                    },
                    leadingContent = ListItemContent.RadioCheckbox(
                        selected = isSelected,
                    ),
                    onClick = {
                        localSelectedPreset = preset
                    },
                    enabled = videoEstimation.canUpload,
                )
            }
        }
    }
}

/**
 * 附件预览底部操作栏
 *
 * 包含文本输入框和发送按钮的区域。
 * 允许用户输入附件的说明文字并发送。
 *
 * @param state 附件预览状态
 * @param onSendClick 发送按钮点击回调
 * @param modifier 视图修饰符
 */
@Composable
private fun AttachmentsPreviewBottomActions(
    state: AttachmentsPreviewState,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextComposer(
        modifier = modifier,
        state = state.textEditorState,
        voiceMessageState = VoiceMessageState.Idle,
        composerMode = MessageComposerMode.Attachment,
        onRequestFocus = {},
        onSendMessage = onSendClick,
        showTextFormatting = false,
        onResetComposerMode = {},
        onAddAttachment = {},
        onDismissTextFormatting = {},
        onVoiceRecorderEvent = {},
        onVoicePlayerEvent = {},
        onSendVoiceMessage = {},
        onDeleteVoiceMessage = {},
        onReceiveSuggestion = {},
        resolveMentionDisplay = { _, _ -> TextDisplay.Plain },
        resolveAtRoomMentionDisplay = { TextDisplay.Plain },
        onError = {},
        onTyping = {},
        onSelectRichContent = {},
    )
}

// Only preview in dark, dark theme is forced on the Node.
/**
 * 附件预览视图预览
 *
 * 用于在预览模式下测试 AttachmentsPreviewView 的渲染效果
 *
 * @param state 附件预览状态提供器
 */
@Preview
@Composable
internal fun AttachmentsPreviewViewPreview(@PreviewParameter(AttachmentsPreviewStateProvider::class) state: AttachmentsPreviewState) = ElementPreviewDark {
    AttachmentsPreviewView(
        state = state,
        localMediaRenderer = object : LocalMediaRenderer {
            @Composable
            override fun Render(localMedia: LocalMedia) {
                Image(
                    painter = painterResource(id = CommonDrawables.sample_background),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = null,
                )
            }
        }
    )
}

/**
 * 视频质量选择对话框预览
 *
 * 用于在预览模式下测试 VideoQualitySelectorDialog 的渲染效果
 */
@PreviewsDayNight
@Composable
internal fun VideoQualitySelectorDialogPreview() {
    ElementPreview {
        VideoQualitySelectorDialog(
            selectedPreset = VideoCompressionPreset.STANDARD,
            videoSizeEstimations = persistentListOf(
                VideoUploadEstimation(VideoCompressionPreset.HIGH, 2_000_000, canUpload = false),
                VideoUploadEstimation(VideoCompressionPreset.STANDARD, 1_000_000, canUpload = true),
                VideoUploadEstimation(VideoCompressionPreset.LOW, 500_000, canUpload = true)
            ),
            maxFileUploadSize = 1_500_000,
            onSubmit = {},
            onDismiss = {},
        )
    }
}

/**
 * 获取视频压缩预设的显示标题
 *
 * @return 对应质量等级的资源字符串
 */
@Composable
fun VideoCompressionPreset.title(): String {
    return stringResource(
        when (this) {
            VideoCompressionPreset.STANDARD -> CommonStrings.common_video_quality_standard
            VideoCompressionPreset.HIGH -> CommonStrings.common_video_quality_high
            VideoCompressionPreset.LOW -> CommonStrings.common_video_quality_low
        }
    )
}

/**
 * 获取视频压缩预设的描述文字
 *
 * @return 对应预设描述的资源字符串
 */
@Composable
fun VideoCompressionPreset.subtitle(): String {
    return stringResource(
        when (this) {
            VideoCompressionPreset.STANDARD -> CommonStrings.common_video_quality_standard_description
            VideoCompressionPreset.HIGH -> CommonStrings.common_video_quality_high_description
            VideoCompressionPreset.LOW -> CommonStrings.common_video_quality_low_description
        }
    )
}
