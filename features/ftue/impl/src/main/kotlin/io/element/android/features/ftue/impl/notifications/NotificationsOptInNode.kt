/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.notifications

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.inputs

/**
 * 通知权限选择节点
 *
 * 这是 FTUE 流程中的通知权限请求页面节点，继承自 Node。
 * 使用 @ContributesNode 注解将其注册到 AppScope，
 * 使用 @AssistedInject 注解实现依赖注入。
 *
 * 主要职责：
 * - 使用 Presenter 获取通知权限选择的状态
 * - 使用 View 组件渲染通知权限选择界面
 * - 处理用户完成通知权限选择后的回调
 *
 * @param buildContext 构建上下文
 * @param plugins 插件列表
 * @param presenterFactory 通知权限选择 Presenter 工厂
 */
@ContributesNode(AppScope::class)
@AssistedInject
class NotificationsOptInNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: NotificationsOptInPresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    /**
     * 通知权限选择完成回调接口
     *
     * 定义通知权限选择完成后的回调方法，
     * 实现此接口的父节点可以接收通知权限选择完成的事件。
     */
    interface Callback : NodeInputs {
        /**
         * 通知权限选择完成
         *
         * 当用户完成通知权限选择（无论选择允许或拒绝）后调用此方法。
         */
        fun onNotificationsOptInFinished()
    }

    private val callback = inputs<Callback>()

    private val presenter: NotificationsOptInPresenter = presenterFactory.create(callback)

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        NotificationsOptInView(
            state = state,
            onBack = { callback.onNotificationsOptInFinished() },
            modifier = modifier
        )
    }
}
