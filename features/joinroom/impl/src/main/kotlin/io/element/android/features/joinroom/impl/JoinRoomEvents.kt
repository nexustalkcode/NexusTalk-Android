/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.joinroom.impl

import io.element.android.features.invite.api.InviteData

/**
 * 加入房间事件密封接口
 *
 * 定义了加入房间界面中所有可能的用户交互事件。
 * 这些事件由 View 层捕获并传递给 Presenter 进行处理。
 */
sealed interface JoinRoomEvents {
    /** 重新获取房间内容事件 */
    data object RetryFetchingContent : JoinRoomEvents

    /** 关闭错误并隐藏内容事件 */
    data object DismissErrorAndHideContent : JoinRoomEvents

    /** 加入房间事件 */
    data object JoinRoom : JoinRoomEvents

    /** 敲门请求事件 */
    data object KnockRoom : JoinRoomEvents

    /** 忘记房间事件 */
    data object ForgetRoom : JoinRoomEvents

    /**
     * 取消敲门请求事件
     *
     * @property requiresConfirmation 是否需要用户确认
     */
    data class CancelKnock(val requiresConfirmation: Boolean) : JoinRoomEvents

    /**
     * 更新敲门消息事件
     *
     * @property message 敲门消息内容
     */
    data class UpdateKnockMessage(val message: String) : JoinRoomEvents

    /** 清除操作状态事件 */
    data object ClearActionStates : JoinRoomEvents

    /**
     * 接受邀请事件
     *
     * @property inviteData 邀请数据
     */
    data class AcceptInvite(val inviteData: InviteData) : JoinRoomEvents

    /**
     * 拒绝邀请事件
     *
     * @property inviteData 邀请数据
     * @property blockUser 是否同时阻止该用户
     */
    data class DeclineInvite(val inviteData: InviteData, val blockUser: Boolean) : JoinRoomEvents
}
