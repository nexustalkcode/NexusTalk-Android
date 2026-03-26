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
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 语音消息发送失败对话框
 *
 * 当语音消息发送失败时，显示此错误对话框通知用户。
 * 用户可以点击"确定"关闭对话框。
 *
 * @param onDismiss 点击确定按钮或关闭对话框时的回调
 */
@Composable
internal fun VoiceMessageSendingFailedDialog(
    onDismiss: () -> Unit,
) {
    ErrorDialog(
        title = stringResource(CommonStrings.common_error),
        content = stringResource(CommonStrings.error_failed_uploading_voice_message),
        onSubmit = onDismiss,
        submitText = stringResource(CommonStrings.action_ok),
    )
}
