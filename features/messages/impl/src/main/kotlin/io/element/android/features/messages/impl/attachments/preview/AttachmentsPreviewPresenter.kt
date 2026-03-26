/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.features.messages.impl.attachments.Attachment
import io.element.android.features.messages.impl.attachments.video.MediaOptimizationSelectorPresenter
import io.element.android.libraries.androidutils.file.TemporaryUriDeleter
import io.element.android.libraries.androidutils.file.safeDelete
import io.element.android.libraries.androidutils.hash.hash
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.core.coroutine.firstInstanceOf
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeImage
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeVideo
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.permalink.PermalinkBuilder
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.mediaupload.api.MediaOptimizationConfig
import io.element.android.libraries.mediaupload.api.MediaOptimizationConfigProvider
import io.element.android.libraries.mediaupload.api.MediaSenderFactory
import io.element.android.libraries.mediaupload.api.MediaUploadInfo
import io.element.android.libraries.mediaupload.api.allFiles
import io.element.android.libraries.preferences.api.store.VideoCompressionPreset
import io.element.android.libraries.textcomposer.model.TextEditorState
import io.element.android.libraries.textcomposer.model.rememberMarkdownTextEditorState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 附件预览Presenter
 *
 * 负责处理附件预览界面的业务逻辑和数据状态管理。
 * 管理附件发送的完整生命周期，包括预处理、上传和错误处理。
 *
 * 主要功能：
 * - 管理附件预览界面的状态
 * - 处理媒体文件的预处理（压缩、优化）
 * - 执行媒体文件上传
 * - 处理发送过程中的各种状态变化
 *
 * @property attachment 要预览和发送的附件
 * @property onDoneListener 完成监听器，当流程结束时回调
 * @property timelineMode 时间线模式，决定发送目标
 * @property inReplyToEventId 回复目标事件ID
 * @property mediaSenderFactory 媒体发送器工厂
 * @property permalinkBuilder 永久链接构建器，用于处理消息中的链接
 * @property temporaryUriDeleter 临时URI删除器，用于清理临时文件
 * @property mediaOptimizationSelectorPresenterFactory 媒体优化选择器Presenter工厂
 * @property sessionCoroutineScope 会话级别的协程作用域
 * @property dispatchers 协程调度器
 * @property mediaOptimizationConfigProvider 媒体优化配置提供者
 */
@AssistedInject
class AttachmentsPreviewPresenter(
    @Assisted private val attachment: Attachment,
    @Assisted private val onDoneListener: OnDoneListener,
    @Assisted private val timelineMode: Timeline.Mode,
    @Assisted private val inReplyToEventId: EventId?,
    mediaSenderFactory: MediaSenderFactory,
    private val permalinkBuilder: PermalinkBuilder,
    private val temporaryUriDeleter: TemporaryUriDeleter,
    private val mediaOptimizationSelectorPresenterFactory: MediaOptimizationSelectorPresenter.Factory,
    @SessionCoroutineScope private val sessionCoroutineScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val mediaOptimizationConfigProvider: MediaOptimizationConfigProvider,
) : Presenter<AttachmentsPreviewState> {
    /**
     * Presenter工厂接口
     *
     * 用于创建AttachmentsPreviewPresenter实例的工厂类。
     * 实现依赖注入模式的自动化工厂生成。
     */
    @AssistedFactory
    interface Factory {
        /**
         * 创建附件预览Presenter实例
         *
         * @param attachment 要预览和发送的附件
         * @param timelineMode 时间线模式
         * @param onDoneListener 完成监听器
         * @param inReplyToEventId 回复目标事件ID
         * @return AttachmentsPreviewPresenter实例
         */
        fun create(
            attachment: Attachment,
            timelineMode: Timeline.Mode,
            onDoneListener: OnDoneListener,
            inReplyToEventId: EventId?,
        ): AttachmentsPreviewPresenter
    }

    /**
     * 媒体发送器
     *
     * 用于发送预处理后的媒体文件
     */
    private val mediaSender = mediaSenderFactory.create(timelineMode)

    /**
     * 生成并返回附件预览状态
     *
     * Compose Composable函数，作为Presenter的主入口。
     * 负责初始化状态、管理协程、处理用户事件并返回视图状态。
     *
     * @return AttachmentsPreviewState 包含所有视图所需数据的不可变状态对象
     */
    @Composable
    override fun present(): AttachmentsPreviewState {
        // 创建协程作用域，用于管理异步操作
        val coroutineScope = rememberCoroutineScope()

        // 发送操作状态，管理附件发送的各个阶段
        val sendActionState = remember {
            mutableStateOf<SendActionState>(SendActionState.Idle)
        }

        // Markdown文本编辑器状态，用于用户输入附件说明文字
        val markdownTextEditorState = rememberMarkdownTextEditorState(initialText = null, initialFocus = false)
        val textEditorState by rememberUpdatedState(
            TextEditorState.Markdown(markdownTextEditorState, isRoomEncrypted = null)
        )

        // 当前正在进行的发送任务
        val ongoingSendAttachmentJob = remember { mutableStateOf<Job?>(null) }

        // 媒体预处理任务
        var preprocessMediaJob by remember { mutableStateOf<Job?>(null) }

        // 将附件转换为媒体附件类型
        val mediaAttachment = attachment as Attachment.Media
        // 创建媒体优化选择器Presenter
        val mediaOptimizationSelectorPresenter = remember {
            mediaOptimizationSelectorPresenterFactory.create(mediaAttachment.localMedia)
        }
        // 获取媒体优化选择器状态
        val mediaOptimizationSelectorState by rememberUpdatedState(mediaOptimizationSelectorPresenter.present())

        // 将发送状态转换为可观察的Flow
        val observableSendState = snapshotFlow { sendActionState.value }

        // 是否显示文件太大错误提示
        var displayFileTooLargeError by remember { mutableStateOf(false) }

        LaunchedEffect(mediaOptimizationSelectorState.displayMediaSelectorViews) {
            // If the media optimization selector is not displayed, we can pre-process the media
            // to prepare it for sending. This is done to avoid blocking the UI thread when the
            // user clicks on the send button.
            if (mediaOptimizationSelectorState.displayMediaSelectorViews == false) {
                preprocessMediaJob = preProcessAttachment(
                    attachment = attachment,
                    mediaOptimizationConfig = mediaOptimizationConfigProvider.get(),
                    displayProgress = false,
                    sendActionState = sendActionState,
                )
            }
        }

        val maxUploadSize = mediaOptimizationSelectorState.maxUploadSize.dataOrNull()
        LaunchedEffect(maxUploadSize) {
            // Check file upload size if the media won't be processed for upload
            val isImageFile = mediaAttachment.localMedia.info.mimeType.isMimeTypeImage()
            val isVideoFile = mediaAttachment.localMedia.info.mimeType.isMimeTypeVideo()
            if (maxUploadSize != null && !(isImageFile || isVideoFile)) {
                // If file size is not known, we're permissive and allow sending. The SDK will cancel the upload if needed.
                val fileSize = mediaAttachment.localMedia.info.fileSize ?: 0L
                if (maxUploadSize < fileSize) {
                    displayFileTooLargeError = true
                }
            }
        }

        val videoSizeEstimations = mediaOptimizationSelectorState.videoSizeEstimations.dataOrNull()
        LaunchedEffect(videoSizeEstimations) {
            if (videoSizeEstimations != null) {
                // Check if the video size estimations are too large for the max upload size
                displayFileTooLargeError = videoSizeEstimations.none { it.canUpload }
            }
        }

        /**
         * 处理用户事件
         *
         * 根据不同的事件类型执行相应的业务逻辑
         *
         * @param event 用户交互事件
         */
        fun handleEvent(event: AttachmentsPreviewEvents) {
            when (event) {
                // 发送附件事件
                is AttachmentsPreviewEvents.SendAttachment -> {
                    ongoingSendAttachmentJob.value = coroutineScope.launch {
                        // 如果媒体优化选择器显示，需要等待用户选择选项后再处理媒体
                        if (mediaOptimizationSelectorState.displayMediaSelectorViews == true) {
                            val config = MediaOptimizationConfig(
                                compressImages = mediaOptimizationSelectorState.isImageOptimizationEnabled == true,
                                videoCompressionPreset = mediaOptimizationSelectorState.selectedVideoPreset ?: VideoCompressionPreset.STANDARD,
                            )
                            preprocessMediaJob = preProcessAttachment(
                                attachment = attachment,
                                mediaOptimizationConfig = config,
                                displayProgress = true,
                                sendActionState = sendActionState,
                            )
                        }

                        // 如果之前处理是隐藏的，现在让它显示出来
                        if (sendActionState.value is SendActionState.Sending.Processing) {
                            sendActionState.value = SendActionState.Sending.Processing(displayProgress = true)
                        }

                        // 等待媒体准备好上传
                        val mediaUploadInfo = observableSendState.firstInstanceOf<SendActionState.Sending.ReadyToUpload>().mediaInfo

                        // 预处理完成，发送附件
                        val caption = markdownTextEditorState.getMessageMarkdown(permalinkBuilder)
                            .takeIf { it.isNotEmpty() }

                        // 如果媒体将在后台发送，可以立即关闭此界面
                        if (coroutineContext.isActive) {
                            onDoneListener()
                        }

                        // 使用会话协程作用域发送媒体，这样即使用户关闭此界面或聊天界面也能完成发送
                        sessionCoroutineScope.launch(dispatchers.io) {
                            sendPreProcessedMedia(
                                mediaUploadInfo = mediaUploadInfo,
                                caption = caption,
                                sendActionState = sendActionState,
                                dismissAfterSend = false,
                                inReplyToEventId = inReplyToEventId,
                            )

                            // 发送完成后清理预处理后的媒体
                            mediaSender.cleanUp()
                        }
                    }
                }
                // 取消并关闭预览界面事件
                AttachmentsPreviewEvents.CancelAndDismiss -> {
                    displayFileTooLargeError = false

                    // 取消媒体预处理和发送
                    preprocessMediaJob?.cancel()
                    // 如果无法发送预处理后的媒体，则删除它
                    mediaSender.cleanUp()
                    ongoingSendAttachmentJob.value?.cancel()

                    // 关闭界面
                    dismiss(
                        attachment,
                        sendActionState,
                    )
                }
                // 取消并清除发送状态事件
                AttachmentsPreviewEvents.CancelAndClearSendState -> {
                    // 取消媒体发送
                    ongoingSendAttachmentJob.value?.let {
                        it.cancel()
                        ongoingSendAttachmentJob.value = null
                    }

                    val mediaUploadInfo = sendActionState.value.mediaUploadInfo()
                    sendActionState.value = if (mediaUploadInfo != null) {
                        SendActionState.Sending.ReadyToUpload(mediaUploadInfo)
                    } else {
                        SendActionState.Idle
                    }
                }
            }
        }

        return AttachmentsPreviewState(
            attachment = attachment,
            sendActionState = sendActionState.value,
            textEditorState = textEditorState,
            mediaOptimizationSelectorState = mediaOptimizationSelectorState,
            displayFileTooLargeError = displayFileTooLargeError,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 预处理附件
     *
     * 在后台协程中预处理媒体文件，包括压缩和优化
     *
     * @param attachment 要处理的附件
     * @param mediaOptimizationConfig 媒体优化配置
     * @param displayProgress 是否显示进度
     * @param sendActionState 发送状态容器
     * @return 预处理任务的Job
     */
    private fun CoroutineScope.preProcessAttachment(
        attachment: Attachment,
        mediaOptimizationConfig: MediaOptimizationConfig,
        displayProgress: Boolean,
        sendActionState: MutableState<SendActionState>,
    ) = launch(dispatchers.io) {
        when (attachment) {
            is Attachment.Media -> {
                preProcessMedia(
                    mediaAttachment = attachment,
                    mediaOptimizationConfig = mediaOptimizationConfig,
                    displayProgress = displayProgress,
                    sendActionState = sendActionState,
                )
            }
        }
    }

    /**
     * 预处理媒体文件
     *
     * 执行实际的媒体预处理操作（压缩、优化）
     *
     * @param mediaAttachment 媒体附件
     * @param mediaOptimizationConfig 媒体优化配置
     * @param displayProgress 是否显示进度
     * @param sendActionState 发送状态容器
     */
    private suspend fun preProcessMedia(
        mediaAttachment: Attachment.Media,
        mediaOptimizationConfig: MediaOptimizationConfig,
        displayProgress: Boolean,
        sendActionState: MutableState<SendActionState>,
    ) {
        sendActionState.value = SendActionState.Sending.Processing(displayProgress = displayProgress)
        mediaSender.preProcessMedia(
            uri = mediaAttachment.localMedia.uri,
            mimeType = mediaAttachment.localMedia.info.mimeType,
            mediaOptimizationConfig = mediaOptimizationConfig,
        ).fold(
            onSuccess = { mediaUploadInfo ->
                Timber.d("Media ${mediaUploadInfo.file.path.orEmpty().hash()} finished processing, it's now ready to upload")
                sendActionState.value = SendActionState.Sending.ReadyToUpload(mediaUploadInfo)
            },
            onFailure = {
                Timber.e(it, "Failed to pre-process media")
                if (it is CancellationException) {
                    throw it
                } else {
                    sendActionState.value = SendActionState.Failure(it, null)
                }
            }
        )
    }

    /**
     * 关闭预览界面
     *
     * 清理临时文件并触发完成回调
     *
     * @param attachment 要清理的附件
     * @param sendActionState 发送状态容器
     */
    private fun dismiss(
        attachment: Attachment,
        sendActionState: MutableState<SendActionState>,
    ) {
        // 删除临时文件
        when (attachment) {
            is Attachment.Media -> {
                temporaryUriDeleter.delete(attachment.localMedia.uri)
                sendActionState.value.mediaUploadInfo()?.let { data ->
                    cleanUp(data)
                }
        }
        }
        // 重置发送状态以确保对话框在界面关闭前关闭
        sendActionState.value = SendActionState.Done
        onDoneListener()
    }

    /**
     * 清理媒体上传信息
     *
     * 删除与媒体上传相关的临时文件
     *
     * @param mediaUploadInfo 媒体上传信息
     */
    private fun cleanUp(
        mediaUploadInfo: MediaUploadInfo,
    ) {
        mediaUploadInfo.allFiles().forEach { file ->
            file.safeDelete()
        }
    }

    /**
     * 发送预处理后的媒体
     *
     * 执行实际上传操作，将预处理完成的媒体发送到服务器
     *
     * @param mediaUploadInfo 预处理后的媒体上传信息
     * @param caption 附件说明文字
     * @param sendActionState 发送状态容器
     * @param dismissAfterSend 发送后是否关闭界面
     * @param inReplyToEventId 回复目标事件ID
     */
    private suspend fun sendPreProcessedMedia(
        mediaUploadInfo: MediaUploadInfo,
        caption: String?,
        sendActionState: MutableState<SendActionState>,
        dismissAfterSend: Boolean,
        inReplyToEventId: EventId?,
    ) = runCatchingExceptions {
        sendActionState.value = SendActionState.Sending.Uploading(mediaUploadInfo)
        mediaSender.sendPreProcessedMedia(
            mediaUploadInfo = mediaUploadInfo,
            caption = caption,
            formattedCaption = null,
            inReplyToEventId = inReplyToEventId,
        ).getOrThrow()
    }.fold(
        onSuccess = {
            cleanUp(mediaUploadInfo)
            // 重置发送状态以确保对话框在界面关闭前关闭
            sendActionState.value = SendActionState.Done

            if (dismissAfterSend) {
                onDoneListener()
            }
        },
        onFailure = { error ->
            Timber.e(error, "Failed to send attachment")
            if (error is CancellationException) {
                throw error
            } else {
                sendActionState.value = SendActionState.Failure(error, mediaUploadInfo)
            }
        }
    )
}
