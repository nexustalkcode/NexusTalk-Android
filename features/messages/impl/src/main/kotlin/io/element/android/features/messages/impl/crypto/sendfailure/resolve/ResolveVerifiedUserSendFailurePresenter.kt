/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.sendfailure.resolve

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.element.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailure
import io.element.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailureFactory
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runUpdatingState
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.timeline.item.event.LocalEventSendState
import kotlinx.coroutines.launch

/**
 * 解决已验证用户发送失败状态展示器
 *
 * 负责管理和呈现已验证用户消息发送失败的状态和解决方案。
 * 当加密消息发送给已验证用户失败时，此展示器提供用户交互界面来处理失败情况。
 *
 * 功能说明：
 * - 检测消息发送失败的具体原因（未验证设备或身份变更）
 * - 创建解决器来处理失败情况
 * - 提供用户操作接口（解决并重试、直接重试、关闭提示）
 * - 追踪操作执行状态（加载、成功、失败）
 *
 * 失败类型：
 * - 未验证设备: 接收者有未验证的加密设备
 * - 身份变更: 接收者的加密身份发生了变更
 *
 * @property room 已加入的房间实例
 * @property verifiedUserSendFailureFactory 发送失败工厂，用于创建失败状态对象
 *
 * @see ResolveVerifiedUserSendFailureState 解决发送失败状态
 * @see ResolveVerifiedUserSendFailureEvents 解决发送失败事件
 * @see VerifiedUserSendFailureResolver 失败解决器
 */
@Inject
class ResolveVerifiedUserSendFailurePresenter(
    private val room: JoinedRoom,
    private val verifiedUserSendFailureFactory: VerifiedUserSendFailureFactory,
) : Presenter<ResolveVerifiedUserSendFailureState> {
    /**
     * 呈现解决发送失败状态
     *
     * 此方法是 Presenter 接口的核心实现，负责：
     * 1. 管理失败解决器（VerifiedUserSendFailureResolver）的生命周期
     * 2. 根据解决器的当前状态生成发送失败信息
     * 3. 追踪用户操作（解决、重试、关闭）的执行状态
     * 4. 处理用户事件并触发相应的操作
     *
     * 状态管理：
     * - resolver: 当前活动的失败解决器实例
     * - verifiedUserSendFailure: 基于解决器状态生成的失败信息
     * - resolveAction: "解决并重试"操作的执行状态
     * - retryAction: "直接重试"操作的执行状态
     *
     * @return [ResolveVerifiedUserSendFailureState] 包含失败信息和事件处理函数
     */
    @Composable
    override fun present(): ResolveVerifiedUserSendFailureState {
        var resolver by remember {
            mutableStateOf<VerifiedUserSendFailureResolver?>(null)
        }
        // 使用 produceState 响应解决器状态变化
        val verifiedUserSendFailure by produceState<VerifiedUserSendFailure>(VerifiedUserSendFailure.None, resolver?.currentSendFailure?.value) {
            val currentSendFailure = resolver?.currentSendFailure?.value
            value = verifiedUserSendFailureFactory.create(currentSendFailure)
        }

        // 追踪解决操作的异步状态
        val resolveAction = remember {
            mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized)
        }
        // 追踪重试操作的异步状态
        val retryAction = remember {
            mutableStateOf<AsyncAction<Unit>>(AsyncAction.Uninitialized)
        }
        val coroutineScope = rememberCoroutineScope()

        /**
         * 处理解决发送失败事件
         *
         * 根据事件类型执行相应的操作：
         * - ComputeForMessage: 为消息创建失败解决器
         * - Dismiss: 清除解决器，关闭提示
         * - Retry: 直接重试发送
         * - ResolveAndResend: 解决失败后重发
         *
         * @param event 解决发送失败事件
         */
        fun handleEvent(event: ResolveVerifiedUserSendFailureEvents) {
            when (event) {
                // 为特定消息创建失败解决器
                // 检查消息是否有发送失败状态，如果有则创建解决器实例
                is ResolveVerifiedUserSendFailureEvents.ComputeForMessage -> {
                    val sendState = event.messageEvent.localSendState as? LocalEventSendState.Failed.VerifiedUser
                    val transactionId = event.messageEvent.transactionId
                    val sendHandle = event.messageEvent.sendhandle
                    resolver = if (sendState != null && transactionId != null && sendHandle != null) {
                        VerifiedUserSendFailureResolver(
                            room = room,
                            transactionId = transactionId,
                            sendHandle = sendHandle,
                            iterator = VerifiedUserSendFailureIterator.from(sendState)
                        )
                    } else {
                        null
                    }
                }
                ResolveVerifiedUserSendFailureEvents.Dismiss -> {
                    resolver = null
                }
                ResolveVerifiedUserSendFailureEvents.Retry -> {
                    coroutineScope.launch {
                        resolver?.run {
                            runUpdatingState(retryAction) {
                                resend()
                            }
                        }
                    }
                }
                ResolveVerifiedUserSendFailureEvents.ResolveAndResend -> {
                    coroutineScope.launch {
                        resolver?.run {
                            runUpdatingState(resolveAction) {
                                resolveAndResend()
                            }
                        }
                    }
                }
            }
        }

        return ResolveVerifiedUserSendFailureState(
            verifiedUserSendFailure = verifiedUserSendFailure,
            resolveAction = resolveAction.value,
            retryAction = retryAction.value,
            eventSink = ::handleEvent,
        )
    }
}
