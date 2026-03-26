/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.verifysession.impl.incoming

import androidx.compose.runtime.Stable
import io.element.android.libraries.matrix.api.core.DeviceId
import io.element.android.libraries.matrix.api.verification.SessionVerificationData
import io.element.android.libraries.matrix.api.verification.VerificationRequest

/**
 * 接收会话验证状态数据类
 *
 * 表示接收会话验证请求的当前状态，包含验证流程的各个步骤。
 *
 * @property step 验证流程的当前步骤
 * @property request 接收的验证请求
 * @property eventSink 事件处理函数
 */
data class IncomingVerificationState(
    val step: Step,
    val request: VerificationRequest.Incoming,
    val eventSink: (IncomingVerificationViewEvents) -> Unit,
) {
    /**
     * 验证步骤密封接口
     *
     * 定义会话验证流程的各个阶段。
     */
    @Stable
    sealed interface Step {
        /**
         * 初始步骤
         * @property deviceDisplayName 设备显示名称
         * @property deviceId 设备 ID
         * @property formattedSignInTime 格式化的登录时间
         * @property isWaiting 是否等待中
         */
        data class Initial(
            val deviceDisplayName: String?,
            val deviceId: DeviceId,
            val formattedSignInTime: String,
            val isWaiting: Boolean,
        ) : Step

        /**
         * 正在验证步骤
         * @property data 验证数据
         * @property isWaiting 是否等待中
         */
        data class Verifying(
            val data: SessionVerificationData,
            val isWaiting: Boolean,
        ) : Step

        /** 已取消 */
        data object Canceled : Step
        /** 已完成 */
        data object Completed : Step
        /** 失败 */
        data object Failure : Step

        /** 是否为限时状态 */
        val isTimeLimited: Boolean
            get() = this is Initial ||
                this is Verifying
    }
}
