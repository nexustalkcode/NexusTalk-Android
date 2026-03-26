/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * 锁屏配置 (Lock Screen Configuration)
 *
 * 此对象包含应用程序锁屏和安全功能相关的配置项。
 * 包括PIN码要求、生物识别认证、锁定宽限期等安全设置。
 */
object LockScreenConfig {
    /** 是否强制要求设置PIN码。true表示用户必须设置PIN码才能使用应用，false表示PIN码为可选 */
    const val IS_PIN_MANDATORY: Boolean = false

    /** 禁止使用的PIN码集合。这些PIN码由于太简单而被禁用，用户不能使用它们作为锁屏密码 */
    val FORBIDDEN_PIN_CODES: Set<String> = setOf("0000", "1234")

    /** PIN码的长度。当前设置为4位数字 */
    const val PIN_SIZE: Int = 4

    /** 用户被强制登出前的最大PIN码尝试次数。超过此次数后，用户将被自动登出以保护账户安全 */
    const val MAX_PIN_CODE_ATTEMPTS_BEFORE_LOGOUT: Int = 3

    /** 应用进入后台后自动锁定的时间宽限期。应用在此时间后会自动锁定，需要重新验证才能访问 */
    val GRACE_PERIOD: Duration = 2.minutes

    /** 是否启用强生物识别认证。强方法包括指纹识别和部分面部/虹膜识别实现，安全性较高 */
    const val IS_STRONG_BIOMETRICS_ENABLED: Boolean = true

    /** 是否启用弱生物识别认证。弱方法包括大多数面部识别和部分虹膜识别实现，安全性相对较低 */
    const val IS_WEAK_BIOMETRICS_ENABLED: Boolean = true
}
