/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.protection

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * 为受保护媒体宽高比预览提供样例数据。
 */
class AspectRatioProvider : PreviewParameterProvider<Float?> {
    override val values: Sequence<Float?> = sequenceOf(
        null,
        0.05f,
        1f,
        20f,
    )
}
