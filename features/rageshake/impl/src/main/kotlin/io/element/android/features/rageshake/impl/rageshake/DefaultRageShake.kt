/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.rageshake

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import com.squareup.seismic.ShakeDetector
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import io.element.android.libraries.di.annotations.ApplicationContext

/**
 * 默认摇一摇服务实现
 *
 * RageShake 接口的默认实现，使用传感器检测设备摇晃。
 * 使用 Square 公司的 ShakeDetector 库来检测摇晃动作。
 *
 * @property context 应用上下文
 */
@SingleIn(AppScope::class)
@ContributesBinding(scope = AppScope::class, binding = binding<RageShake>())
class DefaultRageShake(
    @ApplicationContext context: Context,
) : ShakeDetector.Listener, RageShake {
    /**
     * 传感器管理器
     *
     * 用于访问设备的加速度传感器。
     */
    private var sensorManager = context.getSystemService<SensorManager>()

    /**
     * 摇晃检测器
     *
     * Square 公司的 ShakeDetector 实例，用于检测摇晃动作。
     */
    private var shakeDetector: ShakeDetector? = null

    /**
     * 摇晃拦截器
     *
     * 当检测到摇晃时要执行的回调函数。
     */
    private var interceptor: (() -> Unit)? = null

    /**
     * 设置摇晃拦截器
     *
     * @param interceptor 拦截器回调函数
     */
    override fun setInterceptor(interceptor: (() -> Unit)?) {
        this.interceptor = interceptor
    }

    /**
     * 检查功能是否可用
     *
     * 检测设备是否具备加速度传感器。
     *
     * @return Boolean 是否可用
     */
    override fun isAvailable(): Boolean {
        return sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
    }

    /**
     * 启动摇一摇检测
     *
     * 初始化摇晃检测器并开始监听摇晃动作。
     *
     * @param sensitivity 灵敏度值
     */
    override fun start(sensitivity: Float) {
        sensorManager?.let {
            shakeDetector = ShakeDetector(this).apply {
                start(it, SensorManager.SENSOR_DELAY_GAME)
            }
            setSensitivity(sensitivity)
        }
    }

    /**
     * 停止摇一摇检测
     *
     * 停止摇晃检测器。
     */
    override fun stop() {
        shakeDetector?.stop()
    }

    /**
     * 设置灵敏度
     *
     * 灵敏度值范围是 {0, 0.25, 0.5, 0.75, 1}，
     * 会被转换为 ShakeDetector 的灵敏度常量
     * (SENSITIVITY_LIGHT=11 到 SENSITIVITY_HARD=15)。
     *
     * @param sensitivity 灵敏度值
     */
    override fun setSensitivity(sensitivity: Float) {
        shakeDetector?.setSensitivity(
            ShakeDetector.SENSITIVITY_LIGHT + (sensitivity * 4).toInt()
        )
    }

    /**
     * 检测到摇晃
     *
     * ShakeDetector.Listener 接口的实现，
     * 当检测到摇晃时调用拦截器。
     */
    override fun hearShake() {
        interceptor?.invoke()
    }
}
