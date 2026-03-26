/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model

/**
 * 新事件状态枚举
 *
 * 表示时间线中是否有新事件，以及事件是否来自当前用户或其他用户。
 * 可用于在添加新事件时滚动到列表底部。
 */
enum class NewEventState {
    None,
    FromMe,
    FromOther
}
