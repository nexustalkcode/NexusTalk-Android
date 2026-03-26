/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments.preview

import androidx.compose.runtime.Immutable
import io.element.android.features.messages.impl.attachments.Attachment
import io.element.android.features.messages.impl.attachments.video.MediaOptimizationSelectorState
import io.element.android.libraries.mediaupload.api.MediaUploadInfo
import io.element.android.libraries.textcomposer.model.TextEditorState

/**
 * 附件预览状态数据类
 *
 * 存储附件预览界面的所有状态信息，供视图层渲染使用。
 *
 * @property attachment 要预览和发送的附件对象
 * @property sendActionState 发送操作状态，表示附件发送的当前阶段
 * @property textEditorState 文本编辑器状态，用于输入附件的说明文字
 * @property mediaOptimizationSelectorState 媒体优化选择器状态，控制图片/视频优化选项
 * @property displayFileTooLargeError 是否显示文件太大错误提示
 * @property eventSink 事件处理函数，用于将用户交互事件传递给Presenter
 */
data class AttachmentsPreviewState(
    val attachment: Attachment,
    val sendActionState: SendActionState,
    val textEditorState: TextEditorState,
    val mediaOptimizationSelectorState: MediaOptimizationSelectorState,
    val displayFileTooLargeError: Boolean,
    val eventSink: (AttachmentsPreviewEvents) -> Unit
)

/**
 * 发送操作状态密封接口
 *
 * 定义附件发送过程中的各种状态。
 * 使用密封接口确保类型安全，涵盖从空闲到完成的所有可能状态。
 */
@Immutable
sealed interface SendActionState {
    /**
     * 空闲状态
     *
     * 初始状态，表示用户尚未开始发送操作
     */
    data object Idle : SendActionState

    /**
     * 发送中状态
     *
     * 包含媒体预处理和上传的各个子状态
     */
    @Immutable
    sealed interface Sending : SendActionState {
        /**
         * 媒体处理中状态
         *
         * 正在进行媒体文件的压缩和优化处理
         *
         * @property displayProgress 是否在UI上显示进度指示器
         */
        data class Processing(val displayProgress: Boolean) : Sending

        /**
         * 准备上传状态
         *
         * 媒体预处理已完成，准备开始上传
         *
         * @property mediaInfo 预处理后的媒体上传信息
         */
        data class ReadyToUpload(val mediaInfo: MediaUploadInfo) : Sending

        /**
         * 上传中状态
         *
         * 正在将媒体文件上传到服务器
         *
         * @property mediaUploadInfo 媒体上传信息
         */
        data class Uploading(val mediaUploadInfo: MediaUploadInfo) : Sending
    }

    /**
     * 发送失败状态
     *
     * 发送过程中发生错误
     *
     * @property error 发生的异常对象
     * @property mediaUploadInfo 失败时已处理的媒体信息（如果有）
     */
    data class Failure(val error: Throwable, val mediaUploadInfo: MediaUploadInfo?) : SendActionState

    /**
     * 完成状态
     *
     * 附件发送成功完成
     */
    data object Done : SendActionState

    /**
     * 获取媒体上传信息
     *
     * 如果当前状态包含媒体上传信息，则返回；否则返回null
     *
     * @return 媒体上传信息或null
     */
    fun mediaUploadInfo(): MediaUploadInfo? = when (this) {
        is Sending.ReadyToUpload -> mediaInfo
        is Sending.Uploading -> mediaUploadInfo
        is Failure -> mediaUploadInfo
        else -> null
    }
}
