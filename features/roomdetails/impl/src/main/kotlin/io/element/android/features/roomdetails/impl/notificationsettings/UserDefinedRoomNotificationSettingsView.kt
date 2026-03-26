/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.notificationsettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.features.roomdetails.impl.R
import io.element.android.libraries.core.bool.orTrue
import io.element.android.libraries.designsystem.components.async.AsyncActionView
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.ListItemStyle
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TopAppBar

/**
 * 用户定义房间通知设置视图
 *
 * Composable 函数，用于渲染用户定义的通知设置页面。
 * 这是一个替代的 UI 样式，以房间名称作为标题。
 *
 * @param state 通知设置状态
 * @param onBackClick 返回按钮回调
 * @param modifier 视图修饰符
 * @see RoomNotificationSettingsState 通知设置状态
 */
@Composable
fun UserDefinedRoomNotificationSettingsView(
    state: RoomNotificationSettingsState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            UserDefinedRoomNotificationSettingsTopBar(
                roomName = state.roomName,
                onBackClick = { onBackClick() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .consumeWindowInsets(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val roomNotificationSettings = state.roomNotificationSettings.dataOrNull()
            if (roomNotificationSettings != null && state.displayNotificationMode != null) {
                RoomNotificationSettingsOptions(
                    selected = state.displayNotificationMode,
                    enabled = !state.displayIsDefault.orTrue(),
                    displayMentionsOnlyDisclaimer = state.displayMentionsOnlyDisclaimer,
                    onSelectOption = {
                        state.eventSink(RoomNotificationSettingsEvents.ChangeRoomNotificationMode(it.mode))
                    },
                )
            }

            ListItem(
                headlineContent = { Text(stringResource(R.string.screen_room_notification_settings_edit_remove_setting)) },
                style = ListItemStyle.Destructive,
                onClick = {
                    state.eventSink(RoomNotificationSettingsEvents.DeleteCustomNotification)
                }
            )

            AsyncActionView(
                async = state.setNotificationSettingAction,
                onSuccess = {},
                errorMessage = { stringResource(R.string.screen_notification_settings_edit_failed_updating_default_mode) },
                onErrorDismiss = { state.eventSink(RoomNotificationSettingsEvents.ClearSetNotificationError) },
            )

            AsyncActionView(
                async = state.restoreDefaultAction,
                onSuccess = { onBackClick() },
                errorMessage = { stringResource(R.string.screen_notification_settings_edit_failed_updating_default_mode) },
                onErrorDismiss = { state.eventSink(RoomNotificationSettingsEvents.ClearRestoreDefaultError) },
            )
        }
    }
}

/**
 * 用户定义通知设置顶部导航栏
 *
 * Composable 函数，用于渲染用户定义通知设置页面的顶部导航栏。
 * 标题显示房间名称。
 *
 * @param roomName 房间名称
 * @param onBackClick 返回按钮点击回调
 * @see ExperimentalMaterial3Api 实验性 Material3 API
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserDefinedRoomNotificationSettingsTopBar(
    roomName: String,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        titleStr = roomName,
        navigationIcon = { BackButton(onClick = onBackClick) },
    )
}

@PreviewsDayNight
@Composable
internal fun UserDefinedRoomNotificationSettingsViewPreview(
    @PreviewParameter(UserDefinedRoomNotificationSettingsStateProvider::class) state: RoomNotificationSettingsState
) = ElementPreview {
    UserDefinedRoomNotificationSettingsView(
        state = state,
        onBackClick = {},
    )
}
