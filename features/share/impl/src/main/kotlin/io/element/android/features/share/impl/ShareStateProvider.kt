/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.share.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 分享状态预览参数提供者
 *
 * 提供 ShareState 的示例值，用于在 Android Studio 预览中展示 UI 效果。
 * 包含多种状态场景：未初始化、加载中、成功、失败等。
 *
 * @see ShareState 分享状态
 */
/**
 * Preview parameter provider for ShareState.
 *
 * Provides sample values of ShareState for Android Studio preview to showcase UI effects.
 * Contains multiple state scenarios: uninitialized, loading, success, failure, etc.
 *
 * @see ShareState Share state
 */
open class ShareStateProvider : PreviewParameterProvider<ShareState> {
    /**
     * 获取预览状态序列
     *
     * @return 包含不同场景的 ShareState 序列
     */
    /**
     * Gets the sequence of preview states.
     *
     * @return A sequence of ShareState with different scenarios
     */
    override val values: Sequence<ShareState>
        get() = sequenceOf(
            aShareState(),
            aShareState(
                shareAction = AsyncAction.Loading,
            ),
            aShareState(
                shareAction = AsyncAction.Success(
                    listOf(RoomId("!room2:domain")),
                )
            ),
            aShareState(
                shareAction = AsyncAction.Failure(RuntimeException("error")),
            ),
        )
}

/**
 * 创建示例分享状态
 *
 * @param shareAction 分享操作状态，默认为未初始化
 * @param eventSink 事件处理函数，默认为空函数
 * @return 示例 ShareState 实例
 */
/**
 * Creates a sample ShareState for testing and preview purposes.
 *
 * @param shareAction The share action state, defaults to Uninitialized
 * @param eventSink The event handler function, defaults to empty function
 * @return A sample ShareState instance
 */
fun aShareState(
    shareAction: AsyncAction<List<RoomId>> = AsyncAction.Uninitialized,
    eventSink: (ShareEvents) -> Unit = {}
) = ShareState(
    shareAction = shareAction,
    eventSink = eventSink
)
