/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.notificationsettings

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
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.RoomScope
import io.element.android.services.analytics.api.AnalyticsService

/**
 * 房间通知设置节点
 *
 * 负责显示和管理房间通知设置页面的节点。
 * 使用 @ContributesNode 注解将其贡献到 RoomScope 进行依赖注入。
 * 继承自 Node 基类，处理通知设置界面的展示和交互。
 *
 * @see Node 应用节点基类
 * @see ContributesNode 节点贡献注解
 * @see AssistedInject 依赖注入注解
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class RoomNotificationSettingsNode(
    /** 构建上下文 */
    @Assisted buildContext: BuildContext,
    /** 插件列表 */
    @Assisted plugins: List<Plugin>,
    /** 通知设置 Presenter 工厂 */
    presenterFactory: RoomNotificationSettingsPresenter.Factory,
    /** 分析服务，用于跟踪用户行为 */
    private val analyticsService: AnalyticsService,
) : Node(buildContext, plugins = plugins) {
    /**
     * 通知设置输入数据类
     *
     * 实现 NodeInputs 接口，定义节点所需的输入数据。
     *
     * @property showUserDefinedSettingStyle 是否显示用户定义设置样式
     * @see NodeInputs 节点输入接口
     */
    data class RoomNotificationSettingInput(
        val showUserDefinedSettingStyle: Boolean
    ) : NodeInputs

    /**
     * 通知设置回调接口
     *
     * 定义通知设置页面需要与外部交互的回调方法。
     *
     * @see Plugin 插件接口基类
     */
    interface Callback : Plugin {
        /**
         * 导航到全局通知设置
         */
        fun navigateToGlobalNotificationSettings()
    }

    /** 回调接口实例 */
    private val callback: Callback = callback()
    /** 输入数据 */
    private val inputs = inputs<RoomNotificationSettingInput>()

    /** 通知设置 Presenter */
    private val presenter = presenterFactory.create(inputs.showUserDefinedSettingStyle)

    /**
     * 初始化订阅生命周期事件
     *
     * 订阅节点的生命周期事件，当页面恢复时发送分析屏幕事件。
     */
    init {
        lifecycle.subscribe(
            onResume = {
                analyticsService.screen(MobileScreen(screenName = MobileScreen.ScreenName.RoomNotifications))
            }
        )
    }

    /**
     * 渲染通知设置视图
     *
     * 重写 View 方法，使用 Compose 框架渲染通知设置界面。
     *
     * @param modifier 视图修饰符
     * @see Compose Composable 注解
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        RoomNotificationSettingsView(
            state = state,
            modifier = modifier,
            onShowGlobalNotifications = callback::navigateToGlobalNotificationSettings,
            onBackClick = ::navigateUp,
        )
    }
}
