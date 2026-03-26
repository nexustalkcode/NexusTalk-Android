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
import io.element.android.libraries.architecture.NodeFactoriesBindings
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.MatrixClient

/**
 * 会话依赖注入图接口。
 *
 * 使用 @GraphExtension 注解标记，扩展自 SessionScope。
 * 定义了用户会话级别的依赖注入关系。
 * 继承自 NodeFactoriesBindings，提供节点工厂绑定。
 *
 * 包含以下依赖关系：
 * - roomGraphFactory：房间图的工厂接口，用于创建 RoomGraph
 *
 * Factory 接口定义了创建 SessionGraph 实例的工厂方法，
 * 接收 MatrixClient 作为参数。
 */
@GraphExtension(SessionScope::class)
interface SessionGraph : NodeFactoriesBindings {
    val roomGraphFactory: RoomGraph.Factory

    @GraphExtension.Factory
    interface Factory {
        fun create(@Provides matrixClient: MatrixClient): SessionGraph
    }
}
