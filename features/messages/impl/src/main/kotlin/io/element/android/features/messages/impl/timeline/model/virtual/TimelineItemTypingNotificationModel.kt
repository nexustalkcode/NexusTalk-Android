/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model.virtual

/**
 * 打字通知虚拟项目模型
 *
 * 用于在时间线中显示"用户正在输入..."的通知。
 */
data object TimelineItemTypingNotificationModel : TimelineItemVirtualModel {
    override val type: String = "TimelineItemTypingNotificationModel"
}
