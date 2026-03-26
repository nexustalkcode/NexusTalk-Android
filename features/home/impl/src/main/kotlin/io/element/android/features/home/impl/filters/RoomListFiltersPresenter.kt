/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.filters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import dev.zacsweers.metro.Inject
import io.element.android.features.home.impl.filters.selection.FilterSelectionStrategy
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.roomlist.RoomListService
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map
import io.element.android.libraries.matrix.api.roomlist.RoomListFilter as MatrixRoomListFilter

/**
 * 房间列表过滤器 Presenter
 *
 * 负责处理房间列表过滤器功能的业务逻辑和状态管理。
 * 管理房间筛选条件的设置和过滤器的应用。
 *
 * @property roomListService 房间列表服务
 * @property filterSelectionStrategy 过滤器选择策略
 */
@Inject
class RoomListFiltersPresenter(
    private val roomListService: RoomListService,
    private val filterSelectionStrategy: FilterSelectionStrategy,
) : Presenter<RoomListFiltersState> {
    private val initialFilters = filterSelectionStrategy.filterSelectionStates.value.toImmutableList()

    /**
     * 生成界面状态
     *
     * @return RoomListFiltersState 房间列表过滤器状态
     */
    @Composable
    override fun present(): RoomListFiltersState {
        /**
         * 处理用户事件
         *
         * @param event 房间列表过滤器事件
         */
        fun handleEvent(event: RoomListFiltersEvents) {
            when (event) {
                RoomListFiltersEvents.ClearSelectedFilters -> {
                    filterSelectionStrategy.clear()
                }
                is RoomListFiltersEvents.ToggleFilter -> {
                    filterSelectionStrategy.toggle(event.filter)
                }
            }
        }

        val filters by produceState(initialValue = initialFilters) {
            filterSelectionStrategy.filterSelectionStates
                .map { filters ->
                    value = filters.toImmutableList()
                    filters.mapNotNull { filterState ->
                        if (!filterState.isSelected) {
                            return@mapNotNull null
                        }
                        when (filterState.filter) {
                            RoomListFilter.Rooms -> MatrixRoomListFilter.Category.Group
                            RoomListFilter.People -> MatrixRoomListFilter.Category.People
                            RoomListFilter.Unread -> MatrixRoomListFilter.Unread
                            RoomListFilter.Favourites -> MatrixRoomListFilter.Favorite
                            RoomListFilter.Invites -> MatrixRoomListFilter.Invite
                        }
                    }
                }
                .collect { filters ->
                    val result = MatrixRoomListFilter.All(filters)
                    roomListService.allRooms.updateFilter(result)
                }
        }

        return RoomListFiltersState(
            filterSelectionStates = filters,
            eventSink = ::handleEvent,
        )
    }
}
