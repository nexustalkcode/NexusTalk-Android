/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.api.create

import io.element.android.libraries.matrix.api.core.EventId

/**
 * 创建投票模式密封接口
 *
 * 定义了创建投票的两种模式：新建投票或编辑现有投票。
 * 使用密封接口确保模式的安全性。
 */
sealed interface CreatePollMode {
    /** 新建投票模式 - 用于创建全新的投票 */
    data object NewPoll : CreatePollMode

    /**
     * 编辑现有投票模式 - 用于编辑已存在的投票
     *
     * @property eventId 要编辑的投票事件 ID
     */
    data class EditPoll(val eventId: EventId) : CreatePollMode
}
