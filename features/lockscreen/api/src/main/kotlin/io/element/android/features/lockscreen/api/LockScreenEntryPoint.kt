/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.api

import android.content.Context
import android.content.Intent
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint

/**
 * 锁屏功能入口点接口
 *
 * 定义了锁屏模块的入口点，用于创建锁屏相关的节点和意图。
 * 实现此接口可以进入锁屏设置、设置流程或解锁页面。
 */
interface LockScreenEntryPoint : FeatureEntryPoint {
    /**
     * 创建锁屏节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param navTarget 导航目标（设置或设置流程）
     * @param callback 回调接口，用于通知设置完成
     * @return 创建的节点
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        navTarget: Target,
        callback: Callback,
    ): Node

    /**
     * 获取 PIN 解锁意图
     *
     * @param context Android 上下文
     * @return 用于启动 PIN 解锁 Activity 的意图
     */
    fun pinUnlockIntent(context: Context): Intent

    /**
     * 锁屏入口点回调接口
     *
     * 用于通知锁屏设置完成的回调。
     */
    interface Callback : Plugin {
        /**
         * 当设置完成时调用
         */
        fun onSetupDone()
    }

    /**
     * 锁屏导航目标枚举
     *
     * 定义了锁屏模块的导航目标：
     * - Settings: 锁屏设置页面
     * - Setup: 锁屏设置流程（首次设置 PIN 码）
     */
    enum class Target {
        /** 锁屏设置页面 */
        Settings,
        /** 锁屏设置流程 */
        Setup,
    }
}
