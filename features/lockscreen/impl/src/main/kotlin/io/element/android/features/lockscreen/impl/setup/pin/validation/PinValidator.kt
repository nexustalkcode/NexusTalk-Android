/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.setup.pin.validation

import dev.zacsweers.metro.Inject
import io.element.android.features.lockscreen.impl.LockScreenConfig
import io.element.android.features.lockscreen.impl.pin.model.PinEntry

/**
 * PIN 码验证器
 *
 * 负责验证 PIN 码是否符合要求。
 */
@Inject
class PinValidator(private val lockScreenConfig: LockScreenConfig) {
    /**
     * 验证结果密封接口
     */
    sealed interface Result {
        /** PIN 码有效 */
        data object Valid : Result
        /** PIN 码无效
         * @param failure 失败原因
         */
        data class Invalid(val failure: SetupPinFailure) : Result
    }

    /**
     * 验证 PIN 码是否有效
     *
     * @param pinEntry PIN 码输入
     * @return 验证结果
     */
    fun isPinValid(pinEntry: PinEntry): Result {
        val pinAsText = pinEntry.toText()
        val isForbidden = lockScreenConfig.forbiddenPinCodes.any { it == pinAsText }
        return if (isForbidden) {
            Result.Invalid(SetupPinFailure.ForbiddenPin)
        } else {
            Result.Valid
        }
    }
}
