/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invitepeople.impl

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.theme.components.SearchBarResultState
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.components.aMatrixUser
import io.element.android.libraries.matrix.ui.components.aMatrixUserList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * 默认邀请人员状态预览参数提供者
 *
 * 实现Compose的PreviewParameterProvider接口，用于在预览中提供各种状态的DefaultInvitePeopleState。
 * 提供多种不同的状态场景用于UI预览和测试。
 *
 * 预览场景包括：
 * - 默认初始状态
 * - 可邀请状态（有选中用户）
 * - 搜索激活状态
 * - 搜索有结果状态
 * - 搜索无结果状态
 * - 包含已加入/已邀请用户的状态
 * - 未解析用户状态
 * - 搜索加载状态
 * - 房间加载失败状态
 * - 发送邀请加载状态
 *
 * @see DefaultInvitePeopleState 默认状态实现
 */
internal class DefaultInvitePeopleStateProvider : PreviewParameterProvider<DefaultInvitePeopleState> {
    /**
     * 预览状态序列
     *
     * 提供多种不同的状态用于UI预览，覆盖各种使用场景。
     */
    override val values: Sequence<DefaultInvitePeopleState>
        get() = sequenceOf(
            aDefaultInvitePeopleState(),
            aDefaultInvitePeopleState(canInvite = true, selectedUsers = aMatrixUserList().toImmutableList()),
            aDefaultInvitePeopleState(isSearchActive = true, searchQuery = "some query"),
            aDefaultInvitePeopleState(isSearchActive = true, searchQuery = "some query", selectedUsers = aMatrixUserList().toImmutableList()),
            aDefaultInvitePeopleState(isSearchActive = true, searchQuery = "some query", searchResults = SearchBarResultState.NoResultsFound()),
            // 包含已加入和已邀请用户的场景
            aDefaultInvitePeopleState(
                isSearchActive = true,
                canInvite = true,
                searchQuery = "some query",
                selectedUsers = persistentListOf(
                    aMatrixUser("@carol:server.org", "Carol")
                ),
                searchResults = SearchBarResultState.Results(
                    persistentListOf(
                        anInvitableUser(aMatrixUser("@alice:server.org")),
                        anInvitableUser(aMatrixUser("@bob:server.org", "Bob")),
                        anInvitableUser(aMatrixUser("@carol:server.org", "Carol"), isSelected = true),
                        anInvitableUser(aMatrixUser("@eve:server.org", "Eve"), isSelected = true, isAlreadyJoined = true),
                        anInvitableUser(aMatrixUser("@justin:server.org", "Justin"), isSelected = true, isAlreadyInvited = true),
                    )
                )
            ),
            // 包含未解析用户的场景
            aDefaultInvitePeopleState(
                isSearchActive = true,
                canInvite = true,
                searchQuery = "@alice:server.org",
                selectedUsers = persistentListOf(
                    aMatrixUser("@carol:server.org", "Carol")
                ),
                searchResults = SearchBarResultState.Results(
                    persistentListOf(
                        anInvitableUser(aMatrixUser("@alice:server.org"), isUnresolved = true),
                        anInvitableUser(aMatrixUser("@bob:server.org", "Bob")),
                    )
                )
            ),
            // 搜索加载中的场景
            aDefaultInvitePeopleState(
                isSearchActive = true,
                canInvite = true,
                searchQuery = "@alice:server.org",
                searchResults = SearchBarResultState.Results(
                    persistentListOf(
                        anInvitableUser(aMatrixUser("@alice:server.org"), isUnresolved = true),
                    )
                ),
                showSearchLoader = true,
            ),
            // 房间加载失败的场景
            aDefaultInvitePeopleState(room = AsyncData.Failure(Exception("Room not found"))),
            // 发送邀请加载中的场景
            aDefaultInvitePeopleState(
                canInvite = false,
                selectedUsers = aMatrixUserList().toImmutableList(),
                sendInvitesAction = AsyncAction.Loading,
            ),
        )
}

/**
 * 创建预览用的InvitableUser
 *
 * 辅助函数，用于快速创建不同配置的InvitableUser对象。
 *
 * @param matrixUser Matrix用户对象
 * @param isSelected 是否已选中，默认false
 * @param isAlreadyJoined 是否已加入房间，默认false
 * @param isAlreadyInvited 是否已收到邀请，默认false
 * @param isUnresolved 是否是未解析用户，默认false
 * @return 配置好的InvitableUser对象
 */
private fun anInvitableUser(
    matrixUser: MatrixUser,
    isSelected: Boolean = false,
    isAlreadyJoined: Boolean = false,
    isAlreadyInvited: Boolean = false,
    isUnresolved: Boolean = false,
) = InvitableUser(
    matrixUser = matrixUser,
    isSelected = isSelected,
    isAlreadyJoined = isAlreadyJoined,
    isAlreadyInvited = isAlreadyInvited,
    isUnresolved = isUnresolved,
)

/**
 * 创建预览用的默认邀请人员状态
 *
 * 辅助函数，用于快速创建不同配置的预览状态。
 *
 * @param room 房间加载状态，默认成功
 * @param canInvite 是否可以发送邀请，默认false
 * @param searchQuery 搜索查询字符串，默认空字符串
 * @param searchResults 搜索结果状态，默认初始状态
 * @param selectedUsers 已选用户列表，默认空列表
 * @param isSearchActive 搜索是否激活，默认false
 * @param showSearchLoader 是否显示搜索加载器，默认false
 * @param sendInvitesAction 发送邀请操作状态，默认未初始化
 * @param suggestions 推荐用户列表，默认使用模拟用户列表
 * @return 配置好的DefaultInvitePeopleState对象
 */
private fun aDefaultInvitePeopleState(
    room: AsyncData<Unit> = AsyncData.Success(Unit),
    canInvite: Boolean = false,
    searchQuery: String = "",
    searchResults: SearchBarResultState<ImmutableList<InvitableUser>> = SearchBarResultState.Initial(),
    selectedUsers: ImmutableList<MatrixUser> = persistentListOf(),
    isSearchActive: Boolean = false,
    showSearchLoader: Boolean = false,
    sendInvitesAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    suggestions: List<InvitableUser> = aMatrixUserList()
        .take(5)
        .map { user -> anInvitableUser(matrixUser = user, isSelected = user in selectedUsers) },
): DefaultInvitePeopleState {
    return DefaultInvitePeopleState(
        room = room,
        canInvite = canInvite,
        searchQuery = TextFieldState(initialText = searchQuery),
        searchResults = searchResults,
        selectedUsers = selectedUsers,
        isSearchActive = isSearchActive,
        showSearchLoader = showSearchLoader,
        sendInvitesAction = sendInvitesAction,
        suggestions = suggestions.toImmutableList(),
        eventSink = {},
    )
}
