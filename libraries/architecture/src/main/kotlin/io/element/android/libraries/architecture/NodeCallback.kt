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

/**
 * 从当前节点插件列表中解析单个回调接口实现。
 */
inline fun <reified I : Plugin> Node.callback(): I {
    return plugins.callback()
}

/**
 * 从插件列表中解析单个回调接口实现。
 */
inline fun <reified I : Plugin> List<Plugin>.callback(): I {
    return requireNotNull(filterIsInstance<I>().singleOrNull()) { "Make sure to actually pass a Callback plugin to your node" }
}
