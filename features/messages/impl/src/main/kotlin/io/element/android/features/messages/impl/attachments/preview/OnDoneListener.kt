/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments.preview

/**
 * 完成监听器接口
 *
 * 函数式接口，用于在操作完成时通知调用者。
 * 当用户确认发送附件或取消操作时触发回调。
 *
 * 使用示例：
 * ```kotlin
 * val listener = OnDoneListener {
 *     // 执行完成后的操作，例如导航回上一页面
 *     navigateUp()
 * }
 * ```
 *
 * @see AttachmentsPreviewNode 使用此接口在发送完成后导航回聊天界面
 */
fun interface OnDoneListener {
    /**
     * 触发完成回调
     *
     * 当操作完成时调用此方法
     */
    operator fun invoke()
}
