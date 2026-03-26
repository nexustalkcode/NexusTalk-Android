/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.createroom.api.CreateRoomEntryPoint
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.SessionScope

/**
 * 默认创建房间入口点实现
 *
 * CreateRoomEntryPoint 接口的默认实现，负责创建创建房间流程的主节点。
 * 绑定到 SessionScope 生命周期。
 */
@ContributesBinding(SessionScope::class)
class DefaultCreateRoomEntryPoint : CreateRoomEntryPoint {
    /**
     * 创建创建房间流程节点
     *
     * @param isSpace 是否创建为空间
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param callback 回调接口
     * @return 创建房间流程节点
     */
    override fun createNode(
        isSpace: Boolean,
        parentNode: Node,
        buildContext: BuildContext,
        callback: CreateRoomEntryPoint.Callback,
        addPeopleCallback: CreateRoomEntryPoint.AddPeopleCallback?,
    ): Node {
        val inputs = CreateRoomFlowNode.Inputs(isSpace)
        val plugins = buildList {
            add(inputs)
            add(callback)
            addPeopleCallback?.let { add(it) }
        }
        return parentNode.createNode<CreateRoomFlowNode>(buildContext, plugins)
    }
}
