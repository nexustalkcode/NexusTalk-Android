/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.scanuser

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.di.SessionScope

/**
 * 扫描用户二维码节点
 *
 * 负责创建和管理扫描用户二维码功能的视图节点。
 *
 * @param buildContext 构建上下文
 * @param plugins 插件列表
 * @param presenter 扫描用户二维码 Presenter
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class ScanUserQrCodeNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: ScanUserQrCodePresenter,
) : Node(buildContext = buildContext, plugins = plugins) {

    /**
     * 回调接口
     */
    interface Callback : Plugin {
        /**
         * 用户ID扫描成功回调
         *
         * @param userId 扫描到的用户ID
         */
        fun onUserIdScanned(userId: String)
    }

    private val callback: Callback = callback()

    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()

        // 扫描成功后自动关闭页面并传递数据
        LaunchedEffect(state.scanAction) {
            if (state.scanAction is AsyncAction.Success) {
                val userId = (state.scanAction as AsyncAction.Success).data
                callback.onUserIdScanned(userId)
                navigateUp()
            }
        }

        ScanUserQrCodeView(
            state = state,
            onBackClick = {
                navigateUp()
            },
            modifier = modifier,
        )
    }
}
