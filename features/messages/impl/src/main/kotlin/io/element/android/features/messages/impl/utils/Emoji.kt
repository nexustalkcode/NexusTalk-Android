/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import com.sigpwned.emoji4j.core.Grapheme.Type.EMOJI
import com.sigpwned.emoji4j.core.Grapheme.Type.PICTOGRAPHIC
import com.sigpwned.emoji4j.core.GraphemeMatchResult
import com.sigpwned.emoji4j.core.GraphemeMatcher
import io.element.android.features.messages.impl.timeline.model.event.AN_EMOJI_ONLY_TEXT

/**
 * 检查字符串是否只包含表情符号或象形文字
 *
 * 此函数用于判断一个字符串是否完全由表情符号（emoji）或象形图形组成。
 * 常用于判断消息是否只包含表情符号，据此可以显示特殊的大号表情消息样式。
 *
 * @return 如果字符串只包含表情符号或象形文字返回true，否则返回false
 */
@Composable
fun String.containsOnlyEmojis(): Boolean {
    if (LocalInspectionMode.current) return this == AN_EMOJI_ONLY_TEXT
    if (isEmpty()) return false
    return containsOnlyEmojisInternal()
}

internal fun String.containsOnlyEmojisInternal(): Boolean {
    val matcher = GraphemeMatcher(this)
    var m: GraphemeMatchResult? = null
    var contiguous = true
    var previous = 0
    while (contiguous && matcher.find()) {
        m = matcher.toMatchResult()
        // Many non-"emoji" characters are pictographics. We only want to identify this specific range
        // https://en.wikipedia.org/wiki/Miscellaneous_Symbols_and_Pictographs
        val isEmoji = m!!.grapheme().type == EMOJI || m.grapheme().type == PICTOGRAPHIC && m.group() in "🌍".."🗺"
        contiguous = isEmoji and (m.start() == previous)
        previous = m.end()
    }

    return contiguous and (m?.end() == length)
}
