/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.sessionverification.choosemode

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.logout.api.direct.DirectLogoutView
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.di.SessionScope

/**
 * 选择自验证方式节点
 *
 * 这是 FTUE 会话验证流程中选择验证方式的页面节点，继承自 Node。
 * 使用 @ContributesNode 注解将其注册到 SessionScope，
 * 使用 @AssistedInject 注解实现依赖注入。
 *
 * 主要职责：
 * - 使用 Presenter 获取选择验证方式的状态
 * - 使用 View 组件渲染选择界面
 * - 处理用户选择不同验证方式后的导航回调
 *
 * @param buildContext 构建上下文
 * @param plugins 插件列表
 * @param presenter 选择自验证方式状态的 Presenter
 * @param directLogoutView 直接退出登录视图组件
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class ChooseSelfVerificationModeNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: Presenter<ChooseSelfVerificationModeState>,
    private val directLogoutView: DirectLogoutView,
) : Node(buildContext, plugins = plugins) {
    /**
     * 选择自验证方式导航回调接口
     *
     * 定义用户选择不同验证方式时的导航回调方法。
     */
    interface Callback : Plugin {
        fun onBack()

        /**
         * 导航到使用另一台设备验证
         */
        fun navigateToUseAnotherDevice()

        /**
         * 导航到输入恢复密钥验证
         */
        fun navigateToUseRecoveryKey()

        /**
         * 导航到重置密钥/身份
         */
        fun navigateToResetKey()

        /**
         * 导航到了解更多关于加密的信息
         */
        fun navigateToLearnMoreAboutEncryption()
    }

    private val callback: Callback = callback()

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()

        ChooseSelfVerificationModeView(
            state = state,
            onBack = callback::onBack,
            onUseAnotherDevice = callback::navigateToUseAnotherDevice,
            onUseRecoveryKey = callback::navigateToUseRecoveryKey,
            onResetKey = callback::navigateToResetKey,
            onLearnMore = callback::navigateToLearnMoreAboutEncryption,
            modifier = modifier,
        )

        directLogoutView.Render(state = state.directLogoutState)
    }
}
