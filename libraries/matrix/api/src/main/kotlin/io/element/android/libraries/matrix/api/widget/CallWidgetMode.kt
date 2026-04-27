/*
 * Copyright (c) 2026 Element Creations Ltd.
 * Copyright 2026 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.widget

/**
 * Element Call 小组件的启动媒体模式。
 *
 * Android 侧需要把用户点击的“语音/视频”意图一路传到 widget URL 生成逻辑，
 * 避免只在本地 UI 上切换图标而没有真正影响对端来电语义。
 */
enum class CallWidgetMode {
    Audio,
    Video,
}
