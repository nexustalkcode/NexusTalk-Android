/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model

import io.element.android.libraries.designsystem.components.avatar.AvatarData
import kotlinx.collections.immutable.ImmutableList

/**
 * 时间线项目已读回执数据类
 *
 * 包含一个事件的所有已读回执信息。
 *
 * @property receipts 已读回执数据列表
 */
data class TimelineItemReadReceipts(
    val receipts: ImmutableList<ReadReceiptData>,
)

/**
 * 已读回执数据类
 *
 * 表示单个已读回执的信息。
 *
 * @property avatarData 用户头像数据
 * @property formattedDate 格式化的日期字符串
 */
data class ReadReceiptData(
    val avatarData: AvatarData,
    val formattedDate: String,
)
