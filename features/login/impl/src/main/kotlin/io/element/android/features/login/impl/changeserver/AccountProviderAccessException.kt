/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.changeserver

/**
 * 账户提供商访问异常
 *
 * 当用户尝试连接到不被允许的账户提供商时抛出的异常。
 * 这是一个密封类，定义了两种访问被拒绝的情况：
 * 1. 需要 Element Pro 版本才能连接
 * 2. 服务器不在允许列表中
 *
 * @see DefaultAccountProviderAccessControl 默认访问控制实现
 */
sealed class AccountProviderAccessException : Exception() {
    /**
     * 需要 Element Pro 异常
     *
     * 当服务器要求使用 Element Pro 版本但用户未安装时抛出。
     *
     * @property unauthorizedAccountProviderTitle 未被授权的服务器标题
     * @property applicationId 所需的应用程序包名
     */
    data class NeedElementProException(
        val unauthorisedAccountProviderTitle: String,
        val applicationId: String,
    ) : AccountProviderAccessException()

    /**
     * 未授权账户提供商异常
     *
     * 当服务器不在允许连接的列表中时抛出。
     *
     * @property unauthorisedAccountProviderTitle 未被授权的服务器标题
     * @property authorisedAccountProviderTitles 允许连接的服务器标题列表
     */
    data class UnauthorizedAccountProviderException(
        val unauthorisedAccountProviderTitle: String,
        val authorisedAccountProviderTitles: List<String>,
    ) : AccountProviderAccessException()
}
