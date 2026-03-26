/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl

import androidx.compose.runtime.Immutable
import io.element.android.features.leaveroom.api.LeaveRoomState
import io.element.android.features.roomcall.api.RoomCallState
import io.element.android.features.userprofile.api.UserProfileState
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.RoomMember
import io.element.android.libraries.matrix.api.room.RoomNotificationSettings
import io.element.android.libraries.matrix.api.user.MatrixUser
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 房间详情状态数据类
 *
 * 表示房间详情界面的完整状态，包含房间信息、成员、权限、通话状态等信息。
 *
 * @property roomId 房间 ID
 * @property roomName 房间名称
 * @property roomAlias 房间别名
 * @property roomAvatarUrl 房间头像 URL
 * @property roomTopic 房间主题状态
 * @property memberCount 成员数量
 * @property isEncrypted 是否加密
 * @property roomType 房间类型
 * @property roomMemberDetailsState 房间成员详情状态
 * @property canEdit 是否可以编辑
 * @property canInvite 是否可以邀请
 * @property roomCallState 房间通话状态
 * @property leaveRoomState 离开房间状态
 * @property roomNotificationSettings 房间通知设置
 * @property isFavorite 是否收藏
 * @property displayRolesAndPermissionsSettings 是否显示角色和权限设置
 * @property isPublic 是否公开
 * @property heroes 重要成员列表
 * @property pinnedMessagesCount 固定消息数量
 * @property snackbarMessage 提示消息
 * @property canShowKnockRequests 是否显示敲门请求
 * @property knockRequestsCount 敲门请求数量
 * @property canShowSecurityAndPrivacy 是否显示安全和隐私设置
 * @property hasMemberVerificationViolations 是否有成员验证违规
 * @property canReportRoom 是否可以报告房间
 * @property isTombstoned 是否为墓碑状态
 * @property showDebugInfo 是否显示调试信息
 * @property roomVersion 房间版本
 * @property eventSink 事件处理函数
 */
data class RoomDetailsState(
    val roomId: RoomId,
    val roomName: String,
    val roomAlias: RoomAlias?,
    val roomAvatarUrl: String?,
    val roomTopic: RoomTopicState,
    val memberCount: Long,
    val isEncrypted: Boolean,
    val roomType: RoomDetailsType,
    val roomMemberDetailsState: UserProfileState?,
    val canEdit: Boolean,
    val canInvite: Boolean,
    val roomCallState: RoomCallState,
    val leaveRoomState: LeaveRoomState,
    val roomNotificationSettings: RoomNotificationSettings?,
    val isFavorite: Boolean,
    val displayRolesAndPermissionsSettings: Boolean,
    val isPublic: Boolean,
    val heroes: ImmutableList<MatrixUser>,
    val pinnedMessagesCount: Int?,
    val snackbarMessage: SnackbarMessage?,
    val canShowKnockRequests: Boolean,
    val knockRequestsCount: Int?,
    val canShowSecurityAndPrivacy: Boolean,
    val hasMemberVerificationViolations: Boolean,
    val canReportRoom: Boolean,
    val isTombstoned: Boolean,
    val showDebugInfo: Boolean,
    val roomVersion: String?,
    val eventSink: (RoomDetailsEvent) -> Unit
) {
    /**
     * 获取房间徽章列表
     */
    val roomBadges = buildList {
        if (isEncrypted) {
            add(RoomBadge.ENCRYPTED)
        } else {
            add(RoomBadge.NOT_ENCRYPTED)
        }
        if (isPublic) {
            add(RoomBadge.PUBLIC)
        }
    }.toImmutableList()
}

/**
 * 房间类型密封接口
 */
@Immutable
sealed interface RoomDetailsType {
    /** 普通房间 */
    data object Room : RoomDetailsType
    /** 直接消息房间
     * @property me 当前用户成员
     * @property otherMember 其他成员
     */
    data class Dm(
        val me: RoomMember,
        val otherMember: RoomMember,
    ) : RoomDetailsType
}

/**
 * 房间主题状态密封接口
 */
@Immutable
sealed interface RoomTopicState {
    /** 隐藏 */
    data object Hidden : RoomTopicState
    /** 可以添加主题 */
    data object CanAddTopic : RoomTopicState
    /** 已有主题
     * @property topic 主题内容
     */
    data class ExistingTopic(val topic: String) : RoomTopicState
}

/**
 * 房间徽章枚举
 */
enum class RoomBadge {
    /** 已加密 */
    ENCRYPTED,
    /** 未加密 */
    NOT_ENCRYPTED,
    /** 公开 */
    PUBLIC,
}
