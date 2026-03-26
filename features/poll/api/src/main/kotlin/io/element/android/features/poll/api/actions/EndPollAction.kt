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
 * 结束投票操作接口
 *
 * 定义了结束（关闭）投票的操作。
 * 实现此接口的类负责执行关闭投票的具体逻辑。
 */
interface EndPollAction {
    /**
     * 执行结束投票操作
     *
     * 调用此方法将结束指定的投票，使其不再接受新的投票。
     *
     * @param timeline 时间线，用于发送结束投票的事件
     * @param pollStartId 投票开始事件 ID，指定要结束的投票
     * @return Result<Unit> 操作结果，成功或包含失败原因的错误信息
     */
    suspend fun execute(timeline: Timeline, pollStartId: EventId): Result<Unit>
}
