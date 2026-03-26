/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.tools

import dev.zacsweers.metro.Inject

/** 恢复密钥的标准长度 */
private const val RECOVERY_KEY_LENGTH = 48

/** Base58 字母表用于验证恢复密钥格式 */
private const val BASE_58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"

/**
 * 恢复密钥工具类
 *
 * 提供恢复密钥格式验证等工具函数。
 */
@Inject
class RecoveryKeyTools {
    /**
     * 验证恢复密钥格式是否有效
     *
     * 恢复密钥必须是48个字符，且只包含Base58字母表中的字符。
     *
     * @param recoveryKey 待验证的恢复密钥
     * @return 如果格式有效返回 true，否则返回 false
     */
    fun isRecoveryKeyFormatValid(recoveryKey: String): Boolean {
        val recoveryKeyWithoutSpace = recoveryKey.replace("\\s+".toRegex(), "")
        return recoveryKeyWithoutSpace.length == RECOVERY_KEY_LENGTH && recoveryKeyWithoutSpace.all { BASE_58_ALPHABET.contains(it) }
    }
}
