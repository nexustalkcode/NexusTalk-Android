/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.forward.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.libraries.designsystem.components.async.AsyncActionView
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 转发消息视图
 *
 * Compose 组件，用于渲染转发消息界面。
 * 基于 [ForwardMessagesState] 中的状态显示相应的 UI：
 * - 当转发进行中时显示加载指示器
 * - 当转发成功时调用成功回调
 * - 当转发失败时显示错误消息
 *
 * @param state 转发消息状态，包含转发操作的异步状态
 * @param onForwardSuccess 转发成功时的回调函数，参数为成功转发到的房间 ID 列表
 *
 * @see ForwardMessagesState
 * @see AsyncActionView
 */
@Composable
fun ForwardMessagesView(
    state: ForwardMessagesState,
    onForwardSuccess: (List<RoomId>) -> Unit,
) {
    // 使用异步操作视图处理转发状态
    AsyncActionView(
        // 转发操作的异步状态
        async = state.forwardAction,
        // 转发成功时调用回调
        onSuccess = {
            onForwardSuccess(it)
        },
        // 错误时显示通用错误消息
        errorMessage = {
            stringResource(id = CommonStrings.error_unknown)
        },
        // 关闭错误提示时触发清除错误事件
        onErrorDismiss = {
            state.eventSink(ForwardMessagesEvents.ClearError)
        },
    )
}

/**
 * 转发消息视图预览
 *
 * 使用 [PreviewsDayNight] 注解提供日夜两种主题的预览。
 *
 * @param state 用于预览的转发消息状态，由 [ForwardMessagesStateProvider] 提供
 *
 * @see PreviewsDayNight
 * @see ForwardMessagesStateProvider
 */
@PreviewsDayNight
@Composable
internal fun ForwardMessagesViewPreview(@PreviewParameter(ForwardMessagesStateProvider::class) state: ForwardMessagesState) = ElementPreview {
    ForwardMessagesView(
        state = state,
        onForwardSuccess = {}
    )
}
