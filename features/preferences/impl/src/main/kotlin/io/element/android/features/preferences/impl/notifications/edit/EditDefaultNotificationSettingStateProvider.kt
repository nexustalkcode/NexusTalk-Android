/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.notifications.edit

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.RoomNotificationMode
import kotlinx.collections.immutable.persistentListOf

/**
 * 编辑默认通知设置状态提供者
 *
 * 用于在预览模式下提供编辑默认通知设置页面的示例状态数据。
 *
 * @see EditDefaultNotificationSettingState 编辑默认通知设置状态
 */
open class EditDefaultNotificationSettingStateProvider : PreviewParameterProvider<EditDefaultNotificationSettingState> {
    override val values: Sequence<EditDefaultNotificationSettingState>
        get() = sequenceOf(
            anEditDefaultNotificationSettingsState(),
            anEditDefaultNotificationSettingsState(isOneToOne = true),
            anEditDefaultNotificationSettingsState(changeNotificationSettingAction = AsyncAction.Loading),
            anEditDefaultNotificationSettingsState(changeNotificationSettingAction = AsyncAction.Failure(RuntimeException("error"))),
            anEditDefaultNotificationSettingsState(displayMentionsOnlyDisclaimer = true),
        )
}

/**
 * 创建示例编辑默认通知设置状态
 *
 * @param isOneToOne 是否为一对一房间
 * @param changeNotificationSettingAction 更改设置操作状态
 * @param displayMentionsOnlyDisclaimer 是否显示仅提及免责声明
 * @return 编辑默认通知设置状态
 */
private fun anEditDefaultNotificationSettingsState(
    isOneToOne: Boolean = false,
    changeNotificationSettingAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    displayMentionsOnlyDisclaimer: Boolean = false,
) = EditDefaultNotificationSettingState(
    isOneToOne = isOneToOne,
    mode = RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY,
    roomsWithUserDefinedMode = persistentListOf(
        anEditNotificationSettingRoomInfo("Room"),
        anEditNotificationSettingRoomInfo(null),
    ),
    changeNotificationSettingAction = changeNotificationSettingAction,
    displayMentionsOnlyDisclaimer = displayMentionsOnlyDisclaimer,
    eventSink = {}
)

/**
 * 创建示例编辑通知设置房间信息
 *
 * @param name 房间名称
 * @return 房间信息
 */
private fun anEditNotificationSettingRoomInfo(
    name: String?,
) = EditNotificationSettingRoomInfo(
    roomId = RoomId("!roomId:domain"),
    name = name,
    avatarData = AvatarData(
        id = "!roomId:domain",
        name = name,
        url = null,
        size = AvatarSize.CustomRoomNotificationSetting,
    ),
    heroesAvatar = persistentListOf(),
    notificationMode = RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY,
)
