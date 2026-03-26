/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.rageshake

/**
 * 摇一摇服务接口
 *
 * 定义了摇一摇检测的核心功能接口，用于检测用户摇晃设备。
 */
interface RageShake {
    /**
     * 检查功能是否可用
     *
     * 检测设备是否支持摇一摇功能（需要加速度传感器）。
     *
     * @return Boolean 功能是否可用
     */
    fun isAvailable(): Boolean

    /**
     * 启动摇一摇检测
     *
     * 开始监听设备摇晃动作。
     *
     * @param sensitivity 灵敏度值（0到1之间）
     */
    fun start(sensitivity: Float)

    /**
     * 停止摇一摇检测
     *
     * 停止监听设备摇晃动作。
     */
    fun stop()

    /**
     * 设置灵敏度
     *
     * 灵敏度值可以是 {0, 0.25, 0.5, 0.75, 1} 中的一个，
     * 会被转换为 ShakeDetector 的灵敏度常量
     * (SENSITIVITY_LIGHT=11 到 SENSITIVITY_HARD=15)。
     *
     * @param sensitivity 灵敏度值（0到1之间）
     */
    fun setSensitivity(sensitivity: Float)

    /**
     * 设置摇晃拦截器
     *
     * 设置当检测到摇晃时要执行的回调函数。
     *
     * @param interceptor 拦截器回调函数，传入null可清除拦截器
     */
    fun setInterceptor(interceptor: (() -> Unit)?)
}
