/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.api.acceptdecline

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 接受/拒绝邀请视图接口
 *
 * 定义了渲染接受/拒绝邀请界面的函数式接口。
 * 用于在 Compose 中展示邀请处理界面。
 *
 * @property state 接受/拒绝邀请的界面状态
 * @property onAcceptInviteSuccess 接受邀请成功后的回调
 * @property onDeclineInviteSuccess 拒绝邀请成功后的回调
 * @property modifier 修饰符
 */
fun interface AcceptDeclineInviteView {
    /**
     * 渲染接受/拒绝邀请界面
     *
     * @param state 界面状态
     * @param onAcceptInviteSuccess 接受成功回调
     * @param onDeclineInviteSuccess 拒绝成功回调
     * @param modifier 修饰符
     */
    @Composable
    fun Render(
        state: AcceptDeclineInviteState,
        onAcceptInviteSuccess: (RoomId) -> Unit,
        onDeclineInviteSuccess: (RoomId) -> Unit,
        modifier: Modifier,
    )
}
