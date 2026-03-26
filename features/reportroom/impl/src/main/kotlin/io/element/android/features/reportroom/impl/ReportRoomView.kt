/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.reportroom.impl

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.async.AsyncActionView
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextField
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 举报房间页面的 Compose 视图层
 *
 * 使用 Jetpack Compose 框架构建举报房间的用户界面
 * 包含举报原因输入框、离开房间开关和举报按钮
 *
 * @param state 举报房间的界面状态
 * @param onBackClick 返回按钮的点击回调
 * @param modifier 视图修饰符，用于控制布局和样式
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportRoomView(
    state: ReportRoomState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 获取当前焦点管理器，用于在提交时清除焦点
    val focusManager = LocalFocusManager.current

    // 判断是否正在进行举报操作
    val isReporting = state.reportAction is AsyncAction.Loading

    // 异步操作状态视图，处理加载、成功和错误状态
    AsyncActionView(
        async = state.reportAction,
        onSuccess = { onBackClick() },  // 举报成功后返回上一页
        errorTitle = { failure ->
            // 根据不同错误类型显示不同的标题
            when (failure) {
                is ReportRoom.Exception.LeftRoomFailed -> stringResource(R.string.screen_report_room_leave_failed_alert_title)
                else -> stringResource(CommonStrings.dialog_title_error)
            }
        },
        errorMessage = { failure ->
            // 根据不同错误类型显示不同的错误消息
            when (failure) {
                is ReportRoom.Exception.LeftRoomFailed -> stringResource(R.string.screen_report_room_leave_failed_alert_message)
                else -> stringResource(CommonStrings.error_unknown)
            }
        },
        onRetry = {
            // 重试举报操作
            state.eventSink(ReportRoomEvents.Report)
        },
        onErrorDismiss = { state.eventSink(ReportRoomEvents.ClearReportAction) }
    )

    // 脚手架布局，包含顶部应用栏和内容区域
    Scaffold(
        topBar = {
            TopAppBar(
                titleStr = stringResource(R.string.screen_report_room_title),
                navigationIcon = {
                    BackButton(onClick = onBackClick)
                }
            )
        },
        modifier = modifier
    ) { padding ->
        // 主内容区域，使用列布局
        Column(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()  // 处理键盘弹出时的内边距
                .fillMaxSize()
                .verticalScroll(rememberScrollState())  // 支持垂直滚动
                .padding(vertical = 16.dp)
        ) {
            // 举报原因输入框
            TextField(
                value = state.reason,
                onValueChange = { state.eventSink(ReportRoomEvents.UpdateReason(it)) },
                placeholder = stringResource(R.string.screen_report_room_reason_placeholder),
                minLines = 3,
                enabled = !isReporting,  // 举报进行中时禁用输入
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .heightIn(min = 90.dp),
                supportingText = stringResource(R.string.screen_report_room_reason_footer),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 离开房间开关列表项
            ListItem(
                modifier = Modifier.padding(end = 8.dp),
                headlineContent = {
                    Text(text = stringResource(CommonStrings.action_leave_room))
                },
                onClick = {
                    state.eventSink(ReportRoomEvents.ToggleLeaveRoom)
                },
                trailingContent = ListItemContent.Switch(checked = state.leaveRoom)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 举报按钮
            Button(
                text = stringResource(CommonStrings.action_report),
                enabled = state.canReport && !isReporting,  // 有举报原因且不在举报中时可点击
                destructive = true,  // 使用危险操作样式（红色）
                showProgress = isReporting,  // 举报进行中显示加载指示器
                onClick = {
                    // 清除焦点以隐藏键盘
                    focusManager.clearFocus(force = true)
                    // 触发举报事件
                    state.eventSink(ReportRoomEvents.Report)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * 举报房间视图的预览函数
 *
 * 用于在 Android Studio 预览模式下展示不同的状态组合
 *
 * @param state 预览参数，由 [ReportRoomStateProvider] 提供
 */
@PreviewsDayNight
@Composable
internal fun ReportRoomViewPreview(
    @PreviewParameter(ReportRoomStateProvider::class) state: ReportRoomState
) = ElementPreview {
    ReportRoomView(
        state = state,
        onBackClick = {},
    )
}
