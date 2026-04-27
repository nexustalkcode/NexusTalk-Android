/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.signedout.impl

/**
 * 已登出页面可能触发的用户事件。
 */
sealed interface SignedOutEvents {
    /** 移除当前无效会话并重新登录。 */
    data object SignInAgain : SignedOutEvents
}
