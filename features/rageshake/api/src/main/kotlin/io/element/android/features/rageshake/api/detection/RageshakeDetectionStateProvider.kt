/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.detection

import io.element.android.features.rageshake.api.preferences.aRageshakePreferencesState

/**
 * 创建默认摇一摇检测状态的辅助函数
 *
 * 用于预览和测试，创建一个默认的 RageshakeDetectionState 实例。
 *
 * @return RageshakeDetectionState 默认状态的实例
 */
fun aRageshakeDetectionState() = RageshakeDetectionState(
    takeScreenshot = false,
    showDialog = false,
    isStarted = false,
    preferenceState = aRageshakePreferencesState(),
    eventSink = {}
)
