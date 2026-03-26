/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.api

/**
 * 公告类型枚举
 *
 * 定义了应用内公告的不同类型，用于标识和区分各种公告场景。
 *
 * @property Space 空间公告 - 用于向用户介绍新的空间功能
 * @property NewNotificationSound 新通知声音公告 - 用于通知用户新的通知声音设置
 */
enum class Announcement {
    /** 空间公告 */
    Space,
    /** 新通知声音公告 */
    NewNotificationSound,
}
