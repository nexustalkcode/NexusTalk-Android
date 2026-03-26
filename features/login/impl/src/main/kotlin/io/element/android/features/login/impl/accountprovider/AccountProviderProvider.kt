/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.accountprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.appconfig.AuthenticationConfig

/**
 * 账户提供商预览参数提供者
 *
 * 用于在 Compose 预览中提供多种账户提供商状态的测试数据。
 * 继承自 PreviewParameterProvider，为预览功能提供样本数据。
 *
 * @see AccountProvider 账户提供商数据类
 * @see anAccountProvider 创建测试用账户提供商的辅助函数
 */
open class AccountProviderProvider : PreviewParameterProvider<AccountProvider> {
    override val values: Sequence<AccountProvider>
        get() = sequenceOf(
            anAccountProvider(),
            anAccountProvider().copy(subtitle = null),
            anAccountProvider().copy(subtitle = null, title = "invalid"),
            anAccountProvider().copy(subtitle = null, title = "Other", isPublic = false, isMatrixOrg = false),
            // Add other state here
        )
}

/**
 * 创建测试用账户提供商
 *
 * 辅助函数，用于在测试和预览中快速创建 AccountProvider 对象。
 *
 * @param url 服务器 URL，默认为 matrix.org
 * @param subtitle 服务器描述，默认为 matrix.org 的描述
 * @param isPublic 是否为公共服务器
 * @param isMatrixOrg 是否为 matrix.org 服务器
 * @return 配置好的 AccountProvider 对象
 */
fun anAccountProvider(
    url: String = AuthenticationConfig.MATRIX_ORG_URL,
    subtitle: String? = "Matrix.org is an open network for secure, decentralized communication.",
    isPublic: Boolean = true,
    isMatrixOrg: Boolean = true,
) = AccountProvider(
    url = url,
    subtitle = subtitle,
    isPublic = isPublic,
    isMatrixOrg = isMatrixOrg,
)
