/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.sendfailure.resolve

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailure
import io.element.android.libraries.architecture.AsyncAction

/**
 * 解决已验证用户发送失败状态预览参数提供者
 *
 * 继承自 [PreviewParameterProvider]，用于在预览环境中提供 [ResolveVerifiedUserSendFailureState] 的示例数据。
 * 主要用于 Android Studio 的 Compose 预览功能，帮助开发者快速查看 UI 在不同失败状态下的渲染效果。
 *
 * 提供三种预览状态：
 * 1. 无失败（默认状态）
 * 2. 未验证设备失败
 * 3. 身份变更失败
 *
 * @see ResolveVerifiedUserSendFailureState 解决发送失败状态数据类
 * @see ResolveVerifiedUserSendFailureView 解决发送失败视图
 */
open class ResolveVerifiedUserSendFailureStateProvider : PreviewParameterProvider<ResolveVerifiedUserSendFailureState> {
    /**
     * 提供预览状态序列
     *
     * 返回包含三种不同失败状态的序列，用于预览不同场景下的 UI 效果。
     *
     * @return 包含不同 [ResolveVerifiedUserSendFailureState] 示例的序列
     */
    override val values: Sequence<ResolveVerifiedUserSendFailureState>
        get() = sequenceOf(
            aResolveVerifiedUserSendFailureState(),
            aResolveVerifiedUserSendFailureState(
                verifiedUserSendFailure = anUnsignedDeviceSendFailure()
            ),
            aResolveVerifiedUserSendFailureState(
                verifiedUserSendFailure = aChangedIdentitySendFailure()
            )
        )
}

/**
 * 创建解决发送失败状态测试数据
 *
 * 用于在测试和预览中快速创建 [ResolveVerifiedUserSendFailureState] 实例。
 *
 * @param verifiedUserSendFailure 已验证用户发送失败信息
 * @param resolveAction 解决操作的异步状态
 * @param retryAction 重试操作的异步状态
 * @param eventSink 事件处理函数
 * @return [ResolveVerifiedUserSendFailureState] 实例
 */
fun aResolveVerifiedUserSendFailureState(
    verifiedUserSendFailure: VerifiedUserSendFailure = VerifiedUserSendFailure.None,
    resolveAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    retryAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (ResolveVerifiedUserSendFailureEvents) -> Unit = {}
) = ResolveVerifiedUserSendFailureState(
    verifiedUserSendFailure = verifiedUserSendFailure,
    resolveAction = resolveAction,
    retryAction = retryAction,
    eventSink = eventSink
)

/**
 * 创建未验证设备发送失败测试数据
 *
 * 用于在测试和预览中快速创建 [VerifiedUserSendFailure.UnsignedDevice.FromOther] 实例。
 *
 * @param userDisplayName 用户显示名称，默认为 "Alice"
 * @return [VerifiedUserSendFailure.UnsignedDevice.FromOther] 实例
 */
fun anUnsignedDeviceSendFailure(userDisplayName: String = "Alice") = VerifiedUserSendFailure.UnsignedDevice.FromOther(
    userDisplayName = userDisplayName,
)

/**
 * 创建身份变更发送失败测试数据
 *
 * 用于在测试和预览中快速创建 [VerifiedUserSendFailure.ChangedIdentity] 实例。
 *
 * @param userDisplayName 用户显示名称，默认为 "Alice"
 * @return [VerifiedUserSendFailure.ChangedIdentity] 实例
 */
fun aChangedIdentitySendFailure(userDisplayName: String = "Alice") = VerifiedUserSendFailure.ChangedIdentity(
    userDisplayName = userDisplayName,
)
