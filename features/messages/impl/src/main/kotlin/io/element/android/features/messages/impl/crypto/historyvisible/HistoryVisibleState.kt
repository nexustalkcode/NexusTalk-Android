/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.historyvisible

/**
 * 历史可见性状态数据类
 *
 * @property showAlert 是否显示警告
 * @property eventSink 事件处理函数
 */
data class HistoryVisibleState(
    val showAlert: Boolean,
    val eventSink: (HistoryVisibleEvent) -> Unit,
)
