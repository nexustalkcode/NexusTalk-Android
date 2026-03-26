/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments.preview.error

import io.element.android.features.messages.impl.R
import io.element.android.libraries.mediaupload.api.MediaPreProcessor

/**
 * 发送附件错误格式化函数
 *
 * 根据异常类型返回对应的错误提示字符串资源ID。
 * 用于在发送附件失败时向用户显示适当的错误信息。
 *
 * @param throwable 发生的异常对象
 * @return 对应错误类型的字符串资源ID
 *
 * @see R.string.screen_media_upload_preview_error_failed_processing 媒体处理失败
 * @see R.string.screen_media_upload_preview_error_failed_sending 发送失败
 */
fun sendAttachmentError(
    throwable: Throwable
): Int {
    return if (throwable is MediaPreProcessor.Failure) {
        R.string.screen_media_upload_preview_error_failed_processing
    } else {
        R.string.screen_media_upload_preview_error_failed_sending
    }
}
