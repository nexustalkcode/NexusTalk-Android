/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.typing

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.collections.immutable.toImmutableList

/**
 * 打字通知状态提供者
 *
 * 用于预览功能的参数提供者，提供各种打字通知状态的示例。
 * 继承自PreviewParameterProvider，用于Compose预览功能。
 */
class TypingNotificationStateProvider : PreviewParameterProvider<TypingNotificationState> {
    override val values: Sequence<TypingNotificationState>
        get() = sequenceOf(
            aTypingNotificationState(),
            aTypingNotificationState(
                typingMembers = listOf(
                    aTypingRoomMember(),
                ),
            ),
            aTypingNotificationState(
                typingMembers = listOf(
                    aTypingRoomMember(disambiguatedDisplayName = "Alice"),
                ),
            ),
            aTypingNotificationState(
                typingMembers = listOf(
                    aTypingRoomMember(disambiguatedDisplayName = "Alice (@alice:example.com)"),
                ),
            ),
            aTypingNotificationState(
                typingMembers = listOf(
                    aTypingRoomMember(disambiguatedDisplayName = "Alice"),
                    aTypingRoomMember(disambiguatedDisplayName = "Bob"),
                ),
            ),
            aTypingNotificationState(
                typingMembers = listOf(
                    aTypingRoomMember(disambiguatedDisplayName = "Alice"),
                    aTypingRoomMember(disambiguatedDisplayName = "Bob"),
                    aTypingRoomMember(disambiguatedDisplayName = "Charlie"),
                ),
            ),
            aTypingNotificationState(
                typingMembers = listOf(
                    aTypingRoomMember(disambiguatedDisplayName = "Alice"),
                    aTypingRoomMember(disambiguatedDisplayName = "Bob"),
                    aTypingRoomMember(disambiguatedDisplayName = "Charlie"),
                    aTypingRoomMember(disambiguatedDisplayName = "Dan"),
                    aTypingRoomMember(disambiguatedDisplayName = "Eve"),
                ),
            ),
            aTypingNotificationState(
                typingMembers = listOf(
                    aTypingRoomMember(disambiguatedDisplayName = "Alice with a very long display name which means that it will be truncated"),
                ),
            ),
            aTypingNotificationState(
                typingMembers = emptyList(),
                reserveSpace = true,
            ),
        )
}

/**
 * 创建打字通知状态的辅助函数
 *
 * 用于在测试和预览中快速创建TypingNotificationState实例。
 *
 * @param typingMembers 正在打字的成员列表，默认为空列表
 * @param reserveSpace 是否保留空间，默认为false
 * @return 新的TypingNotificationState实例
 */
internal fun aTypingNotificationState(
    typingMembers: List<TypingRoomMember> = emptyList(),
    reserveSpace: Boolean = false,
) = TypingNotificationState(
    renderTypingNotifications = true,
    typingMembers = typingMembers.toImmutableList(),
    reserveSpace = reserveSpace,
)

internal fun aTypingRoomMember(
    disambiguatedDisplayName: String = "@alice:example.com",
) = TypingRoomMember(
    disambiguatedDisplayName = disambiguatedDisplayName,
)
