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
import io.element.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 退出登录确认对话框
 *
 * 用于在用户点击退出登录按钮后显示的确认对话框，
 * 让用户确认是否真的要退出登录。
 *
 * @param onSubmitClick 点击确认退出按钮的回调
 * @param onDismiss 点击取消/关闭按钮的回调
 */
@Composable
fun LogoutConfirmationDialog(
    onSubmitClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    ConfirmationDialog(
        title = stringResource(id = CommonStrings.action_signout),
        content = stringResource(id = R.string.screen_signout_confirmation_dialog_content),
        submitText = stringResource(id = CommonStrings.action_signout),
        onSubmitClick = onSubmitClick,
        onDismiss = onDismiss,
    )
}
