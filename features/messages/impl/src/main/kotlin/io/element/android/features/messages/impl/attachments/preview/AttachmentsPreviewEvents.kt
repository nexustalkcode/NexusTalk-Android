/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments.preview

/**
 * 附件预览事件密封接口
 *
 * 定义附件预览界面可能发生的用户交互事件。
 * 用于在 Compose 的事件流中传递用户操作。
 *
 * 事件类型：
 * - [SendAttachment]: 发送附件
 * - [CancelAndDismiss]: 取消并关闭预览界面
 * - [CancelAndClearSendState]: 取消发送并清除发送状态
 */
sealed interface AttachmentsPreviewEvents {
    /**
     * 发送附件事件
     *
     * 当用户点击发送按钮时触发，表示用户确认发送当前预览的附件。
     * 该事件会启动附件的预处理、上传流程。
     */
    data object SendAttachment : AttachmentsPreviewEvents

    /**
     * 取消并关闭预览界面事件
     *
     * 当用户点击关闭按钮或返回键时触发。
     * 会取消正在进行的媒体处理和上传操作，并清理临时文件。
     */
    data object CancelAndDismiss : AttachmentsPreviewEvents

    /**
     * 取消并清除发送状态事件
     *
     * 当用户取消正在进行的发送操作时触发，但保持预览界面打开。
     * 会重置发送状态为就绪状态，允许用户重新尝试发送。
     */
    data object CancelAndClearSendState : AttachmentsPreviewEvents
}
