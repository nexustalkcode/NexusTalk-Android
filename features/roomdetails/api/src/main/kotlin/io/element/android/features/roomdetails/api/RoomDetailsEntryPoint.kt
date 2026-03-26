/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.api

import android.os.Parcelable
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.permalink.PermalinkData
import kotlinx.parcelize.Parcelize

/**
 * 房间详情功能入口点接口
 *
 * 定义房间详情模块的外部接口，用于创建和管理房间详情相关的导航节点。
 * 该接口继承自 FeatureEntryPoint，是房间详情模块的公共 API 入口。
 *
 * @see FeatureEntryPoint 功能入口点基接口
 */
interface RoomDetailsEntryPoint : FeatureEntryPoint {
    /**
     * 初始导航目标密封接口
     *
     * 定义房间详情模块初始化时需要导航到的目标页面。
     * 使用密封接口确保类型安全，只能创建预定义的子类型。
     *
     * @see Parcelable 可序列化接口，用于在导航中传递数据
     */
    sealed interface InitialTarget : Parcelable {
        /**
         * 房间详情页面目标
         *
         * 导航到房间基本信息页面，显示房间名称、头像、成员数等信息。
         */
        @Parcelize
        data object RoomDetails : InitialTarget

        /**
         * 房间成员列表页面目标
         *
         * 导航到房间成员列表页面，可以查看和管理房间成员。
         */
        @Parcelize
        data object RoomMemberList : InitialTarget

        /**
         * 房间成员详情页面目标
         *
         * 导航到指定成员的详情页面。
         *
         * @property roomMemberId 要查看详情的房间成员用户ID
         */
        @Parcelize
        data class RoomMemberDetails(val roomMemberId: UserId) : InitialTarget

        /**
         * 房间通知设置页面目标
         *
         * 导航到房间通知设置页面。
         */
        @Parcelize
        data object RoomNotificationSettings : InitialTarget
    }

    /**
     * 入口点参数数据类
     *
     * 包含创建房间详情节点所需的参数。
     *
     * @property initialElement 初始导航目标，决定打开房间详情时首先显示哪个页面
     * @see InitialTarget 初始导航目标类型
     */
    data class Params(val initialElement: InitialTarget) : NodeInputs

    /**
     * 房间详情模块回调接口
     *
     * 定义房间详情模块需要与外部交互的回调方法。
     * 实现此接口的类可以处理模块内部的导航请求和事件。
     *
     * @see Plugin 插件接口基类
     */
    interface Callback : Plugin {
        /**
         * 导航到全局通知设置
         *
         * 当用户点击查看全局通知设置时调用。
         */
        fun navigateToGlobalNotificationSettings()

        /**
         * 导航到指定房间
         *
         * @param roomId 目标房间ID
         * @param serverNames 服务器名称列表
         */
        fun navigateToRoom(roomId: RoomId, serverNames: List<String>)

        /**
         * 处理永久链接点击事件
         *
         * @param data 永久链接数据
         * @param pushToBackstack 是否将其推入返回栈
         */
        fun handlePermalinkClick(data: PermalinkData, pushToBackstack: Boolean)

        /**
         * 开始转发事件流程
         *
         * @param eventId 要转发的事件ID
         * @param fromPinnedEvents 是否来自固定消息
         */
        fun startForwardEventFlow(eventId: EventId, fromPinnedEvents: Boolean)
    }

    /**
     * 创建房间详情节点
     *
     * 工厂方法，用于创建房间详情模块的根节点。
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 入口点参数
     * @param callback 回调接口实现
     * @return 创建的节点实例
     * @see Node 应用节点基类
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node
}
