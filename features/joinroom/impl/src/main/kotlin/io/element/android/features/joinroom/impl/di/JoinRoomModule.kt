/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.joinroom.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import im.vector.app.features.analytics.plan.JoinedRoom
import io.element.android.features.invite.api.SeenInvitesStore
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.element.android.features.joinroom.impl.JoinRoomPresenter
import io.element.android.features.roomdirectory.api.RoomDescription
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.RoomIdOrAlias
import io.element.android.libraries.matrix.api.room.join.JoinRoom
import java.util.Optional

/**
 * 加入房间功能依赖注入模块
 *
 * 提供加入房间功能所需的各种依赖项，包括 Presenter 工厂等。
 */
@BindingContainer
@ContributesTo(SessionScope::class)
object JoinRoomModule {
    /**
     * 提供 JoinRoomPresenter 工厂实例
     *
     * 用于创建 JoinRoomPresenter 实例，包含了所有必要的依赖项。
     *
     * @param client Matrix 客户端实例，用于与 Matrix 服务器通信
     * @param joinRoom 加入房间服务
     * @param knockRoom 敲门请求服务
     * @param cancelKnockRoom 取消敲门请求服务
     * @param forgetRoom 忘记房间服务
     * @param acceptDeclineInvitePresenter 接受/拒绝邀请的 Presenter
     * @param buildMeta 构建元数据
     * @param seenInvitesStore 已查看邀请存储
     * @return JoinRoomPresenter.Factory JoinRoomPresenter 工厂实例
     */
    @Provides
    fun providesJoinRoomPresenterFactory(
        client: MatrixClient,
        joinRoom: JoinRoom,
        knockRoom: KnockRoom,
        cancelKnockRoom: CancelKnockRoom,
        forgetRoom: ForgetRoom,
        acceptDeclineInvitePresenter: Presenter<AcceptDeclineInviteState>,
        buildMeta: BuildMeta,
        seenInvitesStore: SeenInvitesStore,
    ): JoinRoomPresenter.Factory {
        return object : JoinRoomPresenter.Factory {
            /**
             * 创建 JoinRoomPresenter 实例
             *
             * @param roomId 房间 ID
             * @param roomIdOrAlias 房间 ID 或别名
             * @param roomDescription 房间描述（可选）
             * @param serverNames 服务器名称列表
             * @param trigger 加入房间的触发器
             * @return JoinRoomPresenter 实例
             */
            override fun create(
                roomId: RoomId,
                roomIdOrAlias: RoomIdOrAlias,
                roomDescription: Optional<RoomDescription>,
                serverNames: List<String>,
                trigger: JoinedRoom.Trigger,
            ): JoinRoomPresenter {
                return JoinRoomPresenter(
                    roomId = roomId,
                    roomIdOrAlias = roomIdOrAlias,
                    roomDescription = roomDescription,
                    serverNames = serverNames,
                    trigger = trigger,
                    matrixClient = client,
                    joinRoom = joinRoom,
                    knockRoom = knockRoom,
                    forgetRoom = forgetRoom,
                    cancelKnockRoom = cancelKnockRoom,
                    acceptDeclineInvitePresenter = acceptDeclineInvitePresenter,
                    buildMeta = buildMeta,
                    seenInvitesStore = seenInvitesStore,
                )
            }
        }
    }
}
