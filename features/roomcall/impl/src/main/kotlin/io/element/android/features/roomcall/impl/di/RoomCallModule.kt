/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomcall.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import io.element.android.features.roomcall.api.RoomCallState
import io.element.android.features.roomcall.impl.RoomCallStatePresenter
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.di.RoomScope

/**
 * 房间通话功能依赖注入模块
 *
 * 提供房间通话相关的依赖注入配置，
 * 绑定 RoomCallStatePresenter 到 Presenter 接口
 */
@ContributesTo(RoomScope::class)
@BindingContainer
interface RoomCallModule {
    /**
     * 绑定房间通话状态 presenter
     *
     * @param presenter RoomCallStatePresenter 实例
     * @return Presenter<RoomCallState> 接口，用于提供房间通话状态
     */
    @Binds
    fun bindRoomCallStatePresenter(presenter: RoomCallStatePresenter): Presenter<RoomCallState>
}
