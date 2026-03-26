/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl.actions

import dev.zacsweers.metro.ContributesBinding
import im.vector.app.features.analytics.plan.PollVote
import io.element.android.features.poll.api.actions.SendPollResponseAction
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.services.analytics.api.AnalyticsService

/**
 * 默认发送投票响应操作实现类
 *
 * 实现了 SendPollResponseAction 接口，负责发送投票响应的具体逻辑。
 * 发送投票响应后会上报分析事件。
 *
 * @property analyticsService 分析服务，用于上报投票事件
 */
@ContributesBinding(RoomScope::class)
class DefaultSendPollResponseAction(
    private val analyticsService: AnalyticsService,
) : SendPollResponseAction {
    /**
     * 执行发送投票响应操作
     *
     * @param timeline 时间线，用于发送投票响应事件
     * @param pollStartId 投票开始事件 ID
     * @param answerId 用户选择的答案 ID
     * @return Result<Unit> 操作结果
     */
    override suspend fun execute(timeline: Timeline, pollStartId: EventId, answerId: String): Result<Unit> {
        return timeline.sendPollResponse(
            pollStartId = pollStartId,
            answers = listOf(answerId),
        ).onSuccess {
            analyticsService.capture(PollVote())
        }
    }
}
