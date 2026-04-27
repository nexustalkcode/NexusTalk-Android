/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model.event

import io.element.android.libraries.matrix.api.timeline.item.event.UnableToDecryptContent

/**
 * 无法解密消息的 UI 内容模型。
 */
data class TimelineItemEncryptedContent(
    val data: UnableToDecryptContent.Data
) : TimelineItemEventContent {
    override val type: String = "TimelineItemEncryptedContent"
}
