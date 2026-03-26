/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.crash

import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage

/**
 * 崩溃检测状态数据类
 *
 * 表示崩溃检测功能的当前状态，用于检测应用是否发生了崩溃。
 *
 * @property appName 应用名称
 * @property crashDetected 是否检测到崩溃
 * @property snackbarMessage 需要展示的提示消息
 * @property eventSink 事件处理函数
 */
data class CrashDetectionState(
    val appName: String,
    val crashDetected: Boolean,
    val snackbarMessage: SnackbarMessage? = null,
    val eventSink: (CrashDetectionEvent) -> Unit
)
