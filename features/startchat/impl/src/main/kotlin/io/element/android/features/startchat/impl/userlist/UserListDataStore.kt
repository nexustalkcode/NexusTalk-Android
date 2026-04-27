/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.userlist

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.user.MatrixUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Inject
@SingleIn(SessionScope::class)
/**
 * 用户列表共享状态存储。
 *
 * 负责在会话作用域内保存已选用户和初始搜索词。
 */
class UserListDataStore {
    private val _selectedUsers: MutableStateFlow<List<MatrixUser>> = MutableStateFlow(emptyList())
    private val _initialQuery: MutableStateFlow<String?> = MutableStateFlow(null)

    /**
     * 选中用户。
     */
    fun selectUser(user: MatrixUser) {
        if (!_selectedUsers.value.contains(user)) {
            _selectedUsers.tryEmit(_selectedUsers.value.plus(user))
        }
    }

    /**
     * 将用户从选中列表移除。
     */
    fun removeUserFromSelection(user: MatrixUser) {
        _selectedUsers.tryEmit(_selectedUsers.value.minus(user))
    }

    /**
     * 设置下次打开时要使用的初始搜索词。
     */
    fun setInitialQuery(query: String?) {
        _initialQuery.tryEmit(query)
    }

    /**
     * 读取当前初始搜索词。
     */
    fun getInitialQuery(): String? = _initialQuery.value

    val selectedUsers = _selectedUsers.asStateFlow()
    val initialQuery = _initialQuery.asStateFlow()
}
