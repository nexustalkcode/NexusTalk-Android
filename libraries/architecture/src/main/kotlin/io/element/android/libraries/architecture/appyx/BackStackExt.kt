/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.architecture.appyx

import com.bumble.appyx.navmodel.backstack.BackStack

/**
 * 判断当前 BackStack 是否存在可返回的 stashed 元素。
 */
fun <T : Any> BackStack<T>.canPop(): Boolean {
    val elements = elements.value
    return elements.any { it.targetState == BackStack.State.ACTIVE } &&
        elements.any { it.targetState == BackStack.State.STASHED }
}
