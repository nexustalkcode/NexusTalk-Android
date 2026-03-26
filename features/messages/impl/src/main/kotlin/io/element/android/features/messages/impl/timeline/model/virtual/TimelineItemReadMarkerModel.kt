/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model.virtual

/**
 * 阅读标记虚拟项目模型
 *
 * 用于在时间线中显示已读标记，标识用户已阅读到的位置。
 */
data object TimelineItemReadMarkerModel : TimelineItemVirtualModel {
    override val type: String = "TimelineItemReadMarkerModel"
}
