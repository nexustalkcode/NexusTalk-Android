/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.qrcode

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope

@ContributesNode(SessionScope::class)
@AssistedInject
/**
 * 展示二维码页面节点。
 *
 * 负责读取二维码内容输入，并把返回动作回传给外层流程。
 */
class ShowQrCodeNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
) : Node(buildContext, plugins = plugins) {
    /**
     * 展示二维码页面输入。
     *
     * @property data 需要编码展示的二维码内容。
     */
    class Inputs(
        val data: String,
    ) : NodeInputs

    /**
     * 展示二维码页面向上抛出的回调。
     */
    interface Callback : Plugin {
        fun navigateBack()
    }

    private val inputs: Inputs = inputs<Inputs>()
    private val callback: Callback = callback()

    /**
     * 渲染展示二维码页面。
     *
     * @param modifier 应用于页面根节点的修饰符。
     */
    @Composable
    override fun View(modifier: Modifier) {
        ShowQrCodeView(
            data = inputs.data,
            modifier = modifier,
            onBackClick = callback::navigateBack,
        )
    }
}
