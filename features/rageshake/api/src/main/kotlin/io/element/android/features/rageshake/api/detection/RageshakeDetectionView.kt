/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.detection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import io.element.android.features.rageshake.api.R
import io.element.android.features.rageshake.api.screenshot.ImageResult
import io.element.android.features.rageshake.api.screenshot.screenshot
import io.element.android.libraries.androidutils.hardware.vibrate
import io.element.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.utils.OnLifecycleEvent
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 摇一摇检测视图 Composable 函数
 *
 * 显示摇一摇检测的用户界面。当检测到摇晃动作时，会显示一个对话框询问用户是否报告问题。
 * 支持截图功能，可以在用户同意时截取当前屏幕。
 *
 * @param state 摇一摇检测状态
 * @param onOpenBugReport 打开问题报告页面的回调函数
 */
@Composable
fun RageshakeDetectionView(
    state: RageshakeDetectionState,
    onOpenBugReport: () -> Unit = { },
) {
    val eventSink = state.eventSink
    val context = LocalContext.current
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> eventSink(RageshakeDetectionEvent.StartDetection)
            Lifecycle.Event.ON_PAUSE -> eventSink(RageshakeDetectionEvent.StopDetection)
            else -> Unit
        }
    }
    when {
        state.takeScreenshot -> TakeScreenshot(
            onScreenshot = { eventSink(RageshakeDetectionEvent.ProcessScreenshot(it)) }
        )
        state.showDialog -> {
            LaunchedEffect(Unit) {
                context.vibrate()
            }
            RageshakeDialogContent(
                onNoClick = { eventSink(RageshakeDetectionEvent.Dismiss) },
                onDisableClick = { eventSink(RageshakeDetectionEvent.Disable) },
                onYesClick = onOpenBugReport
            )
        }
    }
}

/**
 * 截图 Composable 函数
 *
 * 使用系统API截取当前屏幕的截图。
 *
 * @param onScreenshot 截图完成后的回调函数，传递截图结果
 */
@Composable
private fun TakeScreenshot(
    onScreenshot: (ImageResult) -> Unit
) {
    val view = LocalView.current
    val latestOnScreenshot by rememberUpdatedState(onScreenshot)
    LaunchedEffect(Unit) {
        view.screenshot {
            latestOnScreenshot(it)
        }
    }
}

/**
 * 摇一摇检测对话框内容 Composable 函数
 *
 * 显示摇一摇检测触发后的确认对话框，包含是否报告问题、是否禁用功能等选项。
 *
 * @param onNoClick 拒绝报告问题的回调函数
 * @param onDisableClick 禁用摇一摇功能的回调函数
 * @param onYesClick 同意报告问题的回调函数
 */
@Composable
private fun RageshakeDialogContent(
    onNoClick: () -> Unit = { },
    onDisableClick: () -> Unit = { },
    onYesClick: () -> Unit = { },
) {
    ConfirmationDialog(
        title = stringResource(id = CommonStrings.action_report_bug),
        content = stringResource(id = R.string.rageshake_detection_dialog_content),
        thirdButtonText = stringResource(id = CommonStrings.action_disable),
        submitText = stringResource(id = CommonStrings.action_yes),
        cancelText = stringResource(id = CommonStrings.action_no),
        onCancelClick = onNoClick,
        onThirdButtonClick = onDisableClick,
        onSubmitClick = onYesClick,
        onDismiss = onNoClick,
    )
}

@PreviewsDayNight
@Composable
internal fun RageshakeDialogContentPreview() = ElementPreview {
    RageshakeDialogContent()
}
