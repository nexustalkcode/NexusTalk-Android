/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components.receipt.bottomsheet

import io.element.android.features.messages.impl.timeline.model.TimelineItem

/**
 * 已读回执底部弹窗可能触发的事件。
 */
sealed interface ReadReceiptBottomSheetEvents {
    /** 选中某条时间线事件并展示其已读回执。 */
    data class EventSelected(val event: TimelineItem.Event) : ReadReceiptBottomSheetEvents
    /** 关闭当前底部弹窗。 */
    data object Dismiss : ReadReceiptBottomSheetEvents
}
