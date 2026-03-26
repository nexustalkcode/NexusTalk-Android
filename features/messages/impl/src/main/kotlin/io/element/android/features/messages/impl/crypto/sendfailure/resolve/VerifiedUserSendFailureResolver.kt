/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.sendfailure.resolve

import androidx.compose.runtime.mutableStateOf
import io.element.android.libraries.matrix.api.core.SendHandle
import io.element.android.libraries.matrix.api.core.TransactionId
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.timeline.item.event.LocalEventSendState
import timber.log.Timber

/**
 * 已验证用户发送失败解决器
 *
 * 此类负责解决并重新发送发送给已验证用户但失败的消息。
 * 它还允许在不解决失败的情况下重新发送消息，例如用户在此期间已再次验证其设备。
 *
 * 功能说明：
 * - 使用 [VerifiedUserSendFailureIterator] 遍历不同的失败情况（即涉及的不同用户）
 * - 支持逐个解决每个用户的失败并重发消息
 * - 提供两种重发方式：直接重发和解决后重发
 *
 * 工作流程：
 * 1. 初始化时从迭代器获取第一个失败对象
 * 2. 用户选择"解决并重发"时，执行加密操作然后重发
 * 3. 如果有更多失败对象，继续处理下一个
 * 4. 用户选择"直接重发"时，不解决失败直接尝试重发
 *
 * @property room 已加入的房间实例，用于执行加密操作和重发消息
 * @property transactionId 消息的事务ID，用于标识待重发的消息
 * @property sendHandle 发送句柄，用于执行重发操作
 * @property iterator 失败迭代器，用于遍历多个用户的失败情况
 *
 * @see VerifiedUserSendFailureIterator 失败迭代器接口
 * @see io.element.android.libraries.matrix.api.room.JoinedRoom 房间接口
 */
class VerifiedUserSendFailureResolver(
    private val room: JoinedRoom,
    private val transactionId: TransactionId,
    private val sendHandle: SendHandle,
    private val iterator: VerifiedUserSendFailureIterator,
) {
    /**
     * 当前失败状态
     *
     * 使用 MutableState 包装，允许 UI 响应失败状态的变化。
     * 初始值为 null，当迭代器有内容时会设置为第一个失败对象。
     */
    val currentSendFailure = mutableStateOf<LocalEventSendState.Failed.VerifiedUser?>(null)

    /**
     * 初始化
     *
     * 构造时尝试从迭代器获取第一个失败对象。
     * 如果迭代器为空，currentSendFailure 保持为 null。
     */
    init {
        if (iterator.hasNext()) {
            currentSendFailure.value = iterator.next()
        }
    }

    /**
     * 直接重试发送消息
     *
     * 不解决失败原因，直接尝试重新发送消息。
     * 适用于用户认为失败是暂时性的，或者已经在其他途径解决了问题。
     *
     * @return [Result] 表示操作结果
     * - Success: 消息重发成功
     * - Failure: 消息重发失败，包含错误信息
     */
    suspend fun resend(): Result<Unit> {
        return sendHandle.retry()
            .onSuccess {
                Timber.d("Succeed to resend message with transactionId: $transactionId")
                currentSendFailure.value = null
            }
            .onFailure {
                Timber.e(it, "Failed to resend message with transactionId: $transactionId")
            }
    }

    /**
     * 解决失败并重新发送消息
     *
     * 根据当前失败类型执行相应的解决操作，然后尝试重新发送消息。
     *
     * 失败类型及对应操作：
     * - [LocalEventSendState.Failed.VerifiedUserHasUnsignedDevice]:
     *   调用 [io.element.android.libraries.matrix.api.room.JoinedRoom.ignoreDeviceTrustAndResend]
     *   忽略设备信任并重发
     * - [LocalEventSendState.Failed.VerifiedUserChangedIdentity]:
     *   调用 [io.element.android.libraries.matrix.api.room.JoinedRoom.withdrawVerificationAndResend]
     *   撤回验证并重发
     *
     * 解决流程：
     * 1. 执行对应的加密操作
     * 2. 尝试重发消息
     * 3. 如果成功且迭代器还有更多失败，继续处理下一个
     * 4. 如果成功且没有更多失败，清除失败状态
     *
     * @return [Result] 表示操作结果
     * - Success: 解决并重发成功
     * - Failure: 解决或重发失败，包含错误信息
     */
    suspend fun resolveAndResend(): Result<Unit> {
        return when (val failure = currentSendFailure.value) {
            is LocalEventSendState.Failed.VerifiedUserHasUnsignedDevice -> {
                room.ignoreDeviceTrustAndResend(failure.devices, sendHandle)
            }
            is LocalEventSendState.Failed.VerifiedUserChangedIdentity -> {
                room.withdrawVerificationAndResend(failure.users, sendHandle)
            }
            else -> {
                Result.failure(IllegalStateException("Unknown send failure type"))
            }
        }.onSuccess {
            Timber.d("Succeed to resolve and resend message with transactionId: $transactionId")
            if (iterator.hasNext()) {
                val failure = iterator.next()
                currentSendFailure.value = failure
            } else {
                currentSendFailure.value = null
                Timber.d("No more failure to resolve for transactionId: $transactionId")
            }
        }.onFailure {
            Timber.e(it, "Failed to resolve and resend message with transactionId: $transactionId")
        }
    }
}
