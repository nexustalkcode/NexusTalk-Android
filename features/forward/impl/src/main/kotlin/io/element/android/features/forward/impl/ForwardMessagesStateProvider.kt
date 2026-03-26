/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.forward.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 转发消息状态提供器
 *
 * 用于在预览和测试中提供 [ForwardMessagesState] 的不同状态变体。
 * 继承自 [PreviewParameterProvider]，可以与 Compose 预览功能配合使用。
 *
 * 提供以下几种状态：
 * - 未初始化状态
 * - 加载中状态
 * - 转发成功状态
 * - 转发失败状态
 *
 * @see ForwardMessagesState
 * @see PreviewParameterProvider
 */
open class ForwardMessagesStateProvider : PreviewParameterProvider<ForwardMessagesState> {
    /**
     * 状态序列
     *
     * 包含四种状态变体，用于预览和测试转发消息界面的不同状态。
     */
    override val values: Sequence<ForwardMessagesState>
        get() = sequenceOf(
            // 默认状态 - 未初始化
            aForwardMessagesState(),
            // 加载中状态 - 正在转发
            aForwardMessagesState(
                forwardAction = AsyncAction.Loading,
            ),
            // 成功状态 - 转发完成
            aForwardMessagesState(
                forwardAction = AsyncAction.Success(
                    listOf(RoomId("!room2:domain")),
                )
            ),
            // 失败状态 - 转发出错
            aForwardMessagesState(
                forwardAction = AsyncAction.Failure(RuntimeException("error")),
            ),
        )
}

/**
 * 创建测试用的转发消息状态
 *
 * 辅助函数，用于快速创建 [ForwardMessagesState] 实例。
 *
 * @param forwardAction 转发操作的异步状态，默认为未初始化
 * @param eventSink 事件处理函数，默认为空函数
 * @return ForwardMessagesState 创建的状态实例
 */
fun aForwardMessagesState(
    forwardAction: AsyncAction<List<RoomId>> = AsyncAction.Uninitialized,
    eventSink: (ForwardMessagesEvents) -> Unit = {}
) = ForwardMessagesState(
    forwardAction = forwardAction,
    eventSink = eventSink
)
