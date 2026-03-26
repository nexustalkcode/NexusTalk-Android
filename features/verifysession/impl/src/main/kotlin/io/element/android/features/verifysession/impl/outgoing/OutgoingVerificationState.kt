/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.verifysession.impl.outgoing

import androidx.compose.runtime.Stable
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.verification.SessionVerificationData
import io.element.android.libraries.matrix.api.verification.VerificationRequest

/**
 * 发出会话验证状态数据类
 *
 * 表示发出会话验证请求的当前状态，包含验证流程的各个步骤。
 *
 * @property step 验证流程的当前步骤
 * @property request 发出的验证请求
 * @property eventSink 事件处理函数
 */
data class OutgoingVerificationState(
    val step: Step,
    val request: VerificationRequest.Outgoing,
    val eventSink: (OutgoingVerificationViewEvents) -> Unit,
) {
    /**
     * 验证步骤密封接口
     *
     * 定义会话验证流程的各个阶段。
     */
    @Stable
    sealed interface Step {
        /** 加载中 */
        data object Loading : Step
        /** 初始状态 */
        data object Initial : Step
        /** 已取消 */
        data object Canceled : Step
        /** 等待其他设备响应 */
        data object AwaitingOtherDeviceResponse : Step
        /** 已就绪 */
        data object Ready : Step
        /**
         * 正在验证
         * @property data 验证数据
         * @property state 验证操作的异步状态
         */
        data class Verifying(val data: SessionVerificationData, val state: AsyncData<Unit>) : Step
        /** 已完成 */
        data object Completed : Step
        /** 退出 */
        data object Exit : Step

        /** 是否为限时状态 */
        val isTimeLimited: Boolean
            get() = this is Initial ||
                this is AwaitingOtherDeviceResponse ||
                this is Ready ||
                this is Verifying
    }
}
