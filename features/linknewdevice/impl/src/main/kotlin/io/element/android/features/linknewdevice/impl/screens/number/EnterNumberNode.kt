/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.number

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

/**
 * 输入校验码页面内部导航接口。
 */
interface EnterNumberNavigator {
    /** 跳转到“数字不匹配”错误页。 */
    fun navigateToWrongNumberError()
}

@ContributesNode(SessionScope::class)
@AssistedInject
/**
 * 输入校验码页面节点。
 *
 * 负责连接 [EnterNumberPresenter] 与页面视图，并将错误导航委托给外层流程。
 */
class EnterNumberNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: EnterNumberPresenter.Factory,
) : Node(buildContext, plugins = plugins), EnterNumberNavigator {
    private val presenter = presenterFactory.create(this)

    /** 输入校验码页面向上抛出的导航回调。 */
    interface Callback : Plugin {
        fun navigateToWrongNumberError()
        fun navigateBack()
    }

    private val callback: Callback = callback()

    /**
     * 渲染输入校验码页面。
     *
     * @param modifier 应用于页面根节点的修饰符。
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        EnterNumberView(
            state = state,
            modifier = modifier,
            onBackClick = callback::navigateBack,
        )
    }

    /** 通知外层流程跳转到“数字不匹配”错误页。 */
    override fun navigateToWrongNumberError() {
        callback.navigateToWrongNumberError()
    }
}
