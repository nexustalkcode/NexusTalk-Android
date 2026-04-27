/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.api

import androidx.compose.runtime.MutableState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 发起私聊的业务动作接口。
 */
interface StartDMAction {
    /**
     * 尝试查找指定用户的现有私聊房间，必要时创建新的私聊房间。
     *
     * @param matrixUser 需要发起私聊的目标用户。
     * @param createIfDmDoesNotExist 若为 `true`，在不存在私聊时直接创建；否则返回 [ConfirmingStartDmWithMatrixUser] 确认态。
     * @param actionState 需要被回写的异步动作状态。
     */
    suspend fun execute(
        matrixUser: MatrixUser,
        createIfDmDoesNotExist: Boolean,
        actionState: MutableState<AsyncAction<RoomId>>,
    )
}
