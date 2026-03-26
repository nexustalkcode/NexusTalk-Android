/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.api

import android.os.Parcelable
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.ThreadId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.permalink.PermalinkData
import kotlinx.parcelize.Parcelize

/**
 * 消息功能入口点接口
 *
 * 定义消息功能的接口契约，负责创建和管理消息页面节点。
 * 支持消息列表、固定消息、线程等多种视图模式。
 *
 * @see MessagesEntryPoint.InitialTarget 初始目标
 * @see MessagesEntryPoint.Callback 回调接口
 * @see MessagesEntryPoint.Params 创建参数
 */
interface MessagesEntryPoint : FeatureEntryPoint {
    /**
     * 初始目标密封接口
     *
     * 定义消息页面打开时的初始目标位置。
     */
    sealed interface InitialTarget : Parcelable {
        /**
         * 消息列表目标
         *
         * @property focusedEventId 聚焦的事件 ID（可选）
         */
        @Parcelize
        data class Messages(
            val focusedEventId: EventId?,
        ) : InitialTarget

        /** 固定消息目标 */
        @Parcelize
        data object PinnedMessages : InitialTarget
    }

    /**
     * 消息功能回调接口
     */
    interface Callback : Plugin {
        /** 导航到房间详情 */
        fun navigateToRoomDetails()
        /**
         * 导航到房间成员详情
         *
         * @param userId 用户 ID
         */
        fun navigateToRoomMemberDetails(userId: UserId)
        /**
         * 处理链接点击
         *
         * @param data 链接数据
         * @param pushToBackstack 是否推入后退栈
         */
        fun handlePermalinkClick(data: PermalinkData, pushToBackstack: Boolean)
        /**
         * 转发事件
         *
         * @param eventId 事件 ID
         * @param fromPinnedEvents 是否来自固定消息
         */
        fun forwardEvent(eventId: EventId, fromPinnedEvents: Boolean)
        /**
         * 导航到房间
         *
         * @param roomId 房间 ID
         */
        fun navigateToRoom(roomId: RoomId)
    }

    /**
     * 创建消息节点的参数
     *
     * @property initialTarget 初始目标
     */
    data class Params(val initialTarget: InitialTarget) : NodeInputs

    /**
     * 创建消息节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 创建参数
     * @param callback 回调接口
     * @return 创建的节点
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
        callback: Callback,
    ): Node

    /**
     * 节点代理接口
     *
     * 提供节点操作的代理方法。
     */
    interface NodeProxy {
        /**
         * 附加线程
         *
         * @param threadId 线程 ID
         * @param focusedEventId 聚焦的事件 ID（可选）
         */
        suspend fun attachThread(threadId: ThreadId, focusedEventId: EventId?)
    }
}
