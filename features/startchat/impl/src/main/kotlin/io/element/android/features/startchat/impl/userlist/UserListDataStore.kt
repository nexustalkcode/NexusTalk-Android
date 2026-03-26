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
class UserListDataStore {
    private val _selectedUsers: MutableStateFlow<List<MatrixUser>> = MutableStateFlow(emptyList())
    private val _initialQuery: MutableStateFlow<String?> = MutableStateFlow(null)

    fun selectUser(user: MatrixUser) {
        if (!_selectedUsers.value.contains(user)) {
            _selectedUsers.tryEmit(_selectedUsers.value.plus(user))
        }
    }

    fun removeUserFromSelection(user: MatrixUser) {
        _selectedUsers.tryEmit(_selectedUsers.value.minus(user))
    }

    fun setInitialQuery(query: String?) {
        _initialQuery.tryEmit(query)
    }

    fun getInitialQuery(): String? = _initialQuery.value

    val selectedUsers = _selectedUsers.asStateFlow()
    val initialQuery = _initialQuery.asStateFlow()
}
