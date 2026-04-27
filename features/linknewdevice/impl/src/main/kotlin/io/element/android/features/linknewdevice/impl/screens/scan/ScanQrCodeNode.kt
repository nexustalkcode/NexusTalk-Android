/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.scan

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
 * 扫描二维码页面节点。
 *
 * 负责连接 [ScanQrCodePresenter] 与页面视图，并把返回动作回传给外层流程。
 */
class ScanQrCodeNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: ScanQrCodePresenter,
) : Node(buildContext, plugins = plugins) {
    /**
     * 扫描二维码页面向上抛出的回调。
     */
    interface Callback : Plugin {
        fun cancel()
    }

    private val callback: Callback = callback()

    /**
     * 渲染扫描二维码页面。
     *
     * @param modifier 应用于页面根节点的修饰符。
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        ScanQrCodeView(
            state = state,
            onBackClick = callback::cancel,
            modifier = modifier,
        )
    }
}
