/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetailsedit.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.MobileScreen
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.di.RoomScope
import io.element.android.services.analytics.api.AnalyticsService

/**
 * 房间详情编辑页面节点
 *
 * 代表房间详情编辑功能的页面节点，负责展示编辑界面并处理用户交互
 *
 * @property buildContext 构建上下文，包含节点构建所需的信息
 * @property plugins 插件列表，用于扩展节点功能
 * @property presenter 房间详情编辑Presenter，负责业务逻辑处理
 * @property analyticsService 分析服务，用于追踪页面浏览
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class RoomDetailsEditNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    /** 房间详情编辑Presenter，负责管理编辑状态和业务逻辑 */
    private val presenter: RoomDetailsEditPresenter,
    /** 分析服务，用于记录页面访问事件 */
    private val analyticsService: AnalyticsService,
) : Node(buildContext, plugins = plugins) {
    init {
        lifecycle.subscribe(
            onResume = {
                analyticsService.screen(MobileScreen(screenName = MobileScreen.ScreenName.RoomSettings))
            }
        )
    }

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        RoomDetailsEditView(
            state = state,
            onDone = ::navigateUp,
            modifier = modifier,
        )
    }
}
