/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.impl.declineandblock

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction

/**
 * 拒绝并封禁状态提供者
 *
 * 用于预览和测试生成 DeclineAndBlockState 实例。
 * 继承自 PreviewParameterProvider，提供多种状态变体用于 UI 预览。
 */
open class DeclineAndBlockStateProvider : PreviewParameterProvider<DeclineAndBlockState> {
    override val values: Sequence<DeclineAndBlockState>
        get() = sequenceOf(
            aDeclineAndBlockState(),
            aDeclineAndBlockState(
                reportRoom = true,
                reportReason = "Inappropriate content",
            ),
            aDeclineAndBlockState(
                blockUser = true,
            ),
            aDeclineAndBlockState(
                declineAction = AsyncAction.Loading,
            ),
            aDeclineAndBlockState(
                declineAction = AsyncAction.Failure(Exception("Failed to decline")),
            ),
        )
}

/**
 * 创建默认的拒绝并封禁状态
 *
 * 用于预览和测试生成 DeclineAndBlockState 实例。
 *
 * @param reportRoom 是否举报房间，默认为 false
 * @param reportReason 举报原因，默认为空字符串
 * @param blockUser 是否封禁用户，默认为 false
 * @param declineAction 拒绝操作的异步状态，默认为未初始化
 * @param eventSink 事件处理函数，默认为空函数
 * @return DeclineAndBlockState 实例
 */
fun aDeclineAndBlockState(
    reportRoom: Boolean = false,
    reportReason: String = "",
    blockUser: Boolean = false,
    declineAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (DeclineAndBlockEvents) -> Unit = {},
) = DeclineAndBlockState(
    reportRoom = reportRoom,
    reportReason = reportReason,
    blockUser = blockUser,
    declineAction = declineAction,
    eventSink = eventSink,
)
