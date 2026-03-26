/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.impl.spaces

/**
 * 空间公告状态数据类
 *
 * 表示空间公告界面的当前状态，用于在 UI 层展示空间公告信息。
 * 该状态类封装了处理用户交互所需的事件处理函数。
 *
 * @property eventSink 事件处理函数，用于传递用户交互事件到 Presenter
 * @see SpaceAnnouncementPresenter 用于生成此状态
 * @see SpaceAnnouncementView 用于渲染此状态对应的 UI
 * @see SpaceAnnouncementEvents 空间公告事件 sealed 接口
 */
data class SpaceAnnouncementState(
    /** 事件处理函数，用于传递用户交互事件到 Presenter */
    val eventSink: (SpaceAnnouncementEvents) -> Unit
)
