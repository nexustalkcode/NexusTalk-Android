/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x.di

import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import io.element.android.appnav.di.TimelineBindings
import io.element.android.libraries.architecture.NodeFactoriesBindings
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.room.BaseRoom
import io.element.android.libraries.matrix.api.room.JoinedRoom

/**
 * 房间依赖注入图接口。
 *
 * 使用 @GraphExtension 注解标记，扩展自 RoomScope。
 * 定义了聊天房间级别的依赖注入关系。
 * 继承自 NodeFactoriesBindings 和 TimelineBindings，
 * 同时提供节点工厂和时间线绑定。
 *
 * Factory 接口定义了创建 RoomGraph 实例的工厂方法，
 * 接收 JoinedRoom 和 BaseRoom 作为参数。
 */
@GraphExtension(RoomScope::class)
interface RoomGraph : NodeFactoriesBindings, TimelineBindings {
    @GraphExtension.Factory
    interface Factory {
        fun create(
            @Provides joinedRoom: JoinedRoom,
            @Provides baseRoom: BaseRoom
        ): RoomGraph
    }
}
