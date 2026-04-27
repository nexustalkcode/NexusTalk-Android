/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.api

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 表示“开始私聊”操作进入二次确认态。
 *
 * 当目标用户还没有现成 DM 房间时，UI 会收到该状态以决定是否继续创建新私聊。
 *
 * @property matrixUser 需要发起私聊的目标用户。
 */
data class ConfirmingStartDmWithMatrixUser(
    val matrixUser: MatrixUser,
) : AsyncAction.Confirming
