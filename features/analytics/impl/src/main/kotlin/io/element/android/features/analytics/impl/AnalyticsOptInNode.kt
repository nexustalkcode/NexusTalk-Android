/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.analytics.impl

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.appconfig.AnalyticsConfig
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.androidutils.browser.openUrlInChromeCustomTab

/**
 * 分析功能选择节点
 *
 * 表示分析功能选择页面的节点，负责连接 Presenter 和 View。
 * 继承自 Node，作为 Appyx 导航体系中的页面组件。
 * 支持通过 Chrome Custom Tab 打开隐私政策链接。
 *
 * @property presenter 分析功能选择 Presenter
 * @see AnalyticsOptInPresenter 分析功能选择 Presenter
 * @see AnalyticsOptInView 分析功能选择视图
 */
@ContributesNode(AppScope::class)
@AssistedInject
class AnalyticsOptInNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: AnalyticsOptInPresenter,
) : Node(buildContext, plugins = plugins) {
    /**
     * 点击隐私政策链接
     *
     * @param activity 当前 Activity
     * @param darkTheme 是否使用深色主题
     */
    private fun onClickTerms(activity: Activity, darkTheme: Boolean) {
        activity.openUrlInChromeCustomTab(null, darkTheme, AnalyticsConfig.POLICY_LINK)
    }

    /**
     * 渲染分析选择视图
     *
     * @param modifier 修饰符
     */
    @Composable
    override fun View(modifier: Modifier) {
        val activity = requireNotNull(LocalActivity.current)
        val isDark = ElementTheme.isLightTheme.not()
        val state = presenter.present()
        AnalyticsOptInView(
            state = state,
            modifier = modifier,
            onClickTerms = { onClickTerms(activity, isDark) },
        )
    }
}
