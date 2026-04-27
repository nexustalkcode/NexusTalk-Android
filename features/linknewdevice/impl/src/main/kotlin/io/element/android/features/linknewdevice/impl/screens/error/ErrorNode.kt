/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope

@ContributesNode(SessionScope::class)
@AssistedInject
/**
 * 设备关联流程的错误页节点。
 *
 * 根据注入的 [ErrorScreenType] 渲染不同错误文案，并提供统一的重试出口。
 */
class ErrorNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
) : Node(buildContext = buildContext, plugins = plugins) {
    /**
     * 错误页回调。
     */
    interface Callback : Plugin {
        fun onRetry()
    }

    private val callback: Callback = callback()
    private val errorScreenType = inputs<ErrorScreenType>()

    /**
     * 渲染错误页面。
     *
     * @param modifier 应用于页面根节点的修饰符。
     */
    @Composable
    override fun View(modifier: Modifier) {
        ErrorView(
            modifier = modifier,
            errorScreenType = errorScreenType,
            onRetry = callback::onRetry,
        )
    }
}
