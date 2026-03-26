/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.core.plugin.plugins
import com.bumble.appyx.navmodel.backstack.BackStack
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.lockscreen.api.LockScreenEntryPoint
import io.element.android.features.lockscreen.impl.settings.LockScreenSettingsFlowNode
import io.element.android.features.lockscreen.impl.setup.LockScreenSetupFlowNode
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.SessionScope
import kotlinx.parcelize.Parcelize

/**
 * 锁屏流程节点
 *
 * 管理锁屏模块的导航流程，包括设置流程和设置页面。
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class LockScreenFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
) : BaseFlowNode<LockScreenFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = plugins.filterIsInstance<Inputs>().first().initialNavTarget,
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
) {
    /**
     * 输入数据类
     *
     * @property initialNavTarget 初始导航目标
     */
    data class Inputs(
        val initialNavTarget: NavTarget,
    ) : NodeInputs

    /**
     * 导航目标密封接口
     *
     * 定义锁屏流程中的导航目标。
     */
    sealed interface NavTarget : Parcelable {
        /** 设置流程 */
        @Parcelize
        data object Setup : NavTarget

        /** 设置页面 */
        @Parcelize
        data object Settings : NavTarget
    }

    /**
     * 设置完成回调
     *
     * 当设置流程完成时通知所有注册的回调。
     */
    private class OnSetupDoneCallback(private val plugins: List<LockScreenEntryPoint.Callback>) : LockScreenSetupFlowNode.Callback {
        override fun onSetupDone() {
            plugins.forEach {
                it.onSetupDone()
            }
        }
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.Setup -> {
                val callback = OnSetupDoneCallback(plugins())
                createNode<LockScreenSetupFlowNode>(buildContext, plugins = listOf(callback))
            }
            NavTarget.Settings -> {
                createNode<LockScreenSettingsFlowNode>(buildContext)
            }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        BackstackView()
    }
}
