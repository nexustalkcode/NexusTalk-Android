/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.reporter

/**
 * 问题报告多部分请求体监听器接口
 *
 * 用于监听文件上传进度的接口。
 */
fun interface BugReporterMultipartBodyListener {
    /**
     * 上传进度回调
     *
     * 当写入数据时调用，用于更新上传进度。
     *
     * @param totalWritten 已写入的总字节数
     * @param contentLength 内容的总长度
     */
    fun onWrite(totalWritten: Long, contentLength: Long)
}
