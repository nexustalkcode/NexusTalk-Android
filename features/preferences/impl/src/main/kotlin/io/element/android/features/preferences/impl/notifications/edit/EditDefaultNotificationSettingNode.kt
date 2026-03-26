/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.notifications.edit

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
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 编辑默认通知设置页面 Node
 *
 * 负责显示编辑默认通知设置页面，允许用户设置群组或一对一聊天的默认通知模式。
 *
 * @property presenterFactory 编辑默认通知设置 Presenter 工厂
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class EditDefaultNotificationSettingNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: EditDefaultNotificationSettingPresenter.Factory
) : Node(buildContext, plugins = plugins) {
    /**
     * 页面回调接口
     */
    interface Callback : Plugin {
        /** 导航到房间通知设置页面 */
        fun navigateToRoomNotificationSettings(roomId: RoomId)
    }

    /**
     * 输入数据类
     */
    data class Inputs(
        val isOneToOne: Boolean
    ) : NodeInputs

    private val callback: Callback = callback()
    private val inputs = inputs<Inputs>()
    private val presenter = presenterFactory.create(inputs.isOneToOne)

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        EditDefaultNotificationSettingView(
            state = state,
            openRoomNotificationSettings = callback::navigateToRoomNotificationSettings,
            onBackClick = ::navigateUp,
            modifier = modifier,
        )
    }
}
