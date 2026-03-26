/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.appconfig.LearnMoreConfig
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.di.SessionScope

/**
 * 安全备份根节点
 *
 * 负责显示安全备份根页面的节点。
 * 页面展示安全备份的当前状态，并提供设置、更改、禁用、输入恢复密钥等操作入口。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property presenter 安全备份根页面业务逻辑处理 presenter
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class SecureBackupRootNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: SecureBackupRootPresenter,
) : Node(
    buildContext = buildContext,
    plugins = plugins
) {
    /**
     * 安全备份根页面回调接口
     */
    interface Callback : Plugin {
        /** 导航到设置恢复密钥页面 */
        fun navigateToSetup()

        /** 导航到更改恢复密钥页面 */
        fun navigateToChange()

        /** 导航到禁用安全备份页面 */
        fun navigateToDisable()

        /** 导航到输入恢复密钥页面 */
        fun navigateToEnterRecoveryKey()
    }

    private val callback: Callback = callback()

    private fun onLearnMoreClick(uriHandler: UriHandler) {
        uriHandler.openUri(LearnMoreConfig.SECURE_BACKUP_URL)
    }

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        val uriHandler = LocalUriHandler.current
        SecureBackupRootView(
            state = state,
            onBackClick = ::navigateUp,
            onSetupClick = callback::navigateToSetup,
            onChangeClick = callback::navigateToChange,
            onDisableClick = callback::navigateToDisable,
            onConfirmRecoveryKeyClick = callback::navigateToEnterRecoveryKey,
            onLearnMoreClick = { onLearnMoreClick(uriHandler) },
            modifier = modifier,
        )
    }
}
