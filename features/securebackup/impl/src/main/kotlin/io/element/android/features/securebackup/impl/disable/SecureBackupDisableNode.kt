/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.disable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.di.SessionScope

/**
 * 禁用安全备份节点
 *
 * 负责显示禁用安全备份页面的节点。
 * 使用 [SecureBackupDisablePresenter] 处理业务逻辑，并使用 [SecureBackupDisableView] 渲染界面。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property presenter 禁用安全备份业务逻辑处理 presenter
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class SecureBackupDisableNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: SecureBackupDisablePresenter,
) : Node(buildContext, plugins = plugins) {
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        SecureBackupDisableView(
            state = state,
            modifier = modifier,
            onSuccess = ::navigateUp,
            onBackClick = ::navigateUp,
        )
    }
}
