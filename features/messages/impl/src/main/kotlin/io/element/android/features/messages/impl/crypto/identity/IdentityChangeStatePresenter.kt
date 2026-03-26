/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.identity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.encryption.EncryptionService
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.ui.room.roomMemberIdentityStateChange
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 身份变更状态展示器
 *
 * 负责管理和呈现房间成员身份变更相关的状态。
 * 监听房间内成员的身份验证状态变化，并向用户展示相应的警告提示。
 *
 * 功能说明：
 * - 监听房间成员的身份状态变化
 * - 收集并处理身份验证违规信息
 * - 提供用户操作接口（固定身份或撤回验证）
 *
 * 身份变更类型：
 * - PinViolation: 用户密钥发生变化，可能存在中间人攻击风险
 * - VerificationViolation: 用户验证状态被撤销
 *
 * @property room 已加入的房间实例，用于获取成员身份状态
 * @property encryptionService 加密服务，用于执行身份固定和验证撤回操作
 *
 * @see IdentityChangeState 身份变更状态
 * @see IdentityChangeEvent 身份变更事件
 * @see io.element.android.libraries.matrix.api.encryption.EncryptionService 加密服务接口
 */
@Inject
class IdentityChangeStatePresenter(
    private val room: JoinedRoom,
    private val encryptionService: EncryptionService,
) : Presenter<IdentityChangeState> {
    /**
     * 呈现身份变更状态
     *
     * 此方法是 Presenter 接口的核心实现，负责：
     * 1. 收集房间成员的身份状态变化信息
     * 2. 处理用户的身份验证操作请求
     * 3. 返回包含身份变更列表和事件处理函数的状态
     *
     * 注意：此方法会等待加密初始化完成后再获取身份状态，
     * 确保能够正确获取完整的身份验证信息。
     *
     * @return [IdentityChangeState] 包含身份变更列表和事件处理函数
     */
    @Composable
    override fun present(): IdentityChangeState {
        val coroutineScope = rememberCoroutineScope()
        // 使用 produceState 收集房间成员身份状态变化
        // waitForEncryption = true 确保加密服务已初始化完成
        val roomMemberIdentityStateChange by produceState(persistentListOf()) {
            room.roomMemberIdentityStateChange(waitForEncryption = true).collect { value = it }
        }

        /**
         * 处理身份变更事件
         *
         * 根据事件类型执行相应的操作：
         * - PinIdentity: 固定用户的加密身份
         * - WithdrawVerification: 撤回对用户的验证
         *
         * @param event 身份变更事件
         */
        fun handleEvent(event: IdentityChangeEvent) {
            when (event) {
                is IdentityChangeEvent.WithdrawVerification -> {
                    coroutineScope.withdrawVerification(event.userId)
                }
                is IdentityChangeEvent.PinIdentity -> {
                    coroutineScope.pinUserIdentity(event.userId)
                }
            }
        }

        return IdentityChangeState(
            roomMemberIdentityStateChanges = roomMemberIdentityStateChange,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 固定用户的加密身份
     *
     * 将用户的当前加密身份标记为可信，允许与其进行加密通信。
     * 这是在用户确认信任已变更身份的用户后执行的操作。
     *
     * @param userId 要固定身份的用户的唯一ID
     * @see io.element.android.libraries.matrix.api.encryption.EncryptionService.pinUserIdentity 底层加密操作
     */
    private fun CoroutineScope.pinUserIdentity(userId: UserId) = launch {
        encryptionService.pinUserIdentity(userId)
            .onFailure {
                Timber.e(it, "Failed to pin identity for user $userId")
            }
    }

    /**
     * 撤回对用户的验证
     *
     * 撤销对用户的验证状态，表示不再信任该用户的加密身份。
     * 之后发送给该用户的消息将不再使用端到端加密。
     *
     * @param userId 要撤回验证的用户的唯一ID
     * @see io.element.android.libraries.matrix.api.encryption.EncryptionService.withdrawVerification 底层加密操作
     */
    private fun CoroutineScope.withdrawVerification(userId: UserId) = launch {
        encryptionService.withdrawVerification(userId)
            .onFailure {
                Timber.e(it, "Failed to withdraw verification for user $userId")
            }
    }
}
