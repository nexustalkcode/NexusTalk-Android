/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdirectory.impl.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.roomdirectory.api.RoomDirectoryEntryPoint
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.di.SessionScope

/**
 * 房间目录节点
 *
 * 房间目录功能的主要界面节点，负责展示房间目录搜索界面。
 * 继承自 Appyx 框架的 Node 类，用于管理界面的生命周期和导航。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property presenter 房间目录 Presenter，负责业务逻辑处理
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class RoomDirectoryNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: RoomDirectoryPresenter,
) : Node(buildContext, plugins = plugins) {
    private val callback: RoomDirectoryEntryPoint.Callback = callback()

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        RoomDirectoryView(
            state = state,
            onResultClick = callback::navigateToRoom,
            onBackClick = ::navigateUp,
            modifier = modifier
        )
    }
}
