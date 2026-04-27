/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.number

/**
 * 输入校验码页面可能触发的用户事件。
 */
sealed interface EnterNumberEvent {
    /** 更新当前输入的数字串。 */
    data class UpdateNumber(val number: String) : EnterNumberEvent
    /** 继续提交当前输入的校验码。 */
    data object Continue : EnterNumberEvent
}
