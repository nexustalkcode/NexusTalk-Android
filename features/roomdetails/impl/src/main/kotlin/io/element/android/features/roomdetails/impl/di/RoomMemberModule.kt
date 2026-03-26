/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.element.android.features.roomdetails.impl.members.details.RoomMemberDetailsPresenter
import io.element.android.features.userprofile.api.UserProfilePresenterFactory
import io.element.android.libraries.androidutils.clipboard.ClipboardHelper
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.encryption.EncryptionService
import io.element.android.libraries.matrix.api.room.JoinedRoom

/**
 * 房间成员模块绑定容器
 *
 * 提供房间成员详情相关的依赖注入绑定。
 * 使用 @BindingContainer 和 @ContributesTo 注解将模块贡献到 RoomScope。
 *
 * @see BindingContainer 绑定容器注解
 * @see ContributesTo 贡献到作用域注解
 * @see RoomScope 房间作用域
 */
@BindingContainer
@ContributesTo(RoomScope::class)
object RoomMemberModule {
    /**
     * 提供房间成员详情 Presenter 工厂
     *
     * 依赖注入方法，用于创建 RoomMemberDetailsPresenter.Factory 实例。
     * 该工厂用于创建 RoomMemberDetailsPresenter，处理房间成员详情页面的业务逻辑。
     *
     * @param room 已加入的房间实例
     * @param userProfilePresenterFactory 用户资料Presenter工厂
     * @param encryptionService 加密服务
     * @param clipboardHelper 剪贴板助手
     * @return RoomMemberDetailsPresenter.Factory 实例
     * @see RoomMemberDetailsPresenter 房间成员详情Presenter
     * @see JoinedRoom 已加入的房间
     */
    @Provides
    fun provideRoomMemberDetailsPresenterFactory(
        room: JoinedRoom,
        userProfilePresenterFactory: UserProfilePresenterFactory,
        encryptionService: EncryptionService,
        clipboardHelper: ClipboardHelper,
    ): RoomMemberDetailsPresenter.Factory {
        return object : RoomMemberDetailsPresenter.Factory {
            override fun create(roomMemberId: UserId): RoomMemberDetailsPresenter {
                return RoomMemberDetailsPresenter(
                    roomMemberId = roomMemberId,
                    room = room,
                    userProfilePresenterFactory = userProfilePresenterFactory,
                    encryptionService = encryptionService,
                    clipboardHelper = clipboardHelper,
                )
            }
        }
    }
}
