/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.sendfailure

import dev.zacsweers.metro.Inject
import io.element.android.libraries.matrix.api.room.BaseRoom
import io.element.android.libraries.matrix.api.timeline.item.event.LocalEventSendState

/**
 * 已验证用户发送失败工厂类
 *
 * 负责根据消息发送失败的状态信息创建相应的 [VerifiedUserSendFailure] 对象。
 * 用于将 Matrix SDK 中的发送失败状态转换为应用层可理解的状态类型。
 *
 * 功能说明：
 * - 检测发送失败的具体原因（未验证设备或身份变更）
 * - 获取相关用户的显示名称
 * - 生成适合 UI 显示的状态对象
 *
 * 支持的失败类型：
 * - [LocalEventSendState.Failed.VerifiedUserHasUnsignedDevice]: 已验证用户有未验证设备
 * - [LocalEventSendState.Failed.VerifiedUserChangedIdentity]: 已验证用户身份发生变更
 *
 * @property room 房间实例，用于获取用户显示名称
 *
 * @see VerifiedUserSendFailure 已验证用户发送失败状态接口
 */
@Inject
class VerifiedUserSendFailureFactory(
    private val room: BaseRoom,
) {
    /**
     * 根据发送状态创建发送失败对象
     *
     * 此方法分析传入的发送状态，将其转换为应用层可理解的 [VerifiedUserSendFailure] 对象。
     * 如果无法识别失败类型或没有相关用户信息，将返回 [VerifiedUserSendFailure.None]。
     *
     * 处理逻辑：
     * 1. 检查是否是 VerifiedUserHasUnsignedDevice 失败
     *    - 如果是当前用户的设备，返回 [VerifiedUserSendFailure.UnsignedDevice.FromYou]
     *    - 如果是其他用户的设备，返回 [VerifiedUserSendFailure.UnsignedDevice.FromOther]
     * 2. 检查是否是 VerifiedUserChangedIdentity 失败
     *    - 返回 [VerifiedUserSendFailure.ChangedIdentity]
     * 3. 其他情况返回 [VerifiedUserSendFailure.None]
     *
     * @param sendState 本地事件发送状态，可能为 null
     * @return [VerifiedUserSendFailure] 表示发送失败的具体类型
     */
    suspend fun create(
        sendState: LocalEventSendState?,
    ): VerifiedUserSendFailure {
        return when (sendState) {
            is LocalEventSendState.Failed.VerifiedUserHasUnsignedDevice -> {
                val userId = sendState.devices.keys.firstOrNull()
                if (userId == null) {
                    VerifiedUserSendFailure.None
                } else {
                    if (userId == room.sessionId) {
                        VerifiedUserSendFailure.UnsignedDevice.FromYou
                    } else {
                        val displayName = room.userDisplayName(userId).getOrNull() ?: userId.value
                        VerifiedUserSendFailure.UnsignedDevice.FromOther(displayName)
                    }
                }
            }
            is LocalEventSendState.Failed.VerifiedUserChangedIdentity -> {
                val userId = sendState.users.firstOrNull()
                if (userId == null) {
                    VerifiedUserSendFailure.None
                } else {
                    val displayName = room.userDisplayName(userId).getOrNull() ?: userId.value
                    VerifiedUserSendFailure.ChangedIdentity(displayName)
                }
            }
            else -> VerifiedUserSendFailure.None
        }
    }
}
