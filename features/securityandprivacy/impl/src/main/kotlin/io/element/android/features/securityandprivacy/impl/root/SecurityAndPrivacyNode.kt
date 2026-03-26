/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl.root

import android.app.Activity
import androidx.activity.compose.LocalActivity
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
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.securityandprivacy.impl.SecurityAndPrivacyNavigator
import io.element.android.libraries.androidutils.browser.openUrlInChromeCustomTab
import io.element.android.libraries.architecture.appyx.launchMolecule
import io.element.android.libraries.di.RoomScope

/**
 * 安全与隐私节点
 *
 * 安全与隐私设置的主页面节点。
 * 负责显示安全与隐私设置界面，包括：
 * - 房间访问权限设置
 * - 房间加密设置
 * - 历史可见性设置
 * - 房间地址和目录可见性设置
 *
 * @see Node 基础节点类
 * @see SecurityAndPrivacyPresenter 业务逻辑处理类
 * @see SecurityAndPrivacyView 界面渲染组件
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class SecurityAndPrivacyNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    /** Presenter 工厂，用于创建业务逻辑处理类 */
    presenterFactory: SecurityAndPrivacyPresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    private val navigator = plugins<SecurityAndPrivacyNavigator>().first()
    private val presenter = presenterFactory.create(navigator)

    private val stateFlow = launchMolecule { presenter.present() }

    private fun onOpenExternalUrl(activity: Activity, darkTheme: Boolean, url: String) {
        activity.openUrlInChromeCustomTab(null, darkTheme, url)
    }

    @Composable
    override fun View(modifier: Modifier) {
        val activity = requireNotNull(LocalActivity.current)
        val isDark = ElementTheme.isLightTheme.not()
        val state by stateFlow.collectAsState()
        SecurityAndPrivacyView(
            state = state,
            onLinkClick = { url ->
                onOpenExternalUrl(activity, isDark, url)
            },
            modifier = modifier
        )
    }
}
