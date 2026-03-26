/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomaliasresolver.impl

/**
 * 房间别名解析器事件定义
 *
 * 该密封接口定义了房间别名解析功能中可能发生的所有用户交互事件。
 * 使用 sealed interface 确保所有事件类型都是已知且有限的。
 *
 * @see RoomAliasResolverPresenter 事件处理者
 * @see RoomAliasResolverState 状态管理
 */
sealed interface RoomAliasResolverEvents {
    /**
     * 重试解析事件
     *
     * 当解析失败时，用户可以选择重试解析操作。
     * 触发后将会重新尝试解析房间别名。
     */
    data object Retry : RoomAliasResolverEvents

    /**
     * 关闭错误对话框事件
     *
     * 用户关闭错误提示对话框时触发。
     * 会将解析状态重置为未初始化状态。
     */
    data object DismissError : RoomAliasResolverEvents
}
