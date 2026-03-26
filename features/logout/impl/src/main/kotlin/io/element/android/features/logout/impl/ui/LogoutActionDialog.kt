/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.element.android.features.logout.impl.R
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.dialogs.RetryDialog
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 退出登录操作对话框
 *
 * 根据不同的异步状态显示相应的对话框：
 * - 未初始化：不显示对话框
 * - 确认中：显示确认对话框
 * - 加载中：显示加载进度对话框
 * - 失败：显示重试对话框
 * - 成功：不显示对话框
 *
 * @param state 退出登录操作的异步状态
 * @param onConfirmClick 点击确认按钮的回调
 * @param onForceLogoutClick 点击强制退出按钮的回调
 * @param onDismissDialog 关闭对话框的回调
 */
@Composable
fun LogoutActionDialog(
    state: AsyncAction<Unit>,
    onConfirmClick: () -> Unit,
    onForceLogoutClick: () -> Unit,
    onDismissDialog: () -> Unit,
) {
    when (state) {
        // 未初始化状态，不显示对话框
        AsyncAction.Uninitialized ->
            Unit
        // 确认中状态，显示确认对话框
        is AsyncAction.Confirming ->
            LogoutConfirmationDialog(
                onSubmitClick = onConfirmClick,
                onDismiss = onDismissDialog
            )
        // 加载中状态，显示进度对话框
        is AsyncAction.Loading ->
            ProgressDialog(text = stringResource(id = R.string.screen_signout_in_progress_dialog_content))
        // 失败状态，显示重试对话框
        is AsyncAction.Failure ->
            RetryDialog(
                title = stringResource(id = CommonStrings.dialog_title_error),
                content = stringResource(id = CommonStrings.error_unknown),
                retryText = stringResource(id = CommonStrings.action_signout_anyway),
                onRetry = onForceLogoutClick,
                onDismiss = onDismissDialog,
            )
        // 成功状态，不显示对话框
        is AsyncAction.Success -> Unit
    }
}
