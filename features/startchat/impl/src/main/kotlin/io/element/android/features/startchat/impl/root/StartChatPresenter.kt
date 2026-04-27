/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.features.startchat.api.StartDMAction
import io.element.android.features.startchat.impl.userlist.SelectionMode
import io.element.android.features.startchat.impl.userlist.UserListDataStore
import io.element.android.features.startchat.impl.userlist.UserListPresenter
import io.element.android.features.startchat.impl.userlist.UserListPresenterArgs
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.usersearch.api.UserRepository
import kotlinx.coroutines.launch

@Inject
/**
 * 开始聊天主页 Presenter。
 *
 * 负责组合用户搜索列表、发起私聊动作和功能开关状态，
 * 生成主页所需的完整展示状态。
 */
class StartChatPresenter(
    private val presenterFactory: UserListPresenter.Factory,
    private val userRepository: UserRepository,
    private val userListDataStore: UserListDataStore,
    private val startDMAction: StartDMAction,
    private val buildMeta: BuildMeta,
    private val featureFlagService: FeatureFlagService,
) : Presenter<StartChatState> {
    /**
     * 生成开始聊天主页状态并处理页面事件。
     */
    @Composable
    override fun present(): StartChatState {
        val dataStoreInitialQuery by userListDataStore.initialQuery.collectAsState(initial = null)
        val presenter = presenterFactory.create(
            UserListPresenterArgs(
                selectionMode = SelectionMode.Single,
                initialQuery = dataStoreInitialQuery,
            ),
            userRepository,
            userListDataStore,
        )
        val userListState = presenter.present()

        val localCoroutineScope = rememberCoroutineScope()
        val startDmActionState: MutableState<AsyncAction<RoomId>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        val isRoomDirectorySearchEnabled by remember {
            featureFlagService.isFeatureEnabledFlow(FeatureFlags.RoomDirectorySearch)
        }.collectAsState(initial = false)

        fun handleEvent(event: StartChatEvents) {
            when (event) {
                is StartChatEvents.StartDM -> localCoroutineScope.launch {
                    startDMAction.execute(
                        matrixUser = event.matrixUser,
                        createIfDmDoesNotExist = startDmActionState.value is AsyncAction.Confirming,
                        actionState = startDmActionState,
                    )
                }
                StartChatEvents.CancelStartDM -> startDmActionState.value = AsyncAction.Uninitialized
            }
        }

        return StartChatState(
            applicationName = buildMeta.applicationName,
            userListState = userListState,
            startDmAction = startDmActionState.value,
            isRoomDirectorySearchEnabled = isRoomDirectorySearchEnabled,
            eventSink = ::handleEvent,
        )
    }
}
