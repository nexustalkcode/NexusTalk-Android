/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(DelicateCoilApi::class)

package io.element.android.appnav

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.login.api.LoginEntryPoint
import io.element.android.features.login.api.LoginParams
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.designsystem.utils.ForceOrientationInMobileDevices
import io.element.android.libraries.designsystem.utils.ScreenOrientation
import io.element.android.libraries.matrix.ui.media.ImageLoaderHolder
import io.element.android.services.analytics.api.watchers.AnalyticsColdStartWatcher
import kotlinx.parcelize.Parcelize

/**
 * 未登录态总流程节点。
 *
 * 负责承载登录流程，并在未登录场景下强制保持竖屏。
 */
@ContributesNode(AppScope::class)
@AssistedInject
class NotLoggedInFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val loginEntryPoint: LoginEntryPoint,
    private val imageLoaderHolder: ImageLoaderHolder,
    private val analyticsColdStartWatcher: AnalyticsColdStartWatcher,
) : BaseFlowNode<NotLoggedInFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.Root,
        savedStateMap = buildContext.savedStateMap
    ),
    buildContext = buildContext,
    plugins = plugins,
) {
    /**
     * 未登录流程输入参数。
     */
    data class Params(
        val loginParams: LoginParams?,
    ) : NodeInputs

    /**
     * 未登录流程回调。
     */
    interface Callback : Plugin {
        fun navigateToBugReport()
        fun onDone()
    }

    private val callback: Callback = callback()
    private val inputs = inputs<Params>()

    /**
     * 在构建完成后注册冷启动分析与全局图片加载器。
     */
    override fun onBuilt() {
        super.onBuilt()
        analyticsColdStartWatcher.whenLoggingIn()
        lifecycle.subscribe(
            onResume = {
                SingletonImageLoader.setUnsafe(imageLoaderHolder.get())
            },
        )
    }

    /**
     * 未登录流程中的导航目标。
     */
    sealed interface NavTarget : Parcelable {
        @Parcelize
        data object Root : NavTarget
    }

    /**
     * 解析并创建当前导航目标对应的子节点。
     */
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.Root -> {
                val callback = object : LoginEntryPoint.Callback {
                    override fun navigateToBugReport() {
                        callback.navigateToBugReport()
                    }

                    override fun onDone() {
                        callback.onDone()
                    }
                }
                loginEntryPoint.createNode(
                    parentNode = this,
                    buildContext = buildContext,
                    params = LoginEntryPoint.Params(
                        accountProvider = inputs.loginParams?.accountProvider,
                        loginHint = inputs.loginParams?.loginHint,
                    ),
                    callback = callback,
                )
            }
        }
    }

    @Composable
    /**
     * 渲染未登录流程的 back stack。
     */
    override fun View(modifier: Modifier) {
        // The login flow doesn't support landscape mode on mobile devices yet
        ForceOrientationInMobileDevices(orientation = ScreenOrientation.PORTRAIT)

        BackstackView()
    }
}
