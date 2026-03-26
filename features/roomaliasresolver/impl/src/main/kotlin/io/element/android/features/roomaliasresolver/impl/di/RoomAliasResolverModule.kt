/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomaliasresolver.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.element.android.features.roomaliasresolver.impl.RoomAliasResolverPresenter
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomAlias

/**
 * 房间别名解析器依赖注入模块
 *
 * 该模块提供房间别名解析功能所需的依赖注入配置。
 * 使用 Metro 依赖注入框架，将 RoomAliasResolverPresenter.Factory
 * 绑定到 SessionScope，使得每个会话都可以使用解析器。
 *
 * @see RoomAliasResolverPresenter 解析器 presenter
 * @see RoomAliasResolverPresenter.Factory 解析器工厂接口
 */
@BindingContainer
@ContributesTo(SessionScope::class)
object RoomAliasResolverModule {
    /**
     * 提供 RoomAliasResolverPresenter 工厂方法
     *
     * 创建一个工厂实例，用于生成 RoomAliasResolverPresenter。
     * 工厂方法接收 MatrixClient，用于执行实际的别名解析操作。
     *
     * @param client Matrix 客户端实例，用于解析房间别名
     * @return RoomAliasResolverPresenter 工厂实例
     */
    @Provides
    fun providesJoinRoomPresenterFactory(
        client: MatrixClient,
    ): RoomAliasResolverPresenter.Factory {
        return object : RoomAliasResolverPresenter.Factory {
            override fun create(roomAlias: RoomAlias): RoomAliasResolverPresenter {
                return RoomAliasResolverPresenter(
                    roomAlias = roomAlias,
                    matrixClient = client,
                )
            }
        }
    }
}
