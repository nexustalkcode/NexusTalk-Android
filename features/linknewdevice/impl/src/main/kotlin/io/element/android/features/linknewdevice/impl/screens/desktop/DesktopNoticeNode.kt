/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.desktop

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
 * 桌面端关联说明页节点。
 *
 * 负责展示开始扫码前的提示说明，并把继续/返回动作回传给流程节点。
 */
class DesktopNoticeNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: DesktopNoticePresenter,
) : Node(buildContext, plugins = plugins) {
    /**
     * 桌面端说明页回调。
     */
    interface Callback : Plugin {
        // 返回上一页
        fun navigateBack()
        // 进入二维码扫描页
        fun navigateToQrCodeScanner()
    }

    private val callback: Callback = callback()

    /**
     * 渲染桌面端关联说明页。
     *
     * @param modifier 应用于页面根节点的修饰符。
     */
    @Composable
    override fun View(modifier: Modifier) {
        // 生成状态并渲染提示页
        val state = presenter.present()
        DesktopNoticeView(
            state = state,
            modifier = modifier,
            onBackClick = callback::navigateBack,
            onReadyToScanClick = callback::navigateToQrCodeScanner,
        )
    }
}
