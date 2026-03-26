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
 * 房间通知设置状态提供器
 *
 * 用于预览的测试数据提供器，继承自 PreviewParameterProvider。
 * 提供多种通知设置状态用于 UI 预览和测试。
 *
 * @see PreviewParameterProvider 预览参数提供器
 * @see RoomNotificationSettingsState 通知设置状态
 */
internal class RoomNotificationSettingsStateProvider : PreviewParameterProvider<RoomNotificationSettingsState> {
    /**
     * 获取状态序列
     *
     * 返回包含不同状态的序列，用于预览组件的不同场景。
     * 包括默认设置、自定义设置、加载中、错误等状态。
     */
    override val values: Sequence<RoomNotificationSettingsState>
        get() = sequenceOf(
            aRoomNotificationSettingsState(),
            aRoomNotificationSettingsState(isDefault = false),
            aRoomNotificationSettingsState(setNotificationSettingAction = AsyncAction.Loading),
            aRoomNotificationSettingsState(setNotificationSettingAction = AsyncAction.Failure(RuntimeException("error"))),
            aRoomNotificationSettingsState(restoreDefaultAction = AsyncAction.Loading),
            aRoomNotificationSettingsState(restoreDefaultAction = AsyncAction.Failure(RuntimeException("error"))),
            aRoomNotificationSettingsState(displayMentionsOnlyDisclaimer = true)
        )

    private fun aRoomNotificationSettingsState(
        isDefault: Boolean = true,
        setNotificationSettingAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
        restoreDefaultAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
        displayMentionsOnlyDisclaimer: Boolean = false,
    ): RoomNotificationSettingsState {
        return RoomNotificationSettingsState(
            showUserDefinedSettingStyle = false,
            roomName = "Room 1",
            AsyncData.Success(aRoomNotificationSettings(
                mode = RoomNotificationMode.MUTE,
                isDefault = isDefault
            )),
            pendingRoomNotificationMode = null,
            pendingSetDefault = null,
            defaultRoomNotificationMode = RoomNotificationMode.ALL_MESSAGES,
            setNotificationSettingAction = setNotificationSettingAction,
            restoreDefaultAction = restoreDefaultAction,
            displayMentionsOnlyDisclaimer = displayMentionsOnlyDisclaimer,
            eventSink = { },
        )
    }
}
