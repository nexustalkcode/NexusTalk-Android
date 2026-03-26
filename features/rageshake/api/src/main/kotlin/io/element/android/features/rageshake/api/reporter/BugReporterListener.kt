/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.reporter

/**
 * 问题报告上传监听器接口
 *
 * 监听问题报告上传过程中的各种事件，包括进度、成功和失败状态。
 */
interface BugReporterListener {
    /**
     * 上传已取消
     *
     * 当用户取消问题报告上传时调用。
     */
    fun onUploadCancelled()

    /**
     * 上传失败
     *
     * 当问题报告上传失败时调用。
     *
     * @param reason 失败原因
     */
    fun onUploadFailed(reason: String?)

    /**
     * 上传进度更新
     *
     * 上传进度更新回调，以百分比表示。
     *
     * @param progress 上传进度（0-100）
     */
    fun onProgress(progress: Int)

    /**
     * 上传成功
     *
     * 当问题报告成功上传时调用。
     */
    fun onUploadSucceed()
}
