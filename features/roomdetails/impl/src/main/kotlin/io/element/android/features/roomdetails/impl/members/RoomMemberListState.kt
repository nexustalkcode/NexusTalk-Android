/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.members

import androidx.compose.foundation.text.input.TextFieldState
import io.element.android.features.roommembermoderation.api.RoomMemberModerationState
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.core.bool.orFalse
import io.element.android.libraries.matrix.api.encryption.identity.IdentityState
import io.element.android.libraries.matrix.api.room.RoomMember
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 房间成员列表状态数据类
 *
 * 表示房间成员列表界面的当前状态，包含成员搜索、过滤和 modération 状态。
 *
 * @property roomMembers 房间成员的异步数据，用于判断是否显示封禁成员区域
 * @property filteredRoomMembers 过滤后的成员列表
 * @property searchQuery 搜索查询输入框状态
 * @property canInvite 当前用户是否有邀请权限
 * @property selectedSection 当前选中的成员区域
 * @property moderationState 成员 moderation 状态
 * @property eventSink 事件处理函数
 */
data class RoomMemberListState(
    // Only used to know if we can show the banned section
    private val roomMembers: AsyncData<RoomMembers>,
    val filteredRoomMembers: AsyncData<RoomMembers>,
    val searchQuery: TextFieldState,
    val canInvite: Boolean,
    val selectedSection: SelectedSection,
    val moderationState: RoomMemberModerationState,
    val eventSink: (RoomMemberListEvents) -> Unit,
) {
    /** 是否显示封禁成员区域 */
    val showBannedSection: Boolean = moderationState.permissions.canBan && roomMembers.dataOrNull()?.banned?.isNotEmpty() == true
}

/**
 * 已选区域枚举
 *
 * 定义成员列表中可选择的显示区域。
 */
enum class SelectedSection {
    /** 普通成员区域 */
    MEMBERS,
    /** 封禁成员区域 */
    BANNED
}

/**
 * 房间成员数据类
 *
 * 按状态分组存储房间成员列表。
 *
 * @property invited 已邀请的成员列表
 * @property joined 已加入的成员列表
 * @property banned 已封禁的成员列表
 */
data class RoomMembers(
    val invited: ImmutableList<RoomMemberWithIdentityState>,
    val joined: ImmutableList<RoomMemberWithIdentityState>,
    val banned: ImmutableList<RoomMemberWithIdentityState>,
) {
    /**
     * 判断指定区域是否为空
     *
     * @param selectedSection 要检查的区域
     * @return Boolean 是否为空
     */
    fun isEmpty(section: SelectedSection): Boolean {
        return when (section) {
            SelectedSection.MEMBERS -> invited.isEmpty() && joined.isEmpty()
            SelectedSection.BANNED -> banned.isEmpty()
        }
    }

    /**
     * 根据查询条件过滤成员列表
     *
     * @param query 查询关键词，支持模糊匹配
     * @return RoomMembers 过滤后的成员列表
     */
    fun filter(query: String): RoomMembers {
        if (query.isBlank()) {
            return this
        }
        val filterPredicate = { member: RoomMemberWithIdentityState ->
            member.roomMember.userId.value.contains(query, ignoreCase = true) ||
                member.roomMember.displayName?.contains(query, ignoreCase = true).orFalse()
        }
        return RoomMembers(
            invited = invited.filter(filterPredicate).toImmutableList(),
            joined = joined.filter(filterPredicate).toImmutableList(),
            banned = banned.filter(filterPredicate).toImmutableList(),
        )
    }
}

/**
 * 带身份状态的房间成员数据类
 *
 * 组合房间成员信息和加密身份验证状态。
 *
 * @property roomMember 房间成员信息
 * @property identityState 身份验证状态
 */
data class RoomMemberWithIdentityState(
    val roomMember: RoomMember,
    val identityState: IdentityState?,
)
