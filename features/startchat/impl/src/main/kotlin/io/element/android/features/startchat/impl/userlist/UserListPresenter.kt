/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.userlist

import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.usersearch.api.UserRepository

/**
 * 用户列表 Presenter 接口。
 */
interface UserListPresenter : Presenter<UserListState> {
    /**
     * 创建用户列表 Presenter 的工厂接口。
     */
    interface Factory {
        fun create(
            args: UserListPresenterArgs,
            userRepository: UserRepository,
            userListDataStore: UserListDataStore,
        ): UserListPresenter
    }
}
