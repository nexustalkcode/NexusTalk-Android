/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint

/**
 * 链接新设备功能入口点接口
 *
 * 定义了链接新设备功能的入口接口，负责创建和管理设备绑定流程的节点。
 */
interface LinkNewDeviceEntryPoint : FeatureEntryPoint {
    /**
     * 链接新设备流程回调接口
     */
    interface Callback : Plugin {
        /** 完成回调 */
        fun onDone()
    }

    /**
     * 创建一个链接新设备节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param callback 回调接口
     * @return Node 链接新设备节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: Callback,
    ): Node
}
