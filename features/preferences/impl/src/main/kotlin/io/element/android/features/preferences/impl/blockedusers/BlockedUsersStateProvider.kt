/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.blockedusers

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.components.aMatrixUserList
import kotlinx.collections.immutable.toImmutableList

/**
 * 被屏蔽用户状态提供者
 *
 * 用于在预览模式下提供被屏蔽用户页面的示例状态数据。
 *
 * @see BlockedUsersState 被屏蔽用户状态
 */
class BlockedUsersStateProvider : PreviewParameterProvider<BlockedUsersState> {
    override val values: Sequence<BlockedUsersState>
        get() = sequenceOf(
            aBlockedUsersState(),
            aBlockedUsersState(blockedUsers = aMatrixUserList().map { it.copy(displayName = null, avatarUrl = null) }),
            aBlockedUsersState(blockedUsers = emptyList()),
            aBlockedUsersState(unblockUserAction = AsyncAction.ConfirmingNoParams),
            aBlockedUsersState(unblockUserAction = AsyncAction.Loading),
            aBlockedUsersState(unblockUserAction = AsyncAction.Failure(RuntimeException("Failed to unblock user"))),
            aBlockedUsersState(unblockUserAction = AsyncAction.Success(Unit)),
        )
}

/**
 * 创建示例 BlockedUsersState 对象
 *
 * @param blockedUsers 被屏蔽用户列表
 * @param unblockUserAction 解封用户操作状态
 * @param eventSink 事件处理函数
 * @return BlockedUsersState 示例状态
 */
internal fun aBlockedUsersState(
    blockedUsers: List<MatrixUser> = aMatrixUserList(),
    unblockUserAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (BlockedUsersEvents) -> Unit = {},
): BlockedUsersState {
    return BlockedUsersState(
        blockedUsers = blockedUsers.toImmutableList(),
        unblockUserAction = unblockUserAction,
        eventSink = eventSink,
    )
}
