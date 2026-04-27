/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * App 内多来电顶层浮层的 UI 状态。
 *
 * 这里故意把状态收敛成纯展示模型，后续无论来自 MainActivity 还是 ElementCallActivity，
 * 都只需要把已经排好序的来电条目交给 View，不让 View 关心业务来源。
 */
data class IncomingCallOverlayState(
    val calls: ImmutableList<IncomingCallOverlayCall> = persistentListOf(),
) {
    val isVisible: Boolean
        get() = calls.isNotEmpty()
}

/**
 * 单条来电展示模型。
 *
 * answer / decline 回调直接挂在行模型上，这样点击哪一条就派发哪一条的动作，
 * 宿主层不需要再通过索引二次查找，避免多来电并发时误操作到别的条目。
 */
data class IncomingCallOverlayCall(
    val id: String,
    val title: String,
    val subtitle: String,
    val avatarData: AvatarData,
    val avatarType: AvatarType = AvatarType.User,
    val onAnswerClick: () -> Unit = {},
    val onDeclineClick: () -> Unit = {},
)
