/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.userlist

/**
 * 用户列表 Presenter 输入参数。
 */
data class UserListPresenterArgs(
    val selectionMode: SelectionMode,
    val initialQuery: String? = null,
)

/**
 * 用户列表选择模式。
 */
enum class SelectionMode {
    Single,
    Multiple,
}
