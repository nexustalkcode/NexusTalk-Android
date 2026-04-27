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
 * 已读回执底部弹窗展示状态。
 *
 * @property selectedEvent 当前选中的时间线事件。
 * @property eventSink 页面事件分发函数。
 */
data class ReadReceiptBottomSheetState(
    val selectedEvent: TimelineItem.Event?,
    val eventSink: (ReadReceiptBottomSheetEvents) -> Unit,
)
