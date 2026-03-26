/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.crash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.element.android.features.rageshake.api.R
import io.element.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarHost
import io.element.android.libraries.designsystem.utils.snackbar.rememberSnackbarHostState
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 崩溃检测视图 Composable 函数
 *
 * 显示崩溃检测的用户界面。当检测到崩溃时，会显示一个对话框询问用户是否复制诊断信息。
 *
 * @param state 崩溃检测状态
 */
@Composable
fun CrashDetectionView(
    state: CrashDetectionState,
) {
    val eventSink = state.eventSink
    val snackbarHostState = rememberSnackbarHostState(snackbarMessage = state.snackbarMessage)

    fun onPopupDismissed() {
        eventSink(CrashDetectionEvent.ResetAllCrashData)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        if (state.crashDetected) {
            CrashDetectionContent(
                appName = state.appName,
                onCopyClick = {
                    eventSink(CrashDetectionEvent.CopyDiagnosticInfo)
                },
                onNoClick = ::onPopupDismissed,
                onDismiss = ::onPopupDismissed,
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun CrashDetectionContent(
    appName: String,
    onNoClick: () -> Unit = { },
    onCopyClick: () -> Unit = { },
    onDismiss: () -> Unit = { },
) {
    ConfirmationDialog(
        title = stringResource(id = R.string.crash_detection_dialog_title),
        content = stringResource(id = R.string.crash_detection_dialog_content, appName),
        submitText = stringResource(id = R.string.crash_detection_dialog_submit),
        cancelText = stringResource(id = CommonStrings.action_no),
        onCancelClick = onNoClick,
        onSubmitClick = onCopyClick,
        onDismiss = onDismiss,
    )
}

@PreviewsDayNight
@Composable
internal fun CrashDetectionViewPreview() = ElementPreview {
    CrashDetectionView(
        state = aCrashDetectionState().copy(crashDetected = true)
    )
}
