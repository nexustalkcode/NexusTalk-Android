/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.di.SessionScope

@ContributesNode(SessionScope::class)
@AssistedInject
/**
 * 新设备关联根页面节点。
 *
 * 负责渲染“关联移动端/桌面端”的起始页面，并把用户选择回传给外层流程。
 */
class LinkNewDeviceRootNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: LinkNewDeviceRootPresenter,
) : Node(buildContext, plugins = plugins) {
    /**
     * 根页面向上抛出的回调。
     */
    interface Callback : Plugin {
        fun onDone()
        fun linkDesktopDevice()
    }

    private val callback: Callback = callback()

    /**
     * 渲染新设备关联根页面。
     *
     * @param modifier 应用于页面根节点的修饰符。
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        LinkNewDeviceRootView(
            state = state,
            modifier = modifier,
            onBackClick = callback::onDone,
            onLinkDesktopDeviceClick = callback::linkDesktopDevice,
        )
    }
}
