/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.qrcode.intro

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.login.impl.di.QrCodeLoginScope
import io.element.android.libraries.architecture.callback

@ContributesNode(QrCodeLoginScope::class)
@AssistedInject
/**
 * 二维码登录引导页节点。
 *
 * 负责渲染引导说明，并把返回/继续动作回传给流程外层。
 */
class QrCodeIntroNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: QrCodeIntroPresenter,
) : Node(buildContext, plugins = plugins) {
    /**
     * 引导页向上抛出的回调。
     */
    interface Callback : Plugin {
        fun cancel()
        fun navigateToQrCodeScan()
    }

    private val callback: Callback = callback()

    /**
     * 渲染二维码登录引导页。
     *
     * @param modifier 应用于页面根节点的修饰符。
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        QrCodeIntroView(
            state = state,
            onBackClick = callback::cancel,
            onContinue = callback::navigateToQrCodeScan,
            modifier = modifier
        )
    }
}
