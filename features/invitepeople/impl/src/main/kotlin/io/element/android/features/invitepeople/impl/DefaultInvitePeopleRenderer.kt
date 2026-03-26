/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invitepeople.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.invitepeople.api.InvitePeopleRenderer
import io.element.android.features.invitepeople.api.InvitePeopleState
import io.element.android.libraries.di.SessionScope

/**
 * 默认邀请人员渲染器
 *
 * 实现InvitePeopleRenderer接口，负责渲染邀请人员页面的UI。
 * 使用@ContributesBinding注解将实现绑定到SessionScope，支持依赖注入。
 *
 * 渲染逻辑：
 * - 检查状态类型是否为DefaultInvitePeopleState
 * - 如果是，则调用InvitePeopleView进行渲染
 * - 如果不是，抛出Unsupported state type错误
 *
 * @see InvitePeopleRenderer 渲染器接口
 * @see InvitePeopleView 实际渲染的UI组件
 */
@ContributesBinding(SessionScope::class)
class DefaultInvitePeopleRenderer : InvitePeopleRenderer {
    /**
     * 渲染邀请人员页面
     *
     * 根据传入的状态渲染邀请人员界面。
     * 只支持DefaultInvitePeopleState类型的状态，其他类型会抛出异常。
     *
     * @param state 邀请人员页面状态，必须是DefaultInvitePeopleState类型
     * @param modifier Compose修饰符，用于控制布局和样式
     * @throws IllegalStateException 如果状态类型不支持
     */
    @Composable
    override fun Render(state: InvitePeopleState, modifier: Modifier) {
        if (state is DefaultInvitePeopleState) {
            InvitePeopleView(
                state = state,
                modifier = modifier
            )
        } else {
            error("Unsupported state type: ${state::javaClass}")
        }
    }
}
