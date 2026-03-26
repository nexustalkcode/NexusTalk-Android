/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.reset.root

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * 重置身份根页面状态提供器
 *
 * 用于 Compose Preview 的状态提供器，提供不同状态的 [ResetIdentityRootState] 示例。
 */
class ResetIdentityRootStateProvider : PreviewParameterProvider<ResetIdentityRootState> {
    /** 预览状态序列 */
    override val values: Sequence<ResetIdentityRootState>
        get() = sequenceOf(
            ResetIdentityRootState(
                displayConfirmationDialog = false,
                eventSink = {}
            ),
            ResetIdentityRootState(
                displayConfirmationDialog = true,
                eventSink = {}
            )
        )
}
