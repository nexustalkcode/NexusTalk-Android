/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.notifications

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
 * 通知设置页面 Node
 *
 * 负责显示通知设置页面，包括系统通知、应用通知、推送提供商等配置。
 *
 * @property presenter 通知设置 Presenter
 * @see Callback 页面回调接口
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class NotificationSettingsNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: NotificationSettingsPresenter,
) : Node(buildContext, plugins = plugins) {
    /**
     * 通知设置页面回调接口
     */
    interface Callback : Plugin {
        /** 导航到编辑默认通知设置页面 */
        fun navigateToEditDefaultNotificationSetting(isOneToOne: Boolean)
        /** 导航到通知故障排除页面 */
        fun navigateToTroubleshootNotifications()
    }

    private val callback: Callback = callback()

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        NotificationSettingsView(
            state = state,
            onOpenEditDefault = callback::navigateToEditDefaultNotificationSetting,
            onBackClick = ::navigateUp,
            onTroubleshootNotificationsClick = callback::navigateToTroubleshootNotifications,
            modifier = modifier,
        )
    }
}
