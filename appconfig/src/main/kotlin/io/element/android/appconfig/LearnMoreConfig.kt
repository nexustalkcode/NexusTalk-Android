/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

/**
 * 了解更多配置 (Learn More Configuration)
 *
 * 此对象包含应用中"了解更多"链接的URL配置。
 * 这些链接指向Element官方帮助文档，为用户提供关于各种安全和隐私功能的详细说明。
 */
object LearnMoreConfig {
    /** 加密相关帮助文档的URL链接 */
    const val ENCRYPTION_URL: String = ""
//    const val ENCRYPTION_URL: String = "https://element.io/help#encryption"

    /** 设备验证帮助文档的URL链接，指导用户如何验证他们的设备 */
    const val DEVICE_VERIFICATION_URL: String = ""
//    const val DEVICE_VERIFICATION_URL: String = "https://element.io/help#encryption-device-verification"

    /** 安全备份帮助文档的URL链接，解释如何安全地备份加密密钥 */
    const val SECURE_BACKUP_URL: String = ""
//    const val SECURE_BACKUP_URL: String = "https://element.io/help#encryption5"

    /** 身份更改帮助文档的URL链接，说明当信任的设备发生更改时如何处理 */
    const val IDENTITY_CHANGE_URL: String = ""
//    const val IDENTITY_CHANGE_URL: String = "https://element.io/help#encryption18"

    /** 历史消息可见性帮助文档的URL链接，解释端到端加密的历史消息共享功能 */
    const val HISTORY_VISIBLE_URL: String = ""
//    const val HISTORY_VISIBLE_URL: String = "https://element.io/en/help#e2ee-history-sharing"
}
