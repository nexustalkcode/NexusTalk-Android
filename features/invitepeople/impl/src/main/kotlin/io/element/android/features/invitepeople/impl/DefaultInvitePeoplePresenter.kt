/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invitepeople.impl

import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.invitepeople.api.InvitePeopleEvents
import io.element.android.features.invitepeople.api.InvitePeoplePresenter
import io.element.android.features.invitepeople.api.InvitePeopleState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.map
import io.element.android.libraries.architecture.runCatchingUpdatingState
import io.element.android.libraries.architecture.runUpdatingState
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.designsystem.theme.components.SearchBarResultState
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.room.RoomMember
import io.element.android.libraries.matrix.api.room.RoomMembershipState
import io.element.android.libraries.matrix.api.room.filterMembers
import io.element.android.libraries.matrix.api.room.recent.getRecentDirectRooms
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.libraries.usersearch.api.UserRepository
import io.element.android.services.apperror.api.AppErrorStateService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 推荐用户最大数量
 *
 * 在推荐列表中显示的最近私聊用户数量上限。
 */
private const val MAX_SUGGESTIONS_COUNT = 5

/**
 * 默认邀请人员Presenter实现
 *
 * 实现InvitePeoplePresenter接口，负责邀请人员页面的核心业务逻辑。
 * 管理用户搜索、选择、推荐和邀请发送等完整流程。
 *
 * 主要功能：
 * - 从最近私聊用户中获取推荐列表
 * - 提供用户搜索功能
 * - 管理用户选择状态
 * - 处理批量发送邀请操作
 *
 * @property joinedRoom 已加入的房间实例，可为null
 * @property roomId 房间ID
 * @property userRepository 用户搜索仓库
 * @property coroutineDispatchers 协程调度器
 * @property sessionCoroutineScope 会话级协程作用域
 * @property appErrorStateService 应用错误状态服务
 * @property matrixClient Matrix客户端
 */
@AssistedInject
class DefaultInvitePeoplePresenter(
    @Assisted private val joinedRoom: JoinedRoom?,
    @Assisted private val roomId: RoomId,
    private val userRepository: UserRepository,
    private val coroutineDispatchers: CoroutineDispatchers,
    @SessionCoroutineScope private val sessionCoroutineScope: CoroutineScope,
    private val appErrorStateService: AppErrorStateService,
    private val matrixClient: MatrixClient,
) : InvitePeoplePresenter {
    /**
     * Presenter工厂实现
     *
     * 使用@AssistedInject和@ContributesBinding注解实现依赖注入。
     * 允许通过SessionScope创建DefaultInvitePeoplePresenter实例。
     */
    @AssistedFactory
    @ContributesBinding(SessionScope::class)
    interface Factory : InvitePeoplePresenter.Factory {
        /**
         * 创建DefaultInvitePeoplePresenter实例
         *
         * @param joinedRoom 已加入的房间实例
         * @param roomId 房间ID
         * @return DefaultInvitePeoplePresenter实例
         */
        override fun create(joinedRoom: JoinedRoom?, roomId: RoomId): DefaultInvitePeoplePresenter
    }

    /**
     * 创建并返回邀请人员页面状态
     *
     * Compose Composable函数，负责初始化和管理页面状态。
     * 包含以下状态管理：
     * - 房间成员列表 (roomMembers)
     * - 已选用户列表 (selectedUsers)
     * - 搜索结果 (searchResults)
     * - 搜索查询内容 (queryState)
     * - 搜索激活状态 (searchActive)
     * - 搜索加载状态 (showSearchLoader)
     * - 发送邀请操作状态 (sendInvitesAction)
     * - 推荐用户列表 (suggestions)
     *
     * @return 包含所有状态信息的InvitePeopleState对象
     */
    @Composable
    override fun present(): InvitePeopleState {
        // 房间成员列表的异步状态
        val roomMembers = remember { mutableStateOf<AsyncData<ImmutableList<RoomMember>>>(AsyncData.Loading()) }
        // 已选择要邀请的用户列表
        val selectedUsers = remember { mutableStateOf<ImmutableList<MatrixUser>>(persistentListOf()) }
        // 搜索结果的显示状态
        val searchResults = remember { mutableStateOf<SearchBarResultState<ImmutableList<InvitableUser>>>(SearchBarResultState.Initial()) }
        // 搜索框的文本状态
        val queryState = rememberTextFieldState()
        // 搜索栏是否处于激活状态
        var searchActive by rememberSaveable { mutableStateOf(false) }
        // 是否显示搜索加载指示器
        val showSearchLoader = rememberSaveable { mutableStateOf(false) }
        // 发送邀请操作的异步状态
        val sendInvitesAction = remember { mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized) }

        // 获取最近私聊房间作为推荐用户来源
        val recentDirectRooms by produceState(emptyList(), roomMembers.value) {
            if (roomMembers.value.isSuccess()) {
                // 过滤出当前房间中活跃的成员ID
                val activeMemberIds = roomMembers.value.dataOrNull().orEmpty()
                    .filter { it.membership.isActive() }
                    .mapTo(mutableSetOf()) { it.userId }

                // 获取最近私聊房间，排除已是房间成员的用户
                value = matrixClient.getRecentDirectRooms()
                    .filterNot { it.matrixUser.userId in activeMemberIds }
                    .take(MAX_SUGGESTIONS_COUNT)
                    .toList()
            }
        }

        // 将最近私聊房间转换为可邀请用户列表
        // 使用derivedStateOf确保在selectedUsers变化时自动更新
        val suggestions by remember {
            derivedStateOf {
                recentDirectRooms.map { recentDirectRoom ->
                    InvitableUser(
                        matrixUser = recentDirectRoom.matrixUser,
                        isSelected = recentDirectRoom.matrixUser in selectedUsers.value,
                        isAlreadyJoined = false,
                        isAlreadyInvited = false,
                        isUnresolved = false,
                    )
                }.toImmutableList()
            }
        }

        // 房间信息的状态管理
        val room by produceState(if (joinedRoom != null) AsyncData.Success(joinedRoom) else AsyncData.Loading()) {
            if (joinedRoom == null) {
                // 如果没有传入joinedRoom，则通过roomId获取
                val result = matrixClient.getJoinedRoom(roomId)
                value = if (result == null) {
                    AsyncData.Failure(Exception("Room not found"))
                } else {
                    AsyncData.Success(result)
                }
            }
        }

        // 当房间加载成功时，获取房间成员列表
        LaunchedEffect(room.isSuccess()) {
            room.dataOrNull()?.let {
                fetchMembers(it, roomMembers)
            }
        }

        // 监听搜索查询变化，执行搜索操作
        val searchQuery = queryState.text.toString()
        LaunchedEffect(searchQuery, roomMembers) {
            performSearch(
                searchResults = searchResults,
                roomMembers = roomMembers,
                selectedUsers = selectedUsers,
                showSearchLoader = showSearchLoader,
                searchQuery = searchQuery
            )
        }

        /**
         * 处理用户交互事件
         *
         * 根据不同的事件类型执行相应的业务逻辑：
         * - OnSearchActiveChanged: 切换搜索激活状态，关闭时清空搜索框
         * - ToggleUser: 切换用户的选择状态
         * - SendInvites: 发送选中的用户邀请
         * - CloseSearch: 关闭搜索并清空搜索框
         */
        fun handleEvent(event: InvitePeopleEvents) {
            when (event) {
                is DefaultInvitePeopleEvents.OnSearchActiveChanged -> {
                    searchActive = event.active
                    if (!event.active) {
                        // 关闭搜索时清空搜索框内容
                        queryState.clearText()
                    }
                }

                is DefaultInvitePeopleEvents.ToggleUser -> {
                    // 更新已选用户列表中的用户选择状态
                    selectedUsers.toggleUser(event.user)
                    // 更新搜索结果中的用户选择状态
                    searchResults.toggleUser(event.user)
                    // suggestions会通过derivedStateOf自动更新
                }
                is InvitePeopleEvents.SendInvites -> {
                    // 发送邀请给选中的用户
                    room.dataOrNull()?.let {
                        sessionCoroutineScope.sendInvites(it, selectedUsers.value, sendInvitesAction)
                    }
                }
                is InvitePeopleEvents.CloseSearch -> {
                    // 关闭搜索并清空搜索框
                    searchActive = false
                    queryState.clearText()
                }
            }
        }

        return DefaultInvitePeopleState(
            room = room.map { },
            canInvite = selectedUsers.value.isNotEmpty() && !sendInvitesAction.value.isLoading(),
            selectedUsers = selectedUsers.value,
            searchQuery = queryState,
            isSearchActive = searchActive,
            searchResults = searchResults.value,
            showSearchLoader = showSearchLoader.value,
            sendInvitesAction = sendInvitesAction.value,
            suggestions = suggestions,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 发送邀请操作
     *
     * 在协程作用域中执行批量发送邀请的逻辑。
     * 遍历所有选中的用户，逐个发送邀请，并处理邀请失败的情况。
     *
     * @param room 已加入的房间，用于执行邀请操作
     * @param selectedUsers 选中的要邀请的用户列表
     * @param sendInvitesAction 发送邀请操作的状态，用于更新UI
     */
    private fun CoroutineScope.sendInvites(
        room: JoinedRoom,
        selectedUsers: List<MatrixUser>,
        sendInvitesAction: MutableState<AsyncAction<Unit>>,
    ) = launch {
        sendInvitesAction.runUpdatingState {
            // 遍历所有选中的用户，逐一发送邀请
            val anyInviteFailed = selectedUsers
                .map { room.inviteUserById(it.userId) }
                .any { it.isFailure }

            // 如果有任何邀请失败，显示错误提示
            if (anyInviteFailed) {
                appErrorStateService.showError(
                    titleRes = CommonStrings.common_unable_to_invite_title,
                    bodyRes = CommonStrings.common_unable_to_invite_message,
                )
            }

            Result.success(Unit)
        }
    }

    /**
     * 切换已选用户列表中的用户选择状态
     *
     * 扩展函数，用于在MutableState<ImmutableList<MatrixUser>>中切换用户选择状态。
     * 如果用户已选中则移除，否则添加到选中列表。
     *
     * @param user 要切换选择状态的Matrix用户
     */
    @JvmName("toggleUserInSelectedUsers")
    private fun MutableState<ImmutableList<MatrixUser>>.toggleUser(user: MatrixUser) {
        value = if (value.contains(user)) {
            // 用户已选中，从列表中移除
            value.filterNot { it.userId == user.userId }
        } else {
            // 用户未选中，添加到列表
            value + user
        }.toImmutableList()
    }

    /**
     * 切换搜索结果中的用户选择状态
     *
     * 扩展函数，用于在搜索结果状态中切换用户选择状态。
     * 仅在搜索结果为Results状态时生效。
     *
     * @param user 要切换选择状态的Matrix用户
     */
    @JvmName("toggleUserInSearchResults")
    private fun MutableState<SearchBarResultState<ImmutableList<InvitableUser>>>.toggleUser(user: MatrixUser) {
        val existingResults = value
        if (existingResults is SearchBarResultState.Results) {
            value = SearchBarResultState.Results(
                existingResults.results.map { iu ->
                    if (iu.matrixUser == user) {
                        // 找到目标用户，反转其选择状态
                        iu.copy(isSelected = !iu.isSelected)
                    } else {
                        iu
                    }
                }.toImmutableList()
            )
        }
    }

    /**
     * 执行用户搜索操作
     *
     * 通过用户仓库执行搜索，并根据房间成员状态判断用户是否已加入或已受邀。
     * 在IO线程上执行搜索操作以提高性能。
     *
     * @param searchResults 搜索结果的状态容器
     * @param roomMembers 房间成员列表的状态
     * @param selectedUsers 已选用户列表的状态
     * @param showSearchLoader 是否显示加载指示器的状态
     * @param searchQuery 搜索查询字符串
     */
    private suspend fun performSearch(
        searchResults: MutableState<SearchBarResultState<ImmutableList<InvitableUser>>>,
        roomMembers: MutableState<AsyncData<ImmutableList<RoomMember>>>,
        selectedUsers: MutableState<ImmutableList<MatrixUser>>,
        showSearchLoader: MutableState<Boolean>,
        searchQuery: String,
    ) = withContext(coroutineDispatchers.io) {
        // 重置搜索结果状态
        searchResults.value = SearchBarResultState.Initial()
        showSearchLoader.value = false
        // 获取当前房间成员列表
        val joinedMembers = roomMembers.value.dataOrNull().orEmpty()

        // 执行搜索并处理结果
        userRepository.search(searchQuery).onEach { state ->
            showSearchLoader.value = state.isSearching
            searchResults.value = when {
                // 搜索中且无结果时显示初始状态
                state.results.isEmpty() && state.isSearching -> SearchBarResultState.Initial()
                // 搜索完成但无结果时显示无结果提示
                state.results.isEmpty() && !state.isSearching -> SearchBarResultState.NoResultsFound()
                // 有搜索结果时转换为InvitableUser列表
                else -> SearchBarResultState.Results(state.results.map { result ->
                    // 检查用户是否已是房间成员或已受邀
                    val existingMembership = joinedMembers.firstOrNull { j -> j.userId == result.matrixUser.userId }?.membership
                    val isJoined = existingMembership == RoomMembershipState.JOIN
                    val isInvited = existingMembership == RoomMembershipState.INVITE
                    InvitableUser(
                        matrixUser = result.matrixUser,
                        isSelected = selectedUsers.value.contains(result.matrixUser),
                        isAlreadyJoined = isJoined,
                        isAlreadyInvited = isInvited,
                        isUnresolved = result.isUnresolved,
                    )
                }.toImmutableList())
            }
        }.launchIn(this)
    }

    /**
     * 获取房间成员列表
     *
     * 从指定房间中获取所有成员列表。
     * 使用runCatchingUpdatingState处理可能发生的异常。
     *
     * @param room 要获取成员的已加入房间
     * @param roomMembers 成员列表的状态容器
     */
    private suspend fun fetchMembers(
        room: JoinedRoom,
        roomMembers: MutableState<AsyncData<ImmutableList<RoomMember>>>
    ) {
        suspend {
            room.filterMembers("", coroutineDispatchers.io).toImmutableList()
        }.runCatchingUpdatingState(roomMembers)
    }
}
