/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.api.pollcontent

import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.poll.PollKind
import kotlinx.collections.immutable.ImmutableList

/**
 * 投票内容状态数据类
 *
 * 表示投票在时间线中显示的完整 UI 状态。
 * 包含投票的所有信息：问题、选项、类型、状态等。
 *
 * @property eventId 投票事件 ID
 * @property question 投票问题
 * @property answerItems 投票选项列表
 * @property pollKind 投票类型（公开或匿名）
 * @property isPollEditable 投票是否可编辑
 * @property isPollEnded 投票是否已结束
 * @property isMine 投票是否由当前用户创建
 */
data class PollContentState(
    val eventId: EventId?,
    val question: String,
    val answerItems: ImmutableList<PollAnswerItem>,
    val pollKind: PollKind,
    val isPollEditable: Boolean,
    val isPollEnded: Boolean,
    val isMine: Boolean,
)
