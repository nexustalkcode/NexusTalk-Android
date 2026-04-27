/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.signedout.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.element.android.libraries.architecture.FeatureEntryPoint
import io.element.android.libraries.matrix.api.core.SessionId

/**
 * 已登出页面入口接口。
 */
interface SignedOutEntryPoint : FeatureEntryPoint {
    /**
     * 已登出页面所需参数。
     *
     * @property sessionId 被标记为已登出的会话 ID。
     */
    data class Params(
        val sessionId: SessionId,
    )

    /**
     * 创建已登出页面节点。
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: Params,
    ): Node
}
