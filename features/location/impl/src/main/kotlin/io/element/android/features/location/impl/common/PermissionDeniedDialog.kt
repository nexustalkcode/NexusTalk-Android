/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.element.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 权限被拒绝对话框
 *
 * 当位置权限被用户拒绝后显示的对话框，提示用户需要在设置中手动开启权限。
 *
 * @param onContinue 点击继续按钮的回调，通常用于打开应用设置
 * @param onDismiss 点击取消/关闭按钮的回调
 * @param appName 应用名称，用于对话框中显示
 */
@Composable
internal fun PermissionDeniedDialog(
    onContinue: () -> Unit,
    onDismiss: () -> Unit,
    appName: String,
) {
    ConfirmationDialog(
        content = stringResource(CommonStrings.error_missing_location_auth_android, appName),
        onSubmitClick = onContinue,
        onDismiss = onDismiss,
        submitText = stringResource(CommonStrings.action_continue),
        cancelText = stringResource(CommonStrings.action_cancel),
    )
}
