/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import kotlin.time.Duration
import io.element.android.appconfig.LockScreenConfig as AppConfigLockScreenConfig

/**
 * 锁屏配置数据类
 *
 * 定义锁屏功能的各种配置参数。
 *
 * @param isPinMandatory 是否强制要求设置 PIN 码
 * @param forbiddenPinCodes 禁止使用的 PIN 码集合
 * @param pinSize PIN 码长度
 * @param maxPinCodeAttemptsBeforeLogout 登出前的最大尝试次数
 * @param gracePeriod 进入后台后的宽限期（在此期间不会自动锁定）
 * @param isStrongBiometricsEnabled 是否启用强生物识别
 * @param isWeakBiometricsEnabled 是否启用弱生物识别
 */
data class LockScreenConfig(
    val isPinMandatory: Boolean,
    val forbiddenPinCodes: Set<String>,
    val pinSize: Int,
    val maxPinCodeAttemptsBeforeLogout: Int,
    val gracePeriod: Duration,
    val isStrongBiometricsEnabled: Boolean,
    val isWeakBiometricsEnabled: Boolean,
)

/**
 * 锁屏配置模块
 *
 * 提供锁屏配置的依赖注入绑定。
 */
@ContributesTo(AppScope::class)
@BindingContainer
object LockScreenConfigModule {
    /**
     * 提供锁屏配置实例
     */
    @Provides
    fun providesLockScreenConfig(): LockScreenConfig = LockScreenConfig(
        isPinMandatory = AppConfigLockScreenConfig.IS_PIN_MANDATORY,
        forbiddenPinCodes = AppConfigLockScreenConfig.FORBIDDEN_PIN_CODES,
        pinSize = AppConfigLockScreenConfig.PIN_SIZE,
        maxPinCodeAttemptsBeforeLogout = AppConfigLockScreenConfig.MAX_PIN_CODE_ATTEMPTS_BEFORE_LOGOUT,
        gracePeriod = AppConfigLockScreenConfig.GRACE_PERIOD,
        isStrongBiometricsEnabled = AppConfigLockScreenConfig.IS_STRONG_BIOMETRICS_ENABLED,
        isWeakBiometricsEnabled = AppConfigLockScreenConfig.IS_WEAK_BIOMETRICS_ENABLED,
    )
}
