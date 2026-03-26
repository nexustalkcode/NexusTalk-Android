/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.sendfailure.resolve

import io.element.android.features.messages.impl.timeline.model.TimelineItem

/**
 * 解决已验证用户发送失败事件密封接口
 *
 * 定义用户处理加密消息发送失败时触发的事件类型。
 * 用于处理用户选择解决失败（验证设备/固定身份）或重试发送的操作。
 *
 * @see ResolveVerifiedUserSendFailureState 解决发送失败状态
 * @see ResolveVerifiedUserSendFailurePresenter 状态展示器
 */
sealed interface ResolveVerifiedUserSendFailureEvents {
    /**
     * 为特定消息计算发送失败信息
     *
     * 当需要检查某条消息是否存在发送失败时触发此事件。
     * 系统会根据消息的发送状态判断是否存在需要解决的失败情况。
     *
     * @property messageEvent 消息事件 timeline 项，包含发送状态和事务ID等信息
     */
    data class ComputeForMessage(
        val messageEvent: TimelineItem.Event,
    ) : ResolveVerifiedUserSendFailureEvents

    /**
     * 解决失败并重新发送
     *
     * 用户选择解决失败问题（如验证设备或固定身份）后重新发送消息。
     * 这会执行相应的加密操作，然后尝试重新发送消息。
     *
     * @see VerifiedUserSendFailureResolver.resolveAndResend 底层解决和重发逻辑
     */
    data object ResolveAndResend : ResolveVerifiedUserSendFailureEvents

    /**
     * 直接重试发送
     *
     * 用户选择不解决失败问题，直接尝试重新发送消息。
     * 适用于用户认为失败是暂时性的，或者已经在其他设备上完成了验证。
     *
     * @see VerifiedUserSendFailureResolver.resend 底层重发逻辑
     */
    data object Retry : ResolveVerifiedUserSendFailureEvents

    /**
     * 关闭解决失败的底部弹窗
     *
     * 用户选择暂时忽略失败，不进行任何操作。
     * 消息保持发送失败状态，但界面会关闭提示弹窗。
     */
    data object Dismiss : ResolveVerifiedUserSendFailureEvents
}
