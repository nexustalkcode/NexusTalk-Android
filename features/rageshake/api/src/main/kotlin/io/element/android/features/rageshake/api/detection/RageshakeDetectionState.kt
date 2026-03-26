/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.detection

import io.element.android.features.rageshake.api.preferences.RageshakePreferencesState

/**
 * 摇一摇检测状态数据类
 *
 * 表示摇一摇检测功能的当前状态，用于检测用户摇动设备来报告问题。
 *
 * @property takeScreenshot 是否需要截图
 * @property showDialog 是否显示对话框
 * @property isStarted 检测是否已启动
 * @property preferenceState 偏好设置状态
 * @property eventSink 事件处理函数
 */
data class RageshakeDetectionState(
    val takeScreenshot: Boolean,
    val showDialog: Boolean,
    val isStarted: Boolean,
    val preferenceState: RageshakePreferencesState,
    val eventSink: (RageshakeDetectionEvent) -> Unit
)
