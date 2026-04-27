/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.architecture.overlay

import com.bumble.appyx.core.navigation.backpresshandlerstrategies.BaseBackPressHandlerStrategy
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.BackStackElements
import io.element.android.libraries.architecture.overlay.operation.Hide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Overlay 返回键处理策略。
 *
 * 当 overlay 中存在元素时，返回键会隐藏当前 overlay。
 */
class HideOverlayBackPressHandler<NavTarget : Any> :
    BaseBackPressHandlerStrategy<NavTarget, BackStack.State>() {
    override val canHandleBackPressFlow: Flow<Boolean> by lazy {
        navModel.elements.map(::areThereElements)
    }

    private fun areThereElements(elements: BackStackElements<NavTarget>) =
        elements.isNotEmpty()

    /**
     * 处理返回键，隐藏当前 overlay。
     */
    override fun onBackPressed() {
        navModel.accept(Hide())
    }
}
