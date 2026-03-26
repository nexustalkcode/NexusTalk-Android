/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.error

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * 更改服务器错误预览参数提供者
 *
 * 用于在 Compose 预览中提供不同状态的 ChangeServerError 测试数据。
 *
 * @see ChangeServerError 更改服务器错误类型
 */
class ChangeServerErrorProvider : PreviewParameterProvider<ChangeServerError> {
    override val values: Sequence<ChangeServerError>
        get() = sequenceOf(
            ChangeServerError.InvalidServer,
            ChangeServerError.Error(
                messageStr = "An error description",
            ),
            ChangeServerError.NeedElementPro(
                unauthorisedAccountProviderTitle = "element.io",
                applicationId = "io.element.enterprise",
            ),
            ChangeServerError.UnauthorizedAccountProvider(
                unauthorisedAccountProviderTitle = "element.io",
                authorisedAccountProviderTitles = listOf("provider.org", "provider.io"),
            ),
            ChangeServerError.SlidingSyncAlert,
            ChangeServerError.UnsupportedServer,
        )
}
