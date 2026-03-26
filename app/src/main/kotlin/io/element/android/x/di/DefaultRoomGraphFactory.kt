/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x.di

import dev.zacsweers.metro.ContributesBinding
import io.element.android.appnav.di.RoomGraphFactory
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.room.JoinedRoom

/**
 * 默认的房间图工厂实现。
 *
 * 实现 RoomGraphFactory 接口，
 * 负责创建聊天房间级别的依赖注入图。
 * 内部持有 SessionGraph 实例，
 * 通过调用 SessionGraph.roomGraphFactory.create() 方法创建 RoomGraph。
 *
 * 通过 @ContributesBinding 注解绑定到 SessionScope。
 */
@ContributesBinding(SessionScope::class)
class DefaultRoomGraphFactory(
    private val sessionGraph: SessionGraph,
) : RoomGraphFactory {
    override fun create(room: JoinedRoom): Any {
        return sessionGraph.roomGraphFactory
            .create(room, room)
    }
}
