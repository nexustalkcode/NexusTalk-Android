/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.architecture.NodeInputs

/**
 * 显示位置功能入口点接口
 *
 * 定义了显示位置信息的入口，负责创建和管理显示位置的节点。
 */
interface ShowLocationEntryPoint : FeatureEntryPoint {
    /**
     * 输入参数数据类
     *
     * @property location 要显示的位置
     * @property description 位置描述
     */
    data class Inputs(
        val location: Location,
        val description: String?,
    ) : NodeInputs

    /**
     * 创建显示位置节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param inputs 输入参数
     * @return Node 显示位置节点实例
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        inputs: Inputs,
    ): Node
}
