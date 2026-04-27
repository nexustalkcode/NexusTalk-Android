/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.model

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.room.RoomNotificationMode
import io.element.android.libraries.matrix.ui.model.InviteSender
import kotlinx.collections.immutable.toImmutableList

/**
 * 房间列表摘要提供者
 *
 * 为预览和测试提供 RoomListRoomSummary 示例数据。
 *
 * @see RoomListRoomSummary 房间列表摘要
 */
open class RoomListRoomSummaryProvider : PreviewParameterProvider<RoomListRoomSummary> {
    /**
     * 提供预览状态序列
     */
    override val values: Sequence<RoomListRoomSummary>
        get() = sequenceOf(
            listOf(
                aRoomListRoomSummary(displayType = RoomSummaryDisplayType.PLACEHOLDER),
                aRoomListRoomSummary(),
                aRoomListRoomSummary(name = null),
                aRoomListRoomSummary(latestEvent = LatestEvent.None),
                aRoomListRoomSummary(
                    name = "A very long room name that should be truncated",
                    latestEvent = LatestEvent.Synced(
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt" +
                            " ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea com" +
                            "modo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
                    ),
                    timestamp = "yesterday",
                    numberOfUnreadMessages = 1,
                ),
            ),
            listOf(false, true).map { hasCall ->
                listOf(
                    RoomNotificationMode.ALL_MESSAGES,
                    RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY,
                    RoomNotificationMode.MUTE,
                ).map { roomNotificationMode ->
                    listOf(
                        aRoomListRoomSummary(
                            name = roomNotificationMode.name,
                            latestEvent = LatestEvent.Synced("No activity" + if (hasCall) ", call" else ""),
                            notificationMode = roomNotificationMode,
                            numberOfUnreadMessages = 0,
                            numberOfUnreadMentions = 0,
                            hasRoomCall = hasCall,
                            isFavorite = true
                        ),
                        aRoomListRoomSummary(
                            name = roomNotificationMode.name,
                            latestEvent = LatestEvent.Synced("New messages" + if (hasCall) ", call" else ""),
                            notificationMode = roomNotificationMode,
                            numberOfUnreadMessages = 1,
                            numberOfUnreadMentions = 0,
                            hasRoomCall = hasCall,
                        ),
                        aRoomListRoomSummary(
                            name = roomNotificationMode.name,
                            latestEvent = LatestEvent.Synced("New messages, mentions" + if (hasCall) ", call" else ""),
                            notificationMode = roomNotificationMode,
                            numberOfUnreadMessages = 1,
                            numberOfUnreadMentions = 1,
                            hasRoomCall = hasCall,
                        ),
                        aRoomListRoomSummary(
                            name = roomNotificationMode.name,
                            latestEvent = LatestEvent.Synced("New mentions" + if (hasCall) ", call" else ""),
                            notificationMode = roomNotificationMode,
                            numberOfUnreadMessages = 0,
                            numberOfUnreadMentions = 1,
                            hasRoomCall = hasCall,
                        ),
                    )
                }.flatten()
            }.flatten(),
            listOf(
                aRoomListRoomSummary(
                    displayType = RoomSummaryDisplayType.INVITE,
                    inviteSender = anInviteSender(
                        userId = UserId("@alice:matrix.org"),
                        displayName = "Alice",
                    ),
                    canonicalAlias = RoomAlias("#alias:matrix.org"),
                ),
                aRoomListRoomSummary(
                    name = "Bob",
                    displayType = RoomSummaryDisplayType.INVITE,
                    inviteSender = anInviteSender(
                        userId = UserId("@bob:matrix.org"),
                        displayName = "Bob",
                    ),
                    isDm = true,
                ),
                aRoomListRoomSummary(
                    name = null,
                    displayType = RoomSummaryDisplayType.INVITE,
                    inviteSender = anInviteSender(
                        userId = UserId("@bob:matrix.org"),
                        displayName = "Bob",
                    ),
                ),
                aRoomListRoomSummary(
                    name = "A space invite",
                    displayType = RoomSummaryDisplayType.INVITE,
                    inviteSender = anInviteSender(
                        userId = UserId("@bob:matrix.org"),
                        displayName = "Bob",
                    ),
                    isSpace = true
                ),
                aRoomListRoomSummary(
                    name = "A knocked room",
                    displayType = RoomSummaryDisplayType.KNOCKED,
                ),
                aRoomListRoomSummary(
                    name = "A knocked room with alias",
                    canonicalAlias = RoomAlias("#knockable:matrix.org"),
                    displayType = RoomSummaryDisplayType.KNOCKED,
                ),
                aRoomListRoomSummary(
                    name = "A tombstoned room",
                    displayType = RoomSummaryDisplayType.ROOM,
                    isTombstoned = true,
                ),
                aRoomListRoomSummary(
                    name = "A DM room",
                    displayType = RoomSummaryDisplayType.ROOM,
                    isDm = true,
                ),
                aRoomListRoomSummary(
                    name = "A space room",
                    displayType = RoomSummaryDisplayType.ROOM,
                    isSpace = true,
                ),
            ),
            listOf(
                aRoomListRoomSummary(latestEvent = LatestEvent.Sending("A sending message")),
                aRoomListRoomSummary(latestEvent = LatestEvent.Error),
            )
        ).flatten()
}

/**
 * 创建示例邀请发送者
 *
 * @param userId 用户 ID
 * @param displayName 显示名称
 * @param avatarData 头像数据
 * @return 邀请发送者
 */
internal fun anInviteSender(
    userId: UserId = UserId("@bob:domain"),
    displayName: String = "Bob",
    avatarData: AvatarData = AvatarData(userId.value, displayName, size = AvatarSize.InviteSender),
) = InviteSender(
    userId = userId,
    displayName = displayName,
    avatarData = avatarData,
    membershipChangeReason = null,
)

/**
 * 创建示例房间列表摘要
 *
 * @param id 房间唯一标识符
 * @param name 房间名称
 * @param numberOfUnreadMessages 未读消息数量
 * @param numberOfUnreadMentions 未读提及数量
 * @param numberOfUnreadNotifications 未读通知数量
 * @param isMarkedUnread 是否被标记为未读
 * @param latestEvent 最新事件
 * @param timestamp 时间戳
 * @param notificationMode 通知模式
 * @param hasRoomCall 是否有房间通话
 * @param avatarData 头像数据
 * @param isDirect 是否为直接消息
 * @param isDm 是否为 DM
 * @param isFavorite 是否为收藏
 * @param inviteSender 邀请发送者
 * @param displayType 显示类型
 * @param canonicalAlias 规范别名
 * @param heroes 房间成员头像列表
 * @param isTombstoned 是否已被迁移
 * @param isSpace 是否为空间
 * @return 房间列表摘要
 */
internal fun aRoomListRoomSummary(
    id: String = "!roomId:domain",
    name: String? = "Room name",
    numberOfUnreadMessages: Long = 0,
    numberOfUnreadMentions: Long = 0,
    numberOfUnreadNotifications: Long = 0,
    isMarkedUnread: Boolean = false,
    latestEvent: LatestEvent = LatestEvent.Synced("Last message"),
    timestamp: String? = latestEvent.takeIf { it !is LatestEvent.None }?.let { "88:88" },
    notificationMode: RoomNotificationMode? = null,
    hasRoomCall: Boolean = false,
    avatarData: AvatarData = AvatarData(id, name, size = AvatarSize.RoomListItem),
    isDirect: Boolean = false,
    isDm: Boolean = false,
    isFavorite: Boolean = false,
    inviteSender: InviteSender? = null,
    displayType: RoomSummaryDisplayType = RoomSummaryDisplayType.ROOM,
    canonicalAlias: RoomAlias? = null,
    heroes: List<AvatarData> = emptyList(),
    isTombstoned: Boolean = false,
    isSpace: Boolean = false,
) = RoomListRoomSummary(
    id = id,
    roomId = RoomId(id),
    name = name,
    numberOfUnreadMessages = numberOfUnreadMessages,
    numberOfUnreadMentions = numberOfUnreadMentions,
    numberOfUnreadNotifications = numberOfUnreadNotifications,
    isMarkedUnread = isMarkedUnread,
    timestamp = timestamp,
    latestEvent = latestEvent,
    avatarData = avatarData,
    userDefinedNotificationMode = notificationMode,
    hasRoomCall = hasRoomCall,
    isDirect = isDirect,
    isDm = isDm,
    isFavorite = isFavorite,
    inviteSender = inviteSender,
    displayType = displayType,
    canonicalAlias = canonicalAlias,
    heroes = heroes.toImmutableList(),
    isTombstoned = isTombstoned,
    isSpace = isSpace
)
