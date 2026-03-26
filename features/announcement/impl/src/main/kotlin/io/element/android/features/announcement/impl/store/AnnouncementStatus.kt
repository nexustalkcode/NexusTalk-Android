/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.impl.store

/**
 * 公告状态枚举
 *
 * 定义了公告的生命周期状态，用于跟踪公告的显示情况。
 * 状态流转：NeverShown -> Show -> Shown
 *
 * @property NeverShown 从未显示 - 公告尚未向用户展示过
 * @property Show 显示中 - 公告当前正在向用户展示
 * @property Shown 已显示 - 公告已经向用户展示过（已关闭）
 * @see AnnouncementStore 公告存储接口
 * @see DefaultAnnouncementStore 默认公告存储实现
 */
enum class AnnouncementStatus {
    /** 从未显示 - 公告尚未向用户展示过 */
    NeverShown,
    /** 显示中 - 公告当前正在向用户展示 */
    Show,
    /** 已显示 - 公告已经向用户展示过（已关闭） */
    Shown,
}
