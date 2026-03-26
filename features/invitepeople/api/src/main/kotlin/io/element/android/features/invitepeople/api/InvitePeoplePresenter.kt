/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invitepeople.api

import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.JoinedRoom

/**
 * 邀请人员Presenter接口
 *
 * 定义邀请人员功能的Presenter接口，继承自基础Presenter接口。
 * 负责管理邀请人员页面的业务逻辑和状态管理。
 *
 * @see InvitePeopleState 页面状态定义
 */
interface InvitePeoplePresenter : Presenter<InvitePeopleState> {
    /**
     * Presenter工厂接口
     *
     * 用于创建InvitePeoplePresenter实例的工厂接口。
     * 实现依赖注入，允许在运行时动态创建Presenter。
     */
    interface Factory {
        /**
         * 创建InvitePeoplePresenter实例
         *
         * @param joinedRoom 已加入的房间实例，如果为null则表示房间尚未创建
         * @param roomId 房间ID，用于标识目标房间
         * @return InvitePeoplePresenter实例
         */
        fun create(
            joinedRoom: JoinedRoom?,
            roomId: RoomId,
        ): InvitePeoplePresenter
    }
}
