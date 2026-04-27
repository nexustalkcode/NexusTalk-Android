/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.architecture

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Multibinds
import kotlin.reflect.KClass

/**
 * 从当前节点作用域创建指定类型的子节点。
 */
inline fun <reified N : Node> Node.createNode(
    buildContext: BuildContext,
    plugins: List<Plugin> = emptyList()
): N {
    val bindings: NodeFactoriesBindings = bindings()
    return bindings.createNode(buildContext, plugins)
}

/**
 * 从 NodeFactoriesBindings 中创建指定类型的节点。
 */
inline fun <reified N : Node> NodeFactoriesBindings.createNode(
    buildContext: BuildContext,
    plugins: List<Plugin>,
): N {
    val nodeClass = N::class
    val nodeFactoryMap = nodeFactories()
    // Note to developers: If you got the error below, make sure to build again after
    // clearing the cache (sometimes several times) to let codegen generate the NodeFactory.
    val nodeFactory = nodeFactoryMap[nodeClass] ?: error("Cannot find NodeFactory for ${nodeClass.java.name}.")

    @Suppress("UNCHECKED_CAST")
    val castedNodeFactory = nodeFactory as? AssistedNodeFactory<N>
    val node = castedNodeFactory?.create(buildContext, plugins)
    return node as N
}

/**
 * 提供节点工厂映射的绑定接口。
 */
fun interface NodeFactoriesBindings {
    @Multibinds
    fun nodeFactories(): Map<KClass<out Node>, AssistedNodeFactory<*>>
}
