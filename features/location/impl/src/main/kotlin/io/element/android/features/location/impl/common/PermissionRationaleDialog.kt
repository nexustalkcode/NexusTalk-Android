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
 * 权限说明理由对话框
 *
 * 当系统建议显示权限说明理由时显示的对话框，向用户解释为什么需要位置权限。
 *
 * @param onContinue 点击继续按钮的回调，通常用于触发权限请求
 * @param onDismiss 点击取消/关闭按钮的回调
 * @param appName 应用名称，用于对话框中显示
 */
@Composable
internal fun PermissionRationaleDialog(
    onContinue: () -> Unit,
    onDismiss: () -> Unit,
    appName: String,
) {
    ConfirmationDialog(
        content = stringResource(CommonStrings.error_missing_location_rationale_android, appName),
        onSubmitClick = onContinue,
        onDismiss = onDismiss,
        submitText = stringResource(CommonStrings.action_continue),
        cancelText = stringResource(CommonStrings.action_cancel),
    )
}
