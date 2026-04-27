/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.datasource

import android.util.Patterns
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomIdOrAlias
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.element.android.libraries.matrix.api.permalink.PermalinkData
import io.element.android.libraries.matrix.api.permalink.PermalinkParser
import io.element.android.libraries.matrix.api.roomlist.RoomSummary

private const val BUBBLE_ICON = "\uD83D\uDCAC"

/**
 * 格式化最新事件文案中的 mention / permalink。
 */
interface LatestEventMentionFormatter {
    /**
     * 把最新事件文本中的用户、房间 permalink 转换为更适合首页展示的短文案。
     *
     * @param text 原始事件文本。
     * @param roomSummary 当前房间摘要，用于辅助解析显示名。
     */
    fun format(text: CharSequence, roomSummary: RoomSummary): CharSequence
}

@SingleIn(SessionScope::class)
@ContributesBinding(SessionScope::class)
@Inject
/**
 * [LatestEventMentionFormatter] 的默认实现。
 */
class DefaultLatestEventMentionFormatter(
    private val permalinkParser: PermalinkParser,
    private val matrixClient: MatrixClient,
) : LatestEventMentionFormatter {
    /** 处理最新事件文本中的 URL，并将可识别的 permalink 转成更短的 mention 形式。 */
    override fun format(text: CharSequence, roomSummary: RoomSummary): CharSequence {
        val rawText = text.toString()
        val matches = Patterns.WEB_URL.toRegex().findAll(rawText).toList()
        if (matches.isEmpty()) return text

        var lastIndex = 0
        var changed = false
        val builder = StringBuilder(rawText.length)
        for (match in matches) {
            val replacement = formatPermalink(match.value, roomSummary)
            if (replacement == null) continue
            builder.append(rawText, lastIndex, match.range.first)
            builder.append(replacement)
            lastIndex = match.range.last + 1
            changed = true
        }
        if (!changed) return text
        builder.append(rawText, lastIndex, rawText.length)
        return builder.toString()
    }

    /** 解析单个 permalink 并生成替换文案。 */
    private fun formatPermalink(url: String, roomSummary: RoomSummary): String? {
        return when (val permalink = permalinkParser.parse(url)) {
            is PermalinkData.UserLink -> formatUserMention(permalink.userId.value, roomSummary)
            is PermalinkData.RoomLink -> {
                val roomMention = formatRoomMention(permalink.roomIdOrAlias, roomSummary)
                if (permalink.eventId != null) {
                    "$BUBBLE_ICON > $roomMention"
                } else {
                    roomMention
                }
            }
            is PermalinkData.FallbackLink -> null
            is PermalinkData.RoomEmailInviteLink -> null
        }
    }

    /** 将用户 permalink 格式化为展示名 mention。 */
    private fun formatUserMention(userId: String, roomSummary: RoomSummary): String {
        val displayName = roomSummary.info.heroes
            .firstNotNullOfOrNull { hero -> hero.displayName.takeIf { hero.userId.value == userId } }
            ?: roomSummary.info.inviter?.displayName.takeIf { roomSummary.info.inviter?.userId?.value == userId }
        return if (displayName != null) "@$displayName" else userId
    }

    /** 将房间 permalink 格式化为房间名 mention。 */
    private fun formatRoomMention(roomIdOrAlias: RoomIdOrAlias, roomSummary: RoomSummary): String {
        val roomName = allKnownRooms(roomSummary).firstNotNullOfOrNull { summary ->
            when {
                summary.info.id.toRoomIdOrAlias() == roomIdOrAlias -> summary.info.name
                summary.info.canonicalAlias?.toRoomIdOrAlias() == roomIdOrAlias -> summary.info.name
                else -> null
            }
        }
        return if (roomName != null) {
            "#$roomName"
        } else {
            roomIdOrAlias.identifier
        }
    }

    /** 汇总当前房间和已知房间列表，用于解析房间别名显示名。 */
    private fun allKnownRooms(roomSummary: RoomSummary): List<RoomSummary> {
        return buildList {
            add(roomSummary)
            addAll(matrixClient.roomListService.allRooms.filteredSummaries.replayCache.firstOrNull().orEmpty())
        }
    }
}
