/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.notificationsettings

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.room.RoomNotificationMode
import io.element.android.libraries.matrix.api.room.RoomNotificationSettings

/**
 * 房间通知设置状态数据类
 *
 * 表示房间通知设置界面的当前状态，包含通知模式的配置和操作状态。
 *
 * @property showUserDefinedSettingStyle 是否显示用户定义设置样式
 * @property roomName 房间名称
 * @property roomNotificationSettings 房间通知设置的异步数据
 * @property pendingRoomNotificationMode 待应用的通知模式
 * @property pendingSetDefault 是否设置为默认通知设置
 * @property defaultRoomNotificationMode 默认通知模式
 * @property setNotificationSettingAction 设置通知操作的状态
 * @property restoreDefaultAction 恢复默认操作的状态
 * @property displayMentionsOnlyDisclaimer 是否显示仅提及免责声明
 * @property eventSink 事件处理函数
 */
data class RoomNotificationSettingsState(
    val showUserDefinedSettingStyle: Boolean,
    val roomName: String,
    val roomNotificationSettings: AsyncData<RoomNotificationSettings>,
    val pendingRoomNotificationMode: RoomNotificationMode?,
    val pendingSetDefault: Boolean?,
    val defaultRoomNotificationMode: RoomNotificationMode?,
    val setNotificationSettingAction: AsyncAction<Unit>,
    val restoreDefaultAction: AsyncAction<Unit>,
    val displayMentionsOnlyDisclaimer: Boolean,
    val eventSink: (RoomNotificationSettingsEvents) -> Unit
)

/**
 * 获取当前显示的通知模式
 *
 * 优先显示待应用的通知模式，否则显示已保存的通知设置。
 */
val RoomNotificationSettingsState.displayNotificationMode: RoomNotificationMode? get() {
    return pendingRoomNotificationMode ?: roomNotificationSettings.dataOrNull()?.mode
}

/**
 * 获取当前是否为默认设置
 *
 * 优先显示待应用的设置状态，否则显示已保存的设置状态。
 */
val RoomNotificationSettingsState.displayIsDefault: Boolean? get() {
    return pendingSetDefault ?: roomNotificationSettings.dataOrNull()?.isDefault
}
