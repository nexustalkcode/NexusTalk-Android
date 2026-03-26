/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.identity

import io.element.android.libraries.matrix.api.core.UserId

/**
 * 身份变更事件密封接口
 *
 * 定义用户处理房间成员身份变更事件时触发的事件类型。
 * 用于处理用户对身份验证状态变更的响应操作。
 *
 * 身份变更是指已验证用户的加密身份发生了变化，
 * 可能存在安全风险，需要用户确认是否继续信任该用户。
 *
 * @see IdentityChangeState 身份变更状态
 * @see IdentityChangeStatePresenter 状态展示器
 */
sealed interface IdentityChangeEvent {
    /**
     * 固定（信任）用户的新身份
     *
     * 当用户确认信任已变更身份的用户时触发此事件。
     * 这会将用户的当前加密身份标记为可信，允许与其进行加密通信。
     *
     * @property userId 用户的唯一ID
     * @see io.element.android.libraries.matrix.api.encryption.EncryptionService.pinUserIdentity 底层操作
     */
    data class PinIdentity(val userId: UserId) : IdentityChangeEvent

    /**
     * 撤回对用户的验证
     *
     * 当用户不再信任已变更身份的用户时触发此事件。
     * 这会撤销对该用户的验证，消息将不再使用端到端加密发送。
     *
     * @property userId 用户的唯一ID
     * @see io.element.android.libraries.matrix.api.encryption.EncryptionService.withdrawVerification 底层操作
     */
    data class WithdrawVerification(val userId: UserId) : IdentityChangeEvent
}
