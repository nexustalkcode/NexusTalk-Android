/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.share.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.share.api.ShareEntryPoint
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.SessionScope

/**
 * ShareEntryPoint 的默认实现
 *
 * 提供分享功能的默认入口点实现，创建 ShareNode 节点。
 * 该实现在会话范围内提供，与用户的登录会话关联。
 *
 * @see ShareEntryPoint 分享入口点接口
 * @see ShareNode 分享节点
 */
/**
 * Default implementation of ShareEntryPoint.
 *
 * Provides the default entry point implementation for the share feature, creating ShareNode.
 * This implementation is provided within the session scope, associated with the user's login session.
 *
 * @see ShareEntryPoint Share entry point interface
 * @see ShareNode Share node
 */
@ContributesBinding(SessionScope::class)
class DefaultShareEntryPoint : ShareEntryPoint {
    /**
     * 创建分享节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param params 创建参数
     * @param callback 回调接口
     * @return ShareNode 实例
     */
    /**
     * Creates a share node.
     *
     * @param parentNode The parent node
     * @param buildContext The build context
     * @param params Creation parameters
     * @param callback The callback interface
     * @return ShareNode instance
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: ShareEntryPoint.Params,
        callback: ShareEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<ShareNode>(
            buildContext = buildContext,
            plugins = listOf(
                ShareNode.Inputs(intent = params.intent),
                callback,
            )
        )
    }
}
