/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.architecture

/**
 * 房间级依赖图工厂契约。
 *
 * 这是一个纯架构层接口，放在 architecture 模块可以避免 feature 为了创建 RoomGraph
 * 反向依赖 appnav 这种上层导航聚合模块。
 *
 * 这里故意不暴露具体的 Matrix 房间类型，避免 architecture 反向依赖 matrix API；
 * 具体实现方在自己的模块内完成受控转换。
 */
fun interface RoomGraphFactory {
    fun create(room: Any): Any
}
