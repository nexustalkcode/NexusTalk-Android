/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.model

import androidx.compose.runtime.Immutable
import io.element.android.features.invite.api.InviteData
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.RoomNotificationMode
import io.element.android.libraries.matrix.ui.model.InviteSender
import kotlinx.collections.immutable.ImmutableList

/**
 * 房间列表摘要数据类
 *
 * 表示房间列表中单个房间的完整信息，用于渲染房间列表项。
 *
 * @property id 房间唯一标识符
 * @property displayType 房间显示类型（占位符、房间、邀请、敲击）
 * @property roomId 房间 ID
 * @property name 房间名称
 * @property canonicalAlias 房间规范别名
 * @property numberOfUnreadMessages 未读消息数量
 * @property numberOfUnreadMentions 未读提及数量
 * @property numberOfUnreadNotifications 未读通知数量
 * @property isMarkedUnread 是否被标记为未读
 * @property timestamp 时间戳
 * @property latestEvent 最新事件
 * @property avatarData 头像数据
 * @property userDefinedNotificationMode 用户定义的通知模式
 * @property hasRoomCall 是否有房间通话
 * @property isDirect 是否为直接消息
 * @property isDm 是否为 DM
 * @property isFavorite 是否为收藏
 * @property inviteSender 邀请发送者
 * @property isTombstoned 是否已被迁移
 * @property heroes 房间成员头像列表
 * @property isSpace 是否为空间
 */
@Immutable
data class RoomListRoomSummary(
    /** 房间唯一标识符 */
    val id: String,
    /** 房间显示类型 */
    val displayType: RoomSummaryDisplayType,
    /** 房间 ID */
    val roomId: RoomId,
    /** 房间名称 */
    val name: String?,
    /** 房间规范别名 */
    val canonicalAlias: RoomAlias?,
    /** 未读消息数量 */
    val numberOfUnreadMessages: Long,
    /** 未读提及数量 */
    val numberOfUnreadMentions: Long,
    /** 未读通知数量 */
    val numberOfUnreadNotifications: Long,
    /** 是否被标记为未读 */
    val isMarkedUnread: Boolean,
    /** 时间戳 */
    val timestamp: String?,
    /** 最新事件 */
    val latestEvent: LatestEvent,
    /** 头像数据 */
    val avatarData: AvatarData,
    /** 用户定义的通知模式 */
    val userDefinedNotificationMode: RoomNotificationMode?,
    /** 是否有房间通话 */
    val hasRoomCall: Boolean,
    /** 是否为直接消息 */
    val isDirect: Boolean,
    /** 是否为 DM */
    val isDm: Boolean,
    /** 是否为收藏 */
    val isFavorite: Boolean,
    /** 邀请发送者 */
    val inviteSender: InviteSender?,
    /** 是否已被迁移 */
    val isTombstoned: Boolean,
    /** 房间成员头像列表 */
    val heroes: ImmutableList<AvatarData>,
    /** 是否为空间 */
    val isSpace: Boolean,
) {
    /**
     * 是否高亮显示
     *
     * 当房间有未读通知或提及，或者被标记为未读时返回 true
     */
    val isHighlighted = userDefinedNotificationMode != RoomNotificationMode.MUTE &&
        (numberOfUnreadNotifications > 0 || numberOfUnreadMentions > 0) ||
        isMarkedUnread

    /**
     * 是否有新内容
     *
     * 当房间有未读消息、提及、通知或被标记为未读时返回 true
     */
    val hasNewContent = numberOfUnreadMessages > 0 ||
        numberOfUnreadMentions > 0 ||
        numberOfUnreadNotifications > 0 ||
        isMarkedUnread

    /**
     * 总未读数量
     *
     * 如果被标记为未读，至少返回1，否则返回未读消息数
     */
    val totalUnreadCount: Long = when {
        numberOfUnreadMessages > 0 -> {
            numberOfUnreadMessages
        }
        isMarkedUnread || numberOfUnreadMentions > 0 || numberOfUnreadNotifications > 0 -> {
            1L
        }
        else -> {
            0L
        }
    }

    val hasUnreadIndicator: Boolean = totalUnreadCount > 0

    /**
     * 转换为邀请数据
     *
     * @return 邀请数据
     */
    fun toInviteData() = InviteData(
        roomId = roomId,
        roomName = name ?: roomId.value,
        isDm = isDm,
    )
}
