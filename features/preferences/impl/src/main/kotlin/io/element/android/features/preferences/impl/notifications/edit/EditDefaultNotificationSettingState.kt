/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.notifications.edit

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.room.RoomNotificationMode
import kotlinx.collections.immutable.ImmutableList

/**
 * 编辑默认通知设置状态数据类
 *
 * @property isOneToOne 是否为一对一房间
 * @property mode 当前通知模式
 * @property roomsWithUserDefinedMode 具有用户自定义通知模式的房间列表
 * @property changeNotificationSettingAction 更改通知设置的操作状态
 * @property displayMentionsOnlyDisclaimer 是否显示仅提及免责声明
 * @property eventSink 事件处理函数
 */
data class EditDefaultNotificationSettingState(
    val isOneToOne: Boolean,
    val mode: RoomNotificationMode?,
    val roomsWithUserDefinedMode: ImmutableList<EditNotificationSettingRoomInfo>,
    val changeNotificationSettingAction: AsyncAction<Unit>,
    val displayMentionsOnlyDisclaimer: Boolean,
    val eventSink: (EditDefaultNotificationSettingStateEvents) -> Unit,
)
