/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.pip

/**
 * 画中画视图接口
 *
 * 定义了画中画模式所需的视图操作接口。
 * 实现此接口的类需要提供画中画参数的设置、进入画中画模式和挂断通话的功能。
 *
 * @see ElementCallActivity 实现类
 */
interface PipView {
    /**
     * 设置画中画参数
     *
     * 配置画中画模式的参数，如宽高比等。
     */
    fun setPipParams()

    /**
     * 进入画中画模式
     *
     * 尝试将应用切换到画中画模式。
     *
     * @return Boolean 如果成功进入画中画模式返回 true，否则返回 false
     */
    fun enterPipMode(): Boolean

    /**
     * 挂断通话
     *
     * 在画中画模式下挂断当前通话。
     */
    fun hangUp()
}
