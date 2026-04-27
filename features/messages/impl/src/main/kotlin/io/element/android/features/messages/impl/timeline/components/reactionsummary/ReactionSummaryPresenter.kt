/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components.reactionsummary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.room.BaseRoom
import io.element.android.libraries.matrix.api.room.RoomMember
import io.element.android.libraries.matrix.api.room.roomMembers
import io.element.android.libraries.matrix.api.user.MatrixUser
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Inject
/**
 * reaction summary 底部弹层 Presenter。
 *
 * 负责接收 reaction 汇总目标，并为发送者补齐头像和显示名信息。
 */
class ReactionSummaryPresenter(
    private val room: BaseRoom,
) : Presenter<ReactionSummaryState> {
    /** 生成 reaction summary 状态并处理事件。 */
    @Composable
    override fun present(): ReactionSummaryState {
        val membersState by room.membersStateFlow.collectAsState()

        val target: MutableState<ReactionSummaryState.Summary?> = remember {
            mutableStateOf(null)
        }
        val targetWithAvatars = populateSenderAvatars(members = membersState.roomMembers().orEmpty().toImmutableList(), summary = target.value)

        fun handleEvent(event: ReactionSummaryEvents) {
            when (event) {
                is ReactionSummaryEvents.ShowReactionSummary -> target.value = ReactionSummaryState.Summary(
                    reactions = event.reactions.toImmutableList(),
                    selectedKey = event.selectedKey,
                    selectedEventId = event.eventId
                )
                ReactionSummaryEvents.Clear -> target.value = null
            }
        }
        return ReactionSummaryState(
            target = targetWithAvatars.value,
            eventSink = ::handleEvent,
        )
    }

    /** 根据房间成员数据为 reaction 发送者补齐用户信息。 */
    @Composable
    private fun populateSenderAvatars(members: ImmutableList<RoomMember>, summary: ReactionSummaryState.Summary?) = remember(summary) {
        derivedStateOf {
            summary?.let { summary ->
                summary.copy(reactions = summary.reactions.map { reaction ->
                    reaction.copy(senders = reaction.senders.map { sender ->
                        val member = members.firstOrNull { it.userId == sender.senderId }
                        val user = MatrixUser(
                            userId = sender.senderId,
                            displayName = member?.displayName,
                            avatarUrl = member?.avatarUrl
                        )
                        sender.copy(user = user)
                    }.toImmutableList())
                }.toImmutableList())
            }
        }
    }
}
