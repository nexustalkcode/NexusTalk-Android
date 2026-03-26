/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.accesscontrol

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.features.login.api.accesscontrol.AccountProviderAccessControl
import io.element.android.features.login.impl.changeserver.AccountProviderAccessException
import io.element.android.libraries.core.uri.ensureProtocol
import io.element.android.libraries.wellknown.api.WellknownRetriever

/**
 * 默认账户提供商访问控制实现
 *
 * 提供 AccountProviderAccessControl 接口的默认实现。
 * 负责验证用户是否有权连接到特定的账户提供商（homeserver）。
 * 该类会检查是否为 Element Pro 版本，并验证服务器是否在允许列表中。
 *
 * @param enterpriseService 企业服务，用于获取企业配置和允许的服务器列表
 * @param wellknownRetriever WellKnown 信息检索器，用于获取服务器的 Element 配置
 * @throws AccountProviderAccessException 当连接不被允许时抛出异常
 * @see AccountProviderAccessControl 访问控制接口
 */
@ContributesBinding(AppScope::class)
class DefaultAccountProviderAccessControl(
    private val enterpriseService: EnterpriseService,
    private val wellknownRetriever: WellknownRetriever,
) : AccountProviderAccessControl {
    override suspend fun isAllowedToConnectToAccountProvider(accountProviderUrl: String) = try {
        assertIsAllowedToConnectToAccountProvider(
            title = accountProviderUrl,
            accountProviderUrl = accountProviderUrl,
        )
        true
    } catch (_: AccountProviderAccessException) {
        false
    }

    @Throws(AccountProviderAccessException::class)
    suspend fun assertIsAllowedToConnectToAccountProvider(
        title: String,
        accountProviderUrl: String,
    ) {
        if (enterpriseService.isEnterpriseBuild.not()) {
            // Ensure that Element Pro is not required for this account provider
            val wellKnown = wellknownRetriever.getElementWellKnown(
                baseUrl = accountProviderUrl.ensureProtocol(),
            ).dataOrNull()
            if (wellKnown?.enforceElementPro == true) {
                throw AccountProviderAccessException.NeedElementProException(
                    unauthorisedAccountProviderTitle = title,
                    applicationId = ELEMENT_PRO_APPLICATION_ID,
                )
            }
        }
        if (enterpriseService.isAllowedToConnectToHomeserver(accountProviderUrl).not()) {
            throw AccountProviderAccessException.UnauthorizedAccountProviderException(
                unauthorisedAccountProviderTitle = title,
                authorisedAccountProviderTitles = enterpriseService.defaultHomeserverList(),
            )
        }
    }

    companion object {
        /**
         * Element Pro 应用程序 ID
         *
         * 用于标识需要 Element Pro 版本的应用程序包名。
         * 当服务器要求使用 Element Pro 时，会将此 ID 传递给相关异常。
         */
        const val ELEMENT_PRO_APPLICATION_ID = "io.element.enterprise"
    }
}
