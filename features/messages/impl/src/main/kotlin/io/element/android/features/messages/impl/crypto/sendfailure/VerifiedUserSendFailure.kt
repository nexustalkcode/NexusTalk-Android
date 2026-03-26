/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.sendfailure

import androidx.compose.runtime.Immutable

/**
 * 已验证用户发送失败密封接口
 *
 * 定义加密消息发送失败的各种原因类型。
 * 用于处理设备未验证和身份变更等场景。
 *
 * @see Immutable 可变注解
 */
@Immutable
sealed interface VerifiedUserSendFailure {
    /** 没有发送失败 */
    data object None : VerifiedUserSendFailure

    /**
     * 未验证设备密封接口
     */
    sealed interface UnsignedDevice : VerifiedUserSendFailure {
        /** 来自你的未验证设备 */
        data object FromYou : UnsignedDevice

        /**
         * 来自其他用户的未验证设备
         *
         * @property userDisplayName 用户显示名称
         */
        data class FromOther(val userDisplayName: String) : UnsignedDevice
    }

    /**
     * 身份已变更
     *
     * @property userDisplayName 用户显示名称
     */
    data class ChangedIdentity(
        val userDisplayName: String,
    ) : VerifiedUserSendFailure
}
