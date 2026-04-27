/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import io.element.android.features.home.impl.filters.RoomListFiltersPresenter
import io.element.android.features.home.impl.filters.RoomListFiltersState
import io.element.android.features.home.impl.roomlist.RoomListPresenter
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.search.RoomListSearchPresenter
import io.element.android.features.home.impl.search.RoomListSearchState
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.di.SessionScope

@ContributesTo(SessionScope::class)
@BindingContainer
/**
 * 首页房间列表相关依赖绑定模块。
 */
interface RoomListModule {
    @Binds
    /** 绑定房间列表 Presenter。 */
    fun bindRoomListPresenter(presenter: RoomListPresenter): Presenter<RoomListState>

    @Binds
    /** 绑定房间搜索 Presenter。 */
    fun bindSearchPresenter(presenter: RoomListSearchPresenter): Presenter<RoomListSearchState>

    @Binds
    /** 绑定筛选器 Presenter。 */
    fun bindFiltersPresenter(presenter: RoomListFiltersPresenter): Presenter<RoomListFiltersState>
}
