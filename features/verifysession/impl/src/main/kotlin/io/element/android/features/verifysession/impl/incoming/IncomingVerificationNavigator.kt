/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.verifysession.impl.incoming

/**
 * 传入验证流程内部使用的导航接口。
 */
fun interface IncomingVerificationNavigator {
    /**
     * 结束当前验证流程。
     */
    fun onFinish()
}
