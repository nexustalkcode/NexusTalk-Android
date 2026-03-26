/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.verifysession.impl.outgoing

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.verifysession.api.OutgoingVerificationEntryPoint
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope

/**
 * 发起方验证节点（Outgoing Verification Node）
 *
 * 用于展示「由当前用户发起」的端到端验证流程界面。当用户主动向另一台设备或用户发起
 * 验证请求时，本节点负责呈现等待对方响应、验证步骤说明以及完成/取消等交互。
 *
 * 主要职责：
 * - 接收并展示验证请求参数（如是否显示设备已验证界面、验证请求详情）
 * - 通过 Presenter 获取 UI 状态并驱动 [OutgoingVerificationView]
 * - 将用户操作（了解更多加密、完成、返回）回调给导航层
 *
 * 依赖注入：通过 [ContributesNode] 在 Session 作用域内注册，便于在会话流程中导航到此节点。
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class OutgoingVerificationNode(
    /** Appyx 提供的构建上下文，用于节点在导航树中的定位与生命周期 */
    @Assisted buildContext: BuildContext,
    /** 可挂载到本节点的插件列表，用于扩展行为（如埋点、无障碍等） */
    @Assisted plugins: List<Plugin>,
    /** 用于创建 [OutgoingVerificationPresenter] 的工厂，由 DI 注入 */
    presenterFactory: OutgoingVerificationPresenter.Factory,
) : Node(buildContext, plugins = plugins) {

    /** 与导航层约定的回调，用于「了解更多加密」「完成」「返回」等用户操作 */
    private val callback: OutgoingVerificationEntryPoint.Callback = callback()

    /** 进入本节点时传入的参数（如是否显示设备已验证界面、当前验证请求） */
    private val inputs = inputs<OutgoingVerificationEntryPoint.Params>()

    /** 发起方验证的 Presenter，负责业务逻辑与 UI 状态的转换 */
    private val presenter = presenterFactory.create(
        showDeviceVerifiedScreen = inputs.showDeviceVerifiedScreen,
        verificationRequest = inputs.verificationRequest,
    )

    /**
     * 构建发起方验证的 Compose 视图。
     *
     * 从 Presenter 获取最新状态并交给 [OutgoingVerificationView] 渲染；
     * 用户点击「了解更多加密」「完成」「返回」时通过 [callback] 通知上层。
     *
     * @param modifier 应用于根布局的 Modifier，用于尺寸、内边距等
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        OutgoingVerificationView(
            state = state,
            modifier = modifier,
            onLearnMoreClick = callback::navigateToLearnMoreAboutEncryption,
            onFinish = callback::onDone,
            onBack = callback::onBack,
        )
    }
}
