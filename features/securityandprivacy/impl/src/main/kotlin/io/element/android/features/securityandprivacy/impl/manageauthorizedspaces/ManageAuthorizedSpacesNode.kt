/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl.manageauthorizedspaces

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.core.plugin.plugins
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.appyx.launchMolecule
import io.element.android.libraries.di.RoomScope

/**
 * 管理授权空间节点
 *
 * 负责显示和管理授权空间的页面。
 * 用户可以选择哪些空间可以访问当前房间（用于受限房间访问规则）。
 *
 * @see Node 基础节点类
 * @see ManageAuthorizedSpacesPresenter 业务逻辑处理类
 * @see ManageAuthorizedSpacesView 界面渲染组件
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class ManageAuthorizedSpacesNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    /** Presenter，负责处理业务逻辑 */
    presenter: ManageAuthorizedSpacesPresenter,
) : Node(buildContext, plugins = plugins) {
    private val stateFlow = launchMolecule { presenter.present() }

    @Composable
    override fun View(modifier: Modifier) {
        val state by stateFlow.collectAsState()
        ManageAuthorizedSpacesView(
            state = state,
            modifier = modifier
        )
    }
}
