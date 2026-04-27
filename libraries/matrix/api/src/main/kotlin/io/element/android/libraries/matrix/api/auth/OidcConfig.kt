/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.auth

import io.element.android.libraries.matrix.api.BuildConfig

object OidcConfig {
    // 这些配置最终只会在运行期组装 OIDC 配置对象时读取，
    // 不要求继续保留编译期常量语义。
    // 改成普通 val 后，根模块就可以通过 shim 转发旧的 BuildConfig 路径，
    // 从而把根模块 namespace 与保留中的 :libraries:matrix:api 子模块拆开。
    val CLIENT_URI = BuildConfig.CLIENT_URI

    // Note: host must match with the host of CLIENT_URI
    val LOGO_URI = BuildConfig.LOGO_URI

    // Note: host must match with the host of CLIENT_URI
    val TOS_URI = BuildConfig.TOS_URI

    // Note: host must match with the host of CLIENT_URI
    val POLICY_URI = BuildConfig.POLICY_URI

    // Some homeservers/auth issuers don't support dynamic client registration, and have to be registered manually
    val STATIC_REGISTRATIONS = mapOf(
        "https://id.thirdroom.io/realms/thirdroom" to "elementx",
    )
}
