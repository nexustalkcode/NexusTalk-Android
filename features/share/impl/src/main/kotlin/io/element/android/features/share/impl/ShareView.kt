/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.share.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.libraries.designsystem.components.async.AsyncActionView
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 分享视图组件
 *
 * 使用 Jetpack Compose 实现分享状态的用户界面。
 * 展示分享操作的加载、成功和错误状态。
 *
 * @param state 当前视图状态
 * @param onShareSuccess 分享成功回调
 */
/**
 * Share view component.
 *
 * Implements the user interface for share state using Jetpack Compose.
 * Displays loading, success, and error states of the share operation.
 *
 * @param state Current view state
 * @param onShareSuccess Callback for share success
 */
@Composable
fun ShareView(
    state: ShareState,
    onShareSuccess: (List<RoomId>) -> Unit,
) {
    AsyncActionView(
        async = state.shareAction,
        onSuccess = {
            onShareSuccess(it)
        },
        onErrorDismiss = {
            state.eventSink(ShareEvents.ClearError)
        },
    )
}

/**
 * 分享视图预览组件
 *
 * 用于在 Android Studio 预览中展示分享视图 UI。
 *
 * @param state 预览状态
 */
/**
 * Share view preview component.
 *
 * Used to preview the share view UI in Android Studio.
 *
 * @param state Preview state
 */
@PreviewsDayNight
@Composable
internal fun ShareViewPreview(@PreviewParameter(ShareStateProvider::class) state: ShareState) = ElementPreview {
    ShareView(
        state = state,
        onShareSuccess = {}
    )
}
