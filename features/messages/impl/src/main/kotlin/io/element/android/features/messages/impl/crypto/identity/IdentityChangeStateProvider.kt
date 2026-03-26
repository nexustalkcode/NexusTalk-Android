/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.identity

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.encryption.identity.IdentityState
import io.element.android.libraries.matrix.ui.room.IdentityRoomMember
import io.element.android.libraries.matrix.ui.room.RoomMemberIdentityStateChange
import kotlinx.collections.immutable.toImmutableList

/**
 * 身份变更状态预览参数提供者
 *
 * 继承自 [PreviewParameterProvider]，用于在预览环境中提供 [IdentityChangeState] 的示例数据。
 * 主要用于 Android Studio 的 Compose 预览功能，帮助开发者快速查看 UI 在不同身份变更状态下的渲染效果。
 *
 * 提供三种预览状态：
 * 1. 无身份变更（空列表）
 * 2. PIN违规状态（用户密钥变更）
 * 3. 验证违规状态（验证被撤销）
 *
 * @see IdentityChangeState 身份变更状态数据类
 * @see IdentityChangeStateView 身份变更状态视图
 */
class IdentityChangeStateProvider : PreviewParameterProvider<IdentityChangeState> {
    /**
     * 提供预览状态序列
     *
     * 返回包含三种不同身份变更状态的序列：
     * - 默认空状态（无身份变更）
     * - PIN违规状态
     * - 验证违规状态
     *
     * @return 包含不同 [IdentityChangeState] 示例的序列
     */
    override val values: Sequence<IdentityChangeState>
        get() = sequenceOf(
            anIdentityChangeState(),
            anIdentityChangeState(
                roomMemberIdentityStateChanges = listOf(
                    aRoomMemberIdentityStateChange(
                        identityRoomMember = anIdentityRoomMember(),
                        identityState = IdentityState.PinViolation,
                    ),
                ),
            ),
            anIdentityChangeState(
                roomMemberIdentityStateChanges = listOf(
                    aRoomMemberIdentityStateChange(
                        identityRoomMember = anIdentityRoomMember(displayNameOrDefault = "Alice"),
                        identityState = IdentityState.VerificationViolation,
                    ),
                ),
            ),
        )
}

/**
 * 创建房间成员身份状态变更测试数据
 *
 * 用于在测试和预览中快速创建 [RoomMemberIdentityStateChange] 实例。
 *
 * @param identityRoomMember 身份房间成员信息
 * @param identityState 身份状态（违规类型）
 * @return [RoomMemberIdentityStateChange] 实例
 */
internal fun aRoomMemberIdentityStateChange(
    identityRoomMember: IdentityRoomMember = anIdentityRoomMember(),
    identityState: IdentityState = IdentityState.PinViolation,
) = RoomMemberIdentityStateChange(
    identityRoomMember = identityRoomMember,
    identityState = identityState,
)

/**
 * 创建身份变更状态测试数据
 *
 * 用于在测试和预览中快速创建 [IdentityChangeState] 实例。
 *
 * @param roomMemberIdentityStateChanges 房间成员身份状态变更列表
 * @param eventSink 事件处理函数
 * @return [IdentityChangeState] 实例
 */
internal fun anIdentityChangeState(
    roomMemberIdentityStateChanges: List<RoomMemberIdentityStateChange> = emptyList(),
    eventSink: (IdentityChangeEvent) -> Unit = {},
) = IdentityChangeState(
    roomMemberIdentityStateChanges = roomMemberIdentityStateChanges.toImmutableList(),
    eventSink = eventSink,
)

/**
 * 创建身份房间成员测试数据
 *
 * 用于在测试和预览中快速创建 [IdentityRoomMember] 实例。
 *
 * @param userId 用户ID
 * @param displayNameOrDefault 显示名称，如果为空则使用用户ID的显示名部分
 * @param avatarData 头像数据
 * @return [IdentityRoomMember] 实例
 */
internal fun anIdentityRoomMember(
    userId: UserId = UserId("@alice:example.com"),
    displayNameOrDefault: String = userId.extractedDisplayName,
    avatarData: AvatarData = AvatarData(
        id = userId.value,
        name = null,
        url = null,
        size = AvatarSize.ComposerAlert,
    ),
) = IdentityRoomMember(
    userId = userId,
    displayNameOrDefault = displayNameOrDefault,
    avatarData = avatarData,
)
