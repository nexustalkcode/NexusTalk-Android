/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.deeplink.api.usecase

import android.app.Activity

/**
 * 邀请好友使用应用的用例接口。
 */
fun interface InviteFriendsUseCase {
    /**
     * 通过宿主 Activity 拉起系统分享。
     */
    fun execute(activity: Activity)
}
