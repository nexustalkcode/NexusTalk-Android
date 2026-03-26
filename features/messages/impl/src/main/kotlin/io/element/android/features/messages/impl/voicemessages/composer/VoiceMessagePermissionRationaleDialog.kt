/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.voicemessages.composer

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.element.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 语音消息权限说明对话框
 *
 * 当应用没有麦克风权限时，显示此对话框向用户解释为什么需要此权限。
 * 用户可以选择"继续"（打开系统设置）或"取消"。
 *
 * @param onContinue 点击继续按钮时的回调，通常用于打开系统设置页面
 * @param onDismiss 点击取消或关闭对话框时的回调
 * @param appName 应用名称，用于显示在对话框内容中
 */
@Composable
internal fun VoiceMessagePermissionRationaleDialog(
    onContinue: () -> Unit,
    onDismiss: () -> Unit,
    appName: String,
) {
    ConfirmationDialog(
        content = stringResource(CommonStrings.error_missing_microphone_voice_rationale_android, appName),
        onSubmitClick = onContinue,
        onDismiss = onDismiss,
        submitText = stringResource(CommonStrings.action_continue),
        cancelText = stringResource(CommonStrings.action_cancel),
    )
}
