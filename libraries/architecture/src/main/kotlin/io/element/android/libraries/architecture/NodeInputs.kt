/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.architecture

import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.core.plugin.plugins

/**
 * 作为节点输入参数传递的插件标记接口。
 */
interface NodeInputs : Plugin

/**
 * 从当前节点插件列表中解析单个输入对象。
 */
inline fun <reified I : NodeInputs> Node.inputs(): I {
    return requireNotNull(plugins<I>().firstOrNull()) { "Make sure to actually pass NodeInputs plugin to your node" }
}
