/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

/**
 * 时间线配置。
 *
 * 这里只保留纯应用配置常量，避免 appconfig 再反向依赖 matrix 类型，
 * 否则在 matrix 单模块收敛后会形成 `appconfig <-> matrix` 的项目级环。
 */
object TimelineConfig {
    /** 消息气泡上最多展示的已读回执头像数量。 */
    const val MAX_READ_RECEIPT_TO_DISPLAY = 3
}
