/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
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
import io.element.android.features.home.impl.grouplist.GroupListPresenter
import io.element.android.features.home.impl.grouplist.GroupListState
import io.element.android.features.home.impl.search.RoomListSearchPresenter
import io.element.android.features.home.impl.search.RoomListSearchState
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.di.SessionScope

@ContributesTo(SessionScope::class)
@BindingContainer
interface GroupListModule {
    @Binds
    fun bindGroupListPresenter(presenter: GroupListPresenter): Presenter<GroupListState>
}
