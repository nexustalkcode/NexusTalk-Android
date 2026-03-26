/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invitepeople.api

/**
 * 邀请人员事件接口
 *
 * 定义邀请人员功能中所有可能发生的用户交互事件。
 * 用于统一管理邀请流程中的各种操作事件。
 */
interface InvitePeopleEvents {
    /**
     * 发送邀请事件
     *
     * 触发向选中的用户发送邀请的操作。
     * 该事件会在确认邀请名单后由UI层触发。
     */
    data object SendInvites : InvitePeopleEvents

    /**
     * 关闭搜索事件
     *
     * 触发关闭搜索栏的操作。
     * 关闭搜索时会清空搜索输入框的内容。
     */
    data object CloseSearch : InvitePeopleEvents
}
