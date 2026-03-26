/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.roomdetails.api.RoomDetailsEntryPoint
import io.element.android.features.roomdetails.api.RoomDetailsEntryPoint.InitialTarget
import io.element.android.features.roomdetails.impl.RoomDetailsFlowNode.NavTarget
import io.element.android.libraries.architecture.createNode

/**
 * 默认房间详情入口点实现类
 *
 * RoomDetailsEntryPoint 接口的默认实现类。
 * 使用 @ContributesBinding 注解将其绑定到 AppScope，用于依赖注入。
 *
 * @see RoomDetailsEntryPoint 房间详情入口点接口
 * @see ContributesBinding 绑定注入注解
 */
@ContributesBinding(AppScope::class)
class DefaultRoomDetailsEntryPoint : RoomDetailsEntryPoint {
    /**
     * 创建房间详情节点
     *
     * 实现接口方法，创建一个 RoomDetailsFlowNode 作为房间详情模块的根节点。
     * 将传入的参数和回调作为插件子节点。
     *
     * @param传递给 parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 入口点参数，包含初始导航目标
     * @param callback 回调接口，用于处理模块内部事件
     * @return 创建的 RoomDetailsFlowNode 节点
     * @see RoomDetailsFlowNode 房间详情流程节点
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: RoomDetailsEntryPoint.Params,
        callback: RoomDetailsEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<RoomDetailsFlowNode>(
            buildContext = buildContext,
            plugins = listOf(params, callback)
        )
    }
}

/**
 * 将入口点初始目标转换为导航目标
 *
 * 内部扩展函数，用于将 InitialTarget 转换为 RoomDetailsFlowNode 的 NavTarget。
 * 这个转换使得入口点的导航目标可以与内部导航系统兼容。
 *
 * @return 对应的 NavTarget 枚举值
 * @see InitialTarget 入口点初始目标
 * @see NavTarget 流程节点导航目标
 */
internal fun InitialTarget.toNavTarget() = when (this) {
    is InitialTarget.RoomDetails -> NavTarget.RoomDetails
    is InitialTarget.RoomMemberDetails -> NavTarget.RoomMemberDetails(roomMemberId)
    is InitialTarget.RoomNotificationSettings -> NavTarget.RoomNotificationSettings(showUserDefinedSettingStyle = true)
    InitialTarget.RoomMemberList -> NavTarget.RoomMemberList
}
