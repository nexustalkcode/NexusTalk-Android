/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl.editroomaddress

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.core.plugin.plugins
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.securityandprivacy.impl.SecurityAndPrivacyNavigator
import io.element.android.libraries.di.RoomScope

/**
 * 编辑房间地址节点
 *
 * 负责显示和处理编辑房间地址的页面。
 * 使用 Presenter 模式处理业务逻辑，并通过 View 渲染界面。
 *
 * @see Node 基础节点类
 * @see EditRoomAddressPresenter 业务逻辑处理类
 * @see EditRoomAddressView 界面渲染组件
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class EditRoomAddressNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    /** Presenter 工厂，用于创建业务逻辑处理类 */
    presenterFactory: EditRoomAddressPresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    private val navigator = plugins<SecurityAndPrivacyNavigator>().first()
    private val presenter = presenterFactory.create(navigator)

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        EditRoomAddressView(
            state = state,
            onBackClick = ::navigateUp,
            modifier = modifier
        )
    }
}
