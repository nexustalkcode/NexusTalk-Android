/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.api.accesscontrol

/**
 * 账户提供商访问控制接口
 *
 * 定义检查是否允许连接到特定账户提供商的功能接口。
 * 用于企业环境中的访问策略控制。
 *
 * @see AccountProviderAccessControl 访问控制接口
 */
interface AccountProviderAccessControl {
    /**
     * 检查是否允许连接到账户提供商
     *
     * @param accountProviderUrl 账户提供商 URL
     * @return 是否允许连接
     */
    suspend fun isAllowedToConnectToAccountProvider(accountProviderUrl: String): Boolean
}
