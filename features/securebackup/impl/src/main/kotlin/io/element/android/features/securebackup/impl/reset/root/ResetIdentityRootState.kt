/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.reset.root

/**
 * 重置身份根页面状态数据类
 *
 * @property displayConfirmationDialog 是否显示确认对话框
 * @property eventSink 事件处理函数
 */
data class ResetIdentityRootState(
    /** 是否显示确认对话框 */
    val displayConfirmationDialog: Boolean,
    /** 事件处理函数 */
    val eventSink: (ResetIdentityRootEvent) -> Unit,
)
