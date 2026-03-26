/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.notificationsettings

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.roomdetails.impl.aRoomNotificationSettings
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.room.RoomNotificationMode

/**
 * 用户定义房间通知设置状态提供器
 *
 * 用于预览的测试数据提供器，继承自 PreviewParameterProvider。
 * 提供用户定义通知设置的状态用于 UI 预览和测试。
 *
 * @see PreviewParameterProvider 预览参数提供器
 * @see RoomNotificationSettingsState 通知设置状态
 */
internal class UserDefinedRoomNotificationSettingsStateProvider : PreviewParameterProvider<RoomNotificationSettingsState> {
    override val values: Sequence<RoomNotificationSettingsState>
        get() = sequenceOf(
            RoomNotificationSettingsState(
                showUserDefinedSettingStyle = false,
                roomName = "Room 1",
                AsyncData.Success(
                    aRoomNotificationSettings(
                        mode = RoomNotificationMode.MUTE,
                        isDefault = false
                    )
                ),
                pendingRoomNotificationMode = null,
                pendingSetDefault = null,
                defaultRoomNotificationMode = RoomNotificationMode.ALL_MESSAGES,
                setNotificationSettingAction = AsyncAction.Uninitialized,
                restoreDefaultAction = AsyncAction.Uninitialized,
                displayMentionsOnlyDisclaimer = false,
                eventSink = { },
            ),
        )
}
