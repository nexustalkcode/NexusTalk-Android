/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 为来电 overlay 预览提供样例状态。
 *
 * 这里覆盖无来电、单条来电、多条并发来电以及大头像等主要展示场景，
 * 方便在 Compose Preview 中校验布局密度和截断表现。
 */
open class IncomingCallOverlayStateProvider : PreviewParameterProvider<IncomingCallOverlayState> {
    override val values: Sequence<IncomingCallOverlayState>
        get() = sequenceOf(
            aIncomingCallOverlayState(),
            aIncomingCallOverlayState(
                calls = persistentListOf(
                    aIncomingCallOverlayCall(
                        id = "call-1",
                        title = "Alice",
                        subtitle = "@alice:matrix.org",
                    ),
                ),
            ),
            aIncomingCallOverlayState(
                calls = persistentListOf(
                    aIncomingCallOverlayCall(
                        id = "call-1",
                        title = "Alice",
                        subtitle = "@alice:matrix.org",
                    ),
                    aIncomingCallOverlayCall(
                        id = "call-2",
                        title = "Design Sync",
                        subtitle = "!design-sync:matrix.org",
                        avatarType = AvatarType.Room(isTombstoned = false),
                    ),
                    aIncomingCallOverlayCall(
                        id = "call-3",
                        title = "Bob",
                        subtitle = "@bob:matrix.org",
                    ),
                ),
            ),
            aIncomingCallOverlayState(
                calls = persistentListOf(
                    aIncomingCallOverlayCall(
                        id = "call-large-avatar",
                        title = "qwr",
                        subtitle = "@qwr:matrix.org",
                        avatarSize = AvatarSize.RoomDetailsHeader,
                    ),
                ),
            ),
        )
}

/**
 * 构造一份 overlay 状态样例。
 *
 * @param calls 需要在 overlay 中展示的来电条目列表。
 */
internal fun aIncomingCallOverlayState(
    calls: ImmutableList<IncomingCallOverlayCall> = persistentListOf(),
): IncomingCallOverlayState {
    return IncomingCallOverlayState(calls = calls)
}

/**
 * 构造一条 overlay 来电条目样例。
 *
 * @param id 来电条目的稳定标识。
 * @param title 主标题。
 * @param subtitle 副标题。
 * @param avatarType 头像语义类型。
 * @param avatarSize 头像尺寸。
 * @param onAnswerClick 点击接听时触发的回调。
 * @param onDeclineClick 点击拒绝时触发的回调。
 */
internal fun aIncomingCallOverlayCall(
    id: String,
    title: String,
    subtitle: String,
    avatarType: AvatarType = AvatarType.User,
    avatarSize: AvatarSize = AvatarSize.UserListItem,
    onAnswerClick: () -> Unit = {},
    onDeclineClick: () -> Unit = {},
): IncomingCallOverlayCall {
    return IncomingCallOverlayCall(
        id = id,
        title = title,
        subtitle = subtitle,
        avatarData = AvatarData(
            id = id,
            name = title,
            url = null,
            size = avatarSize,
        ),
        avatarType = avatarType,
        onAnswerClick = onAnswerClick,
        onDeclineClick = onDeclineClick,
    )
}
