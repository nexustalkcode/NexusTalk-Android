/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.userlist

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.designsystem.theme.components.SearchBarResultState
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.room.recent.RecentDirectRoom
import io.element.android.libraries.matrix.api.room.recent.getRecentDirectRooms
import io.element.android.libraries.usersearch.api.UserRepository
import io.element.android.libraries.usersearch.api.UserSearchResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList

private const val MAX_SUGGESTIONS_COUNT = 5

@AssistedInject
/**
 * 默认的用户列表 Presenter 实现。
 *
 * 负责查询用户搜索结果、维护搜索状态，并提供最近私聊建议。
 */
class DefaultUserListPresenter(
    @Assisted val args: UserListPresenterArgs,
    @Assisted val userRepository: UserRepository,
    @Assisted val userListDataStore: UserListDataStore,
    private val matrixClient: MatrixClient,
) : UserListPresenter {
    @AssistedFactory
    @ContributesBinding(SessionScope::class)
    /**
     * DefaultUserListPresenter 的工厂绑定。
     */
    interface DefaultUserListFactory : UserListPresenter.Factory {
        override fun create(
            args: UserListPresenterArgs,
            userRepository: UserRepository,
            userListDataStore: UserListDataStore,
        ): DefaultUserListPresenter
    }

    /**
     * 生成用户列表状态并处理事件。
     */
    @Composable
    override fun present(): UserListState {
        var recentDirectRooms by remember { mutableStateOf(emptyList<RecentDirectRoom>()) }
        LaunchedEffect(Unit) {
            recentDirectRooms = matrixClient
                .getRecentDirectRooms()
                .take(MAX_SUGGESTIONS_COUNT)
                .toList()
        }
        val dataStoreInitialQuery by userListDataStore.initialQuery.collectAsState(initial = null)
        val effectiveInitialQuery = dataStoreInitialQuery ?: args.initialQuery
        var isSearchActive by rememberSaveable { mutableStateOf(effectiveInitialQuery != null) }
        val selectedUsers by userListDataStore.selectedUsers.collectAsState(emptyList())
        val queryState = rememberTextFieldState(initialText = effectiveInitialQuery ?: "")
        LaunchedEffect(effectiveInitialQuery) {
            if (effectiveInitialQuery != null) {
                isSearchActive = true
                queryState.edit { replace(0, length, effectiveInitialQuery) }
                if (dataStoreInitialQuery != null) {
                    userListDataStore.setInitialQuery(null)
                }
            }
        }
        var searchResults: SearchBarResultState<ImmutableList<UserSearchResult>> by remember {
            mutableStateOf(SearchBarResultState.Initial())
        }
        var showSearchLoader by remember { mutableStateOf(false) }

        val searchQuery = queryState.text.toString()
        LaunchedEffect(searchQuery, effectiveInitialQuery) {
            searchResults = SearchBarResultState.Initial()
            showSearchLoader = false
            userRepository.search(searchQuery).onEach { state ->
                showSearchLoader = state.isSearching
                searchResults = when {
                    state.results.isEmpty() && state.isSearching -> SearchBarResultState.Initial()
                    state.results.isEmpty() && !state.isSearching -> SearchBarResultState.NoResultsFound()
                    else -> SearchBarResultState.Results(state.results.toImmutableList())
                }
            }.launchIn(this)
        }

        fun handleEvent(event: UserListEvents) {
            when (event) {
                is UserListEvents.OnSearchActiveChanged -> isSearchActive = event.active
                is UserListEvents.AddToSelection -> userListDataStore.selectUser(event.matrixUser)
                is UserListEvents.RemoveFromSelection -> userListDataStore.removeUserFromSelection(event.matrixUser)
            }
        }

        return UserListState(
            searchQuery = queryState,
            searchResults = searchResults,
            selectedUsers = selectedUsers.toImmutableList(),
            isSearchActive = isSearchActive,
            showSearchLoader = showSearchLoader,
            selectionMode = args.selectionMode,
            recentDirectRooms = recentDirectRooms.toImmutableList(),
            eventSink = ::handleEvent,
        )
    }
}
