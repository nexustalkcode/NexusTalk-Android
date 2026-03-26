/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.utils

/**
 * 画中画控制器接口
 *
 * 定义了画中画模式的控制操作接口。
 * 实现此接口的类负责与 Element Call WebView 进行交互，控制画中画模式。
 *
 * @see WebViewPipController WebView 画中画控制器实现
 */
interface PipController {
    /**
     * 检查是否可以进入画中画模式
     *
     * 需要 Element Call WebView 内部状态允许进入画中画。
     *
     * @return Boolean 如果可以进入画中画则返回 true
     */
    suspend fun canEnterPip(): Boolean

    /**
     * 进入画中画模式
     *
     * 通知 Element Call WebView 启用画中画模式。
     */
    fun enterPip()

    /**
     * 退出画中画模式
     *
     * 通知 Element Call WebView 禁用画中画模式。
     */
    fun exitPip()
}
