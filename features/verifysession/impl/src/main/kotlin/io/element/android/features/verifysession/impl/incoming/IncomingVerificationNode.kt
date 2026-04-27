/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.verifysession.impl.incoming

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.verifysession.api.IncomingVerificationEntryPoint
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope

@ContributesNode(SessionScope::class)
@AssistedInject
/**
 * 传入验证页面节点。
 *
 * 负责根据入口参数创建 Presenter，并在验证流程结束后把完成事件回传给上层。
 */
class IncomingVerificationNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: IncomingVerificationPresenter.Factory,
) : Node(buildContext, plugins = plugins),
    IncomingVerificationNavigator {
    private val callback: IncomingVerificationEntryPoint.Callback = callback()
    private val presenter = presenterFactory.create(
        verificationRequest = inputs<IncomingVerificationEntryPoint.Params>().verificationRequest,
        navigator = this,
    )

    /**
     * 验证流程结束时通知上层回调。
     */
    override fun onFinish() {
        callback.onDone()
    }

    /**
     * 渲染传入验证页面。
     *
     * @param modifier 应用于页面根节点的修饰符。
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        IncomingVerificationView(
            state = state,
            modifier = modifier,
        )
    }
}
