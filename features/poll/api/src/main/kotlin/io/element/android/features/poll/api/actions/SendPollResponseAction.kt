/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.api.actions

import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.timeline.Timeline

/**
 * 发送投票响应操作接口
 *
 * 定义了向投票发送用户响应（选择答案）的操作。
 * 实现此接口的类负责执行投票响应的具体逻辑。
 */
interface SendPollResponseAction {
    /**
     * 执行发送投票响应操作
     *
     * 调用此方法将用户选择的答案发送到服务器。
     *
     * @param timeline 时间线，用于发送投票响应事件
     * @param pollStartId 投票开始事件 ID，指定要投票的投票
     * @param answerId 用户选择的答案 ID
     * @return Result<Unit> 操作结果，成功或包含失败原因的错误信息
     */
    suspend fun execute(
        timeline: Timeline,
        pollStartId: EventId,
        answerId: String
    ): Result<Unit>
}
