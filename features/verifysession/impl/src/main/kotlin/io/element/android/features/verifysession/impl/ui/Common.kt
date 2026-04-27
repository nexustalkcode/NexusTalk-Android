/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.verifysession.impl.ui

import io.element.android.libraries.matrix.api.verification.SessionVerificationData
import io.element.android.libraries.matrix.api.verification.VerificationEmoji

/**
 * 构造一份 emoji 比对数据样例。
 */
internal fun aEmojisSessionVerificationData(
    emojiList: List<VerificationEmoji> = aVerificationEmojiList(),
): SessionVerificationData {
    return SessionVerificationData.Emojis(emojiList)
}

/**
 * 构造一份数字比对数据样例。
 */
internal fun aDecimalsSessionVerificationData(
    decimals: List<Int> = listOf(123, 456, 789),
): SessionVerificationData {
    return SessionVerificationData.Decimals(decimals)
}

/**
 * 构造默认的 SAS emoji 列表样例。
 */
private fun aVerificationEmojiList() = listOf(
    VerificationEmoji(number = 27),
    VerificationEmoji(number = 54),
    VerificationEmoji(number = 54),
    VerificationEmoji(number = 42),
    VerificationEmoji(number = 48),
    VerificationEmoji(number = 48),
    VerificationEmoji(number = 63),
)
