/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.joinroom.impl

import androidx.compose.runtime.Immutable
import io.element.android.features.invite.api.InviteData
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.RoomIdOrAlias
import io.element.android.libraries.matrix.api.room.join.JoinRoom
import io.element.android.libraries.matrix.api.room.join.JoinRule
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.model.InviteSender
import kotlinx.collections.immutable.ImmutableList

internal const val MAX_KNOCK_MESSAGE_LENGTH = 500

/**
 * 加入房间状态数据类
 *
 * 表示加入房间界面的完整状态，包含房间内容、加入/敲门/忘记操作、权限状态等信息。
 *
 * @property roomIdOrAlias 房间 ID 或别名
 * @property contentState 内容状态
 * @property acceptDeclineInviteState 接受/拒绝邀请状态
 * @property joinAction 加入房间的异步操作状态
 * @property knockAction 敲门请求的异步操作状态
 * @property forgetAction 忘记房间的异步操作状态
 * @property cancelKnockAction 取消敲门请求的异步操作状态
 * @property applicationName 应用程序名称
 * @property knockMessage 敲门消息
 * @property hideInviteAvatars 是否隐藏邀请头像
 * @property canReportRoom 是否可以报告房间
 * @property eventSink 事件处理函数
 */
data class JoinRoomState(
    val roomIdOrAlias: RoomIdOrAlias,
    val contentState: ContentState,
    val acceptDeclineInviteState: AcceptDeclineInviteState,
    val joinAction: AsyncAction<Unit>,
    val knockAction: AsyncAction<Unit>,
    val forgetAction: AsyncAction<Unit>,
    val cancelKnockAction: AsyncAction<Unit>,
    private val applicationName: String,
    val knockMessage: String,
    val hideInviteAvatars: Boolean,
    val canReportRoom: Boolean,
    val eventSink: (JoinRoomEvents) -> Unit
) {
    /**
     * 是否为未授权的加入操作
     */
    val isJoinActionUnauthorized = joinAction is AsyncAction.Failure && joinAction.error is JoinRoom.Failures.UnauthorizedJoin

    /**
     * 获取加入授权状态
     */
    val joinAuthorisationStatus = when (contentState) {
        is ContentState.Loaded -> {
            when {
                isJoinActionUnauthorized -> {
                    JoinAuthorisationStatus.Unauthorized
                }
                else -> {
                    contentState.joinAuthorisationStatus
                }
            }
        }
        is ContentState.UnknownRoom -> {
            if (isJoinActionUnauthorized) {
                JoinAuthorisationStatus.Unauthorized
            } else {
                JoinAuthorisationStatus.Unknown
            }
        }
        else -> JoinAuthorisationStatus.None
    }

    /**
     * 是否隐藏头像图片
     */
    val hideAvatarsImages = hideInviteAvatars && joinAuthorisationStatus is JoinAuthorisationStatus.IsInvited
}

/**
 * 内容状态密封接口
 *
 * 表示房间内容的不同加载状态。
 */
@Immutable
sealed interface ContentState {
    /** 正在关闭 */
    data object Dismissing : ContentState
    /** 正在加载 */
    data object Loading : ContentState
    /** 加载失败
     * @property error 异常
     */
    data class Failure(val error: Throwable) : ContentState
    /** 未知房间 */
    data object UnknownRoom : ContentState
    /** 已加载
     * @property roomId 房间 ID
     * @property name 房间名称
     * @property topic 房间主题
     * @property alias 房间别名
     * @property numberOfMembers 成员数量
     * @property roomAvatarUrl 房间头像 URL
     * @property joinAuthorisationStatus 加入授权状态
     * @property joinRule 加入规则
     * @property details 加载详情
     */
    data class Loaded(
        val roomId: RoomId,
        val name: String?,
        val topic: String?,
        val alias: RoomAlias?,
        val numberOfMembers: Long?,
        val roomAvatarUrl: String?,
        val joinAuthorisationStatus: JoinAuthorisationStatus,
        val joinRule: JoinRule?,
        val details: LoadedDetails,
    ) : ContentState {
        /** 是否显示成员数量 */
        val showMemberCount = numberOfMembers != null
        /** 是否为空间 */
        val isSpace = details is LoadedDetails.Space

        /**
         * 获取头像数据
         *
         * @param size 头像大小
         * @return AvatarData 头像数据
         */
        fun avatarData(size: AvatarSize): AvatarData {
            return AvatarData(
                id = roomId.value,
                name = name,
                url = roomAvatarUrl,
                size = size,
            )
        }
    }
}

/**
 * 加载详情密封接口
 */
@Immutable
sealed interface LoadedDetails {
    /** 房间详情
     * @property isDm 是否为直接消息
     */
    data class Room(
        val isDm: Boolean,
    ) : LoadedDetails

    /** 空间详情
     * @property childrenCount 子空间数量
     * @property heroes 重要成员列表
     */
    data class Space(
        val childrenCount: Int,
        val heroes: ImmutableList<MatrixUser>,
    ) : LoadedDetails
}

/**
 * 加入授权状态密封接口
 */
sealed interface JoinAuthorisationStatus {
    /** 无状态 */
    data object None : JoinAuthorisationStatus
    /** 已邀请
     * @property inviteData 邀请数据
     * @property inviteSender 邀请发送者
     */
    data class IsInvited(val inviteData: InviteData, val inviteSender: InviteSender?) : JoinAuthorisationStatus
    /** 已禁止
     * @property banSender 禁止发送者
     * @property reason 禁止原因
     */
    data class IsBanned(val banSender: InviteSender?, val reason: String?) : JoinAuthorisationStatus
    /** 已敲门 */
    data object IsKnocked : JoinAuthorisationStatus
    /** 可以敲门 */
    data object CanKnock : JoinAuthorisationStatus
    /** 可以加入 */
    data object CanJoin : JoinAuthorisationStatus
    /** 需要邀请 */
    data object NeedInvite : JoinAuthorisationStatus
    /** 受限制 */
    data object Restricted : JoinAuthorisationStatus
    /** 未知 */
    data object Unknown : JoinAuthorisationStatus
    /** 未授权 */
    data object Unauthorized : JoinAuthorisationStatus
}
