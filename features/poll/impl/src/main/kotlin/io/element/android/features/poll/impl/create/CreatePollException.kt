/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl.create

/**
 * 创建投票异常密封类
 *
 * 定义了创建投票过程中可能发生的异常类型。
 */
internal sealed class CreatePollException : Exception() {
    /**
     * 获取投票失败异常
     *
     * 当尝试编辑投票但无法获取投票详情时抛出。
     *
     * @property message 错误消息
     * @property cause 原始异常
     */
    data class GetPollFailed(
        override val message: String?,
        override val cause: Throwable?
    ) : CreatePollException()

    /**
     * 保存投票失败异常
     *
     * 当尝试创建或保存投票但失败时抛出。
     *
     * @property message 错误消息
     * @property cause 原始异常
     */
    data class SavePollFailed(
        override val message: String?,
        override val cause: Throwable?
    ) : CreatePollException()
}
