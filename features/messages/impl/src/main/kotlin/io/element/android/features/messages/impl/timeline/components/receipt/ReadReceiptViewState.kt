/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components.receipt

import io.element.android.features.messages.impl.timeline.model.ReadReceiptData
import io.element.android.libraries.matrix.api.timeline.item.event.LocalEventSendState
import kotlinx.collections.immutable.ImmutableList

/**
 * 已读回执视图状态数据类
 *
 * @property sendState 消息发送状态
 * @property isLastOutgoingMessage 是否为最后一条发出的消息
 * @property receipts 已读回执数据列表
 */
data class ReadReceiptViewState(
    val sendState: LocalEventSendState?,
    val isLastOutgoingMessage: Boolean,
    val receipts: ImmutableList<ReadReceiptData>,
)
