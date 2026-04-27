/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.root

import io.element.android.libraries.matrix.api.user.MatrixUser

/**
 * 开始聊天主页可能产生的用户事件。
 */
sealed interface StartChatEvents {
    /** 对指定用户发起私聊。 */
    data class StartDM(val matrixUser: MatrixUser) : StartChatEvents

    /** 取消当前“开始私聊”异步状态。 */
    data object CancelStartDM : StartChatEvents
}
