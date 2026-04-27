/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.userprofile.api

import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.core.UserId

/**
 * 创建用户资料页 Presenter 的工厂接口。
 */
fun interface UserProfilePresenterFactory {
    /**
     * 为指定用户创建资料页 Presenter。
     *
     * @param userId 需要展示资料页的用户 ID。
     */
    fun create(userId: UserId): Presenter<UserProfileState>
}
