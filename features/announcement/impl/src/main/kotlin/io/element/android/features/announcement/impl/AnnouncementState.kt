/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.impl

/**
 * 公告状态数据类
 *
 * 表示公告功能的当前状态，用于在 UI 层展示公告相关的信息。
 * 该状态类封装了所有公告相关的显示状态，供 Presenter 和 ViewModel 使用。
 *
 * @property showSpaceAnnouncement 是否显示空间公告 - true 表示需要显示空间公告引导，false 表示不显示
 * @see AnnouncementPresenter 用于生成此状态
 * @see SpaceAnnouncementView 用于渲染此状态对应的 UI
 */
data class AnnouncementState(
    /** 是否显示空间公告 */
    val showSpaceAnnouncement: Boolean,
)

/**
 * 创建公告状态工厂函数
 *
 * 用于在测试或预览时快速创建 AnnouncementState 实例。
 * 提供默认参数方便调用，无需显式指定所有属性。
 *
 * @param showSpaceAnnouncement 是否显示空间公告，默认值为 false
 * @return AnnouncementState 公告状态实例
 * @see AnnouncementState 公告状态数据类
 */
fun anAnnouncementState(
    showSpaceAnnouncement: Boolean = false,
) = AnnouncementState(
    showSpaceAnnouncement = showSpaceAnnouncement,
)
