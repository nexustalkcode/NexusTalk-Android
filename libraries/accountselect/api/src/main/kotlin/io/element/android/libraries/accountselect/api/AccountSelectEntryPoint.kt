/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.accountselect.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.matrix.api.core.SessionId

/**
 * 账号选择页入口接口。
 */
interface AccountSelectEntryPoint : FeatureEntryPoint {
    /**
     * 创建账号选择页节点。
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: Callback,
    ): Node

    /**
     * 账号选择页回调。
     */
    interface Callback : Plugin {
        fun onAccountSelected(sessionId: SessionId)
        fun onCancel()
    }
}
