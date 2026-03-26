/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.leaveroom.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import io.element.android.features.leaveroom.api.LeaveRoomState
import io.element.android.features.leaveroom.impl.LeaveRoomPresenter
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.di.SessionScope

/**
 * 离开房间依赖注入模块
 *
 * 定义离开房间功能所需的依赖注入绑定，
 * 用于在会话范围内提供 LeaveRoomPresenter 实例。
 *
 * @see LeaveRoomPresenter 离开房间 Presenter
 * @see LeaveRoomState 离开房间状态
 */
@ContributesTo(SessionScope::class)
@BindingContainer
interface LeaveRoomModule {
    /**
     * 绑定离开房间 Presenter
     *
     * @param presenter LeaveRoomPresenter 实例
     * @return Presenter<LeaveRoomState>
     */
    @Binds
    fun bindLeaveRoomPresenter(presenter: LeaveRoomPresenter): Presenter<LeaveRoomState>
}
