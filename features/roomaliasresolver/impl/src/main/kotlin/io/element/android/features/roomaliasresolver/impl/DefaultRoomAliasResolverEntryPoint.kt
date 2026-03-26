/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomaliasresolver.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.roomaliasesolver.api.RoomAliasResolverEntryPoint
import io.element.android.libraries.architecture.createNode

/**
 * 房间别名解析器入口点默认实现
 *
 * 该类是 RoomAliasResolverEntryPoint 接口的默认实现，
 * 负责创建并返回房间别名解析功能的节点。
 *
 * 使用 @ContributesBinding 注解将其绑定到 AppScope，
 * 使其可以在依赖注入系统中被自动注入使用。
 *
 * @see RoomAliasResolverEntryPoint 接口定义
 * @see RoomAliasResolverNode 解析节点
 */
@ContributesBinding(AppScope::class)
class DefaultRoomAliasResolverEntryPoint : RoomAliasResolverEntryPoint {
    /**
     * 创建房间别名解析节点
     *
     * @param parentNode 父节点，用于构建节点树
     * @param buildContext 构建上下文
     * @param params 入口点参数，包含要解析的房间别名
     * @param callback 回调接口，用于通知解析结果
     * @return 创建的 RoomAliasResolverNode 节点
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: RoomAliasResolverEntryPoint.Params,
        callback: RoomAliasResolverEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<RoomAliasResolverNode>(
            buildContext = buildContext,
            plugins = listOf(params, callback),
        )
    }
}
