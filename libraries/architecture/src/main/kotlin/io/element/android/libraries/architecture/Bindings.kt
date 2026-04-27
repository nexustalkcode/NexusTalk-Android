/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.architecture

import android.content.Context
import android.content.ContextWrapper
import com.bumble.appyx.core.node.Node
import io.element.android.libraries.di.DependencyInjectionGraphOwner

/**
 * 按类型从当前节点作用域解析绑定。
 */
inline fun <reified T : Any> Node.bindings() = bindings(T::class.java)
/**
 * 按类型从当前上下文作用域解析绑定。
 */
inline fun <reified T : Any> Context.bindings() = bindings(T::class.java)

/**
 * 从上下文链和应用上下文中查找指定类型的绑定实例。
 */
fun <T : Any> Context.bindings(klass: Class<T>): T {
    // search the components in the dependency injection graph
    return generateSequence(this) { (it as? ContextWrapper)?.baseContext }
        .plus(applicationContext)
        .filterIsInstance<DependencyInjectionGraphOwner>()
        .map { it.graph }
        .flatMap { it as? Collection<*> ?: listOf(it) }
        .filterIsInstance(klass)
        .firstOrNull()
        ?: error("Unable to find bindings for ${klass.name}")
}

/**
 * 从节点父链中查找指定类型的绑定实例。
 */
fun <T : Any> Node.bindings(klass: Class<T>): T {
    // search the components in the node hierarchy
    return generateSequence(this, Node::parent)
        .filterIsInstance<DependencyInjectionGraphOwner>()
        .map { it.graph }
        .flatMap { it as? Collection<*> ?: listOf(it) }
        .filterIsInstance(klass)
        .firstOrNull()
        ?: error("Unable to find bindings for ${klass.name}")
}
