/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.model

/**
 * 房间摘要显示类型枚举
 *
 * 表示房间列表项的显示类型，用于确定如何渲染房间列表中的各个项。
 */
enum class RoomSummaryDisplayType {
    /** 占位符（加载中） */
    PLACEHOLDER,
    /** 普通房间 */
    ROOM,
    /** 邀请 */
    INVITE,
    /** 敲击请求（等待批准加入） */
    KNOCKED,
}
