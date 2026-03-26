/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.crash

/**
 * 崩溃检测事件密封接口
 *
 * 定义了崩溃检测功能可能产生的各种事件，用于状态管理和事件处理。
 */
sealed interface CrashDetectionEvent {
    /**
     * 复制诊断信息
     *
     * 将当前的崩溃诊断信息复制到剪贴板。
     */
    data object CopyDiagnosticInfo : CrashDetectionEvent

    /**
     * 重置所有崩溃数据
     *
     * 清除所有与崩溃相关的数据，包括崩溃信息和崩溃标志。
     */
    data object ResetAllCrashData : CrashDetectionEvent

    /**
     * 重置应用崩溃标志
     *
     * 仅重置应用是否崩溃的标志，保留其他崩溃数据。
     */
    data object ResetAppHasCrashed : CrashDetectionEvent
}
