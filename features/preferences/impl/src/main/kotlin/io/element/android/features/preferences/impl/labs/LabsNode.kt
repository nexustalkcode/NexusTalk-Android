/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.labs

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
 * 实验室功能页面 Node
 *
 * 负责显示实验室功能页面，允许用户启用或禁用实验性功能。
 *
 * @property presenter 实验室功能 Presenter
 * @see Callback 页面回调接口
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class LabsNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: LabsPresenter,
) : Node(buildContext, plugins = plugins) {
    /**
     * 实验室功能页面回调接口
     */
    interface Callback : Plugin {
        /** 完成设置 */
        fun onDone()
    }

    val callback: Callback = callback()

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        LabsView(
            state = state,
            onBack = callback::onDone,
        )
    }
}
