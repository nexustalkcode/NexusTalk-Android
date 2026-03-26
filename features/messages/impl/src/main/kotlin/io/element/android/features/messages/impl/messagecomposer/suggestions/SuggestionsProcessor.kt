/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.messagecomposer.suggestions

import dev.zacsweers.metro.Inject
import io.element.android.libraries.core.data.filterUpTo
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.room.RoomMember
import io.element.android.libraries.matrix.api.room.RoomMembersState
import io.element.android.libraries.matrix.api.room.RoomMembershipState
import io.element.android.libraries.matrix.api.room.roomMembers
import io.element.android.libraries.textcomposer.mentions.ResolvedSuggestion
import io.element.android.libraries.textcomposer.model.Suggestion
import io.element.android.libraries.textcomposer.model.SuggestionType

/**
 * 建议处理器
 *
 * 负责处理消息编辑器中的建议功能。当用户在编辑器中输入 "@"、"/" 或 "#" 时，此类会根据用户输入
 * 的内容搜索并返回匹配的建议列表，包括房间成员、房间别名、命令和表情符号等建议。
 *
 * @see Suggestion 建议类型
 * @see ResolvedSuggestion 已解析的建议
 * @see RoomMembersState 房间成员状态
 */
@Inject
class SuggestionsProcessor {
    /**
     * 处理建议
     *
     * 根据当前的建议输入、房间成员状态和房间别名建议列表，返回匹配的建议列表。
     *
     * @param suggestion 当前的建议输入
     * @param roomMembersState 房间成员状态，包含当前房间中的所有用户
     * @param roomAliasSuggestions 可用的房间别名建议列表
     * @param currentUserId 当前用户ID
     * @param canSendRoomMention 判断当前用户是否可以发送@房间提及的函数
     * @return 要显示的建议列表
     */
    suspend fun process(
        suggestion: Suggestion?,
        roomMembersState: RoomMembersState,
        roomAliasSuggestions: List<RoomAliasSuggestion>,
        currentUserId: UserId,
        canSendRoomMention: suspend () -> Boolean,
    ): List<ResolvedSuggestion> {
        suggestion ?: return emptyList()
        return when (suggestion.type) {
            SuggestionType.Mention -> {
                // Replace suggestions
                val members = roomMembersState.roomMembers()
                val matchingMembers = getMemberSuggestions(
                    query = suggestion.text,
                    roomMembers = members,
                    currentUserId = currentUserId,
                    canSendRoomMention = canSendRoomMention()
                )
                matchingMembers
            }
            SuggestionType.Room -> {
                roomAliasSuggestions
                    .filter { roomAliasSuggestion ->
                        // Filter by either room alias or room name (if available)
                        roomAliasSuggestion.roomAlias.value.contains(suggestion.text, ignoreCase = true) ||
                            roomAliasSuggestion.roomName?.contains(suggestion.text, ignoreCase = true) == true
                    }
                    .map {
                        ResolvedSuggestion.Alias(
                            roomAlias = it.roomAlias,
                            roomId = it.roomId,
                            roomName = it.roomName,
                            roomAvatarUrl = it.roomAvatarUrl,
                        )
                    }
            }
            SuggestionType.Command,
            SuggestionType.Emoji,
            is SuggestionType.Custom -> {
                // Clear suggestions
                emptyList()
            }
        }
    }

    /**
     * 获取房间成员建议
     *
     * 根据查询字符串在房间成员列表中搜索匹配的用户，返回符合条件且不是自己的已加入成员列表。
     *
     * @param query 查询字符串
     * @param roomMembers 房间成员列表
     * @param currentUserId 当前用户ID
     * @param canSendRoomMention 是否可以发送@房间提及
     * @return 匹配的房间成员建议列表
     */
    private fun getMemberSuggestions(
        query: String,
        roomMembers: List<RoomMember>?,
        currentUserId: UserId,
        canSendRoomMention: Boolean,
    ): List<ResolvedSuggestion> {
        return if (roomMembers.isNullOrEmpty()) {
            emptyList()
        } else {
            fun isJoinedMemberAndNotSelf(member: RoomMember): Boolean {
                return member.membership == RoomMembershipState.JOIN && currentUserId != member.userId
            }

            fun memberMatchesQuery(member: RoomMember, query: String): Boolean {
                return member.userId.value.contains(query, ignoreCase = true) ||
                    member.displayName?.contains(query, ignoreCase = true) == true
            }

            val matchingMembers = roomMembers
                // Search only in joined members, up to MAX_BATCH_ITEMS, exclude the current user
                .filterUpTo(MAX_BATCH_ITEMS) { member ->
                    isJoinedMemberAndNotSelf(member) && memberMatchesQuery(member, query)
                }
                .map(ResolvedSuggestion::Member)

            if ("room".contains(query) && canSendRoomMention) {
                listOf(ResolvedSuggestion.AtRoom) + matchingMembers
            } else {
                matchingMembers
            }
        }
    }

    companion object {
        // We don't want to retrieve thousands of members
        private const val MAX_BATCH_ITEMS = 100
    }
}
