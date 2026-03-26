/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.joinroom.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.invite.api.InviteData
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.element.android.features.invite.api.acceptdecline.anAcceptDeclineInviteState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.RoomIdOrAlias
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.element.android.libraries.matrix.api.exception.ClientException
import io.element.android.libraries.matrix.api.room.join.JoinRoom
import io.element.android.libraries.matrix.api.room.join.JoinRule
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.model.InviteSender
import kotlinx.collections.immutable.toImmutableList

/**
 * 加入房间状态提供者
 *
 * 用于在预览中提供各种 JoinRoomState 示例状态的类。
 * 继承自 PreviewParameterProvider，用于 Compose 预览功能。
 */
open class JoinRoomStateProvider : PreviewParameterProvider<JoinRoomState> {
    /**
     * 提供预览状态序列
     *
     * 包含各种典型的加入房间状态，用于 UI 预览和测试。
     */
    override val values: Sequence<JoinRoomState>
        get() = sequenceOf(
            aJoinRoomState(
                contentState = ContentState.Loading
            ),
            aJoinRoomState(
                contentState = ContentState.UnknownRoom
            ),
            aJoinRoomState(
                contentState = aLoadedContentState(
                    name = null,
                    alias = null,
                    topic = null,
                )
            ),
            aJoinRoomState(
                contentState = aLoadedContentState(joinAuthorisationStatus = JoinAuthorisationStatus.CanJoin)
            ),
            aJoinRoomState(
                contentState = aLoadedContentState(joinAuthorisationStatus = JoinAuthorisationStatus.CanJoin),
                joinAction = AsyncAction.Failure(JoinRoom.Failures.UnauthorizedJoin)
            ),
            aJoinRoomState(
                contentState = aLoadedContentState(joinAuthorisationStatus = JoinAuthorisationStatus.CanJoin),
                joinAction = AsyncAction.Failure(ClientException.Generic("Something went wrong", null))
            ),
            aJoinRoomState(
                contentState = aLoadedContentState(
                    joinAuthorisationStatus = JoinAuthorisationStatus.IsInvited(
                        inviteData = anInviteData(),
                        inviteSender = null,
                    )
                )
            ),
            aJoinRoomState(
                contentState = aLoadedContentState(
                    numberOfMembers = 123,
                    joinAuthorisationStatus = JoinAuthorisationStatus.IsInvited(
                        inviteData = anInviteData(),
                        inviteSender = anInviteSender(),
                    ),
                )
            ),
            aJoinRoomState(
                contentState = aFailureContentState()
            ),
            aJoinRoomState(
                contentState = aLoadedContentState(
                    roomId = RoomId("!aSpaceId:domain"),
                    name = "A space",
                    alias = null,
                    topic = "This is the topic of a space",
                    details = aLoadedDetailsSpace(
                        childrenCount = 42,
                    ),
                )
            ),
            aJoinRoomState(
                contentState = aLoadedContentState(
                    name = "A DM",
                    details = aLoadedDetailsRoom(
                        isDm = true,
                    ),
                )
            ),
            aJoinRoomState(
                contentState = aLoadedContentState(
                    joinAuthorisationStatus = JoinAuthorisationStatus.CanKnock,
                    topic = "lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt" +
                        " ut labore et dolore magna aliqua ut enim ad minim veniam quis nostrud exercitation ullamco" +
                        " laboris nisi ut aliquip ex ea commodo consequat duis aute irure dolor in reprehenderit in" +
                        " voluptate velit esse cillum dolore eu fugiat nulla pariatur excepteur sint occaecat cupidatat" +
                        " non proident sunt in culpa qui officia deserunt mollit anim id est laborum",
                    numberOfMembers = 888,
                )
            ),
            aJoinRoomState(
                knockMessage = "Let me in please!",
                contentState = aLoadedContentState(
                    joinAuthorisationStatus = JoinAuthorisationStatus.CanKnock,
                    topic = "lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt" +
                        " ut labore et dolore magna aliqua ut enim ad minim veniam quis nostrud exercitation ullamco" +
                        " laboris nisi ut aliquip ex ea commodo consequat duis aute irure dolor in reprehenderit in" +
                        " voluptate velit esse cillum dolore eu fugiat nulla pariatur excepteur sint occaecat cupidatat" +
                        " non proident sunt in culpa qui officia deserunt mollit anim id est laborum",
                    numberOfMembers = 888,
                )
            ),
            aJoinRoomState(
                contentState = aLoadedContentState(
                    name = "A knocked Room",
                    joinAuthorisationStatus = JoinAuthorisationStatus.IsKnocked
                )
            ),
            aJoinRoomState(
                contentState = aLoadedContentState(
                    name = "A private room",
                    joinAuthorisationStatus = JoinAuthorisationStatus.NeedInvite
                )
            ),
            aJoinRoomState(
                contentState = aLoadedContentState(
                    name = "A banned room",
                    joinAuthorisationStatus = JoinAuthorisationStatus.IsBanned(
                        banSender = InviteSender(
                            userId = UserId("@alice:domain"),
                            displayName = "Alice",
                            avatarData = AvatarData("alice", "Alice", size = AvatarSize.InviteSender),
                            membershipChangeReason = "spamming"
                        ),
                        reason = "spamming",
                    ),
                )
            ),
            aJoinRoomState(
                contentState = aLoadedContentState(
                    name = "A restricted room",
                    joinAuthorisationStatus = JoinAuthorisationStatus.Restricted,
                )
            ),
        )
}

/**
 * 创建失败状态的内容
 *
 * @return ContentState.Failure 失败状态
 */
fun aFailureContentState(): ContentState {
    return ContentState.Failure(
        error = Exception("Error"),
    )
}

/**
 * 创建已加载状态的内容
 *
 * @param roomId 房间 ID
 * @param name 房间名称
 * @param alias 房间别名
 * @param topic 房间主题
 * @param numberOfMembers 成员数量
 * @param roomAvatarUrl 房间头像 URL
 * @param joinAuthorisationStatus 加入授权状态
 * @param joinRule 加入规则
 * @param details 加载详情
 * @return ContentState.Loaded 已加载状态
 */
fun aLoadedContentState(
    roomId: RoomId = A_ROOM_ID,
    name: String? = "Element X android",
    alias: RoomAlias? = RoomAlias("#exa:matrix.org"),
    topic: String? = "Element X is a secure, private and decentralized messenger.",
    numberOfMembers: Long? = null,
    roomAvatarUrl: String? = null,
    joinAuthorisationStatus: JoinAuthorisationStatus = JoinAuthorisationStatus.Unknown,
    joinRule: JoinRule? = null,
    details: LoadedDetails = aLoadedDetailsRoom(isDm = false),
) = ContentState.Loaded(
    roomId = roomId,
    name = name,
    alias = alias,
    topic = topic,
    numberOfMembers = numberOfMembers,
    roomAvatarUrl = roomAvatarUrl,
    joinAuthorisationStatus = joinAuthorisationStatus,
    joinRule = joinRule,
    details = details,
)

/**
 * 创建房间详情
 *
 * @param isDm 是否为直接消息
 * @return LoadedDetails.Room 房间详情
 */
fun aLoadedDetailsRoom(
    isDm: Boolean = false,
) = LoadedDetails.Room(
    isDm = isDm
)

/**
 * 创建空间详情
 *
 * @param childrenCount 子空间数量
 * @param heroes 重要成员列表
 * @return LoadedDetails.Space 空间详情
 */
fun aLoadedDetailsSpace(
    childrenCount: Int = 0,
    heroes: List<MatrixUser> = emptyList(),
) = LoadedDetails.Space(
    childrenCount = childrenCount,
    heroes = heroes.toImmutableList()
)

/**
 * 创建加入房间状态
 *
 * @param roomIdOrAlias 房间 ID 或别名
 * @param contentState 内容状态
 * @param acceptDeclineInviteState 接受/拒绝邀请状态
 * @param joinAction 加入操作状态
 * @param knockAction 敲门操作状态
 * @param forgetAction 忘记操作状态
 * @param cancelKnockAction 取消敲门操作状态
 * @param knockMessage 敲门消息
 * @param hideInviteAvatars 是否隐藏邀请头像
 * @param canReportRoom 是否可以报告房间
 * @param eventSink 事件处理函数
 * @return JoinRoomState 加入房间状态
 */
fun aJoinRoomState(
    roomIdOrAlias: RoomIdOrAlias = A_ROOM_ALIAS.toRoomIdOrAlias(),
    contentState: ContentState = aLoadedContentState(),
    acceptDeclineInviteState: AcceptDeclineInviteState = anAcceptDeclineInviteState(),
    joinAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    knockAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    forgetAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    cancelKnockAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    knockMessage: String = "",
    hideInviteAvatars: Boolean = false,
    canReportRoom: Boolean = true,
    eventSink: (JoinRoomEvents) -> Unit = {}
) = JoinRoomState(
    roomIdOrAlias = roomIdOrAlias,
    contentState = contentState,
    acceptDeclineInviteState = acceptDeclineInviteState,
    joinAction = joinAction,
    knockAction = knockAction,
    cancelKnockAction = cancelKnockAction,
    forgetAction = forgetAction,
    applicationName = "AppName",
    knockMessage = knockMessage,
    hideInviteAvatars = hideInviteAvatars,
    canReportRoom = canReportRoom,
    eventSink = eventSink
)

/**
 * 创建邀请发送者测试数据
 *
 * @param userId 用户 ID
 * @param displayName 显示名称
 * @param avatarData 头像数据
 * @param membershipChangeReason 成员变更原因
 * @return InviteSender 邀请发送者
 */
internal fun anInviteSender(
    userId: UserId = UserId("@bob:domain"),
    displayName: String = "Bob",
    avatarData: AvatarData = AvatarData(userId.value, displayName, size = AvatarSize.InviteSender),
    membershipChangeReason: String? = null,
) = InviteSender(
    userId = userId,
    displayName = displayName,
    avatarData = avatarData,
    membershipChangeReason = membershipChangeReason,
)

/**
 * 创建邀请数据测试数据
 *
 * @param roomId 房间 ID
 * @param roomName 房间名称
 * @param isDm 是否为直接消息
 * @return InviteData 邀请数据
 */
internal fun anInviteData(
    roomId: RoomId = A_ROOM_ID,
    roomName: String = "Room name",
    isDm: Boolean = false,
) = InviteData(
    roomId = roomId,
    roomName = roomName,
    isDm = isDm,
)

private val A_ROOM_ID = RoomId("!exa:matrix.org")
private val A_ROOM_ALIAS = RoomAlias("#exa:matrix.org")
