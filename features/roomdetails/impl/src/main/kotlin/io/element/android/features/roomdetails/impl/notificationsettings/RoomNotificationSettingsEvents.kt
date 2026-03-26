/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.notificationsettings

import io.element.android.libraries.matrix.api.room.RoomNotificationMode

/**
 * 房间通知设置事件密封接口
 *
 * 定义房间通知设置页面可能发生的事件。
 * 使用密封接口确保类型安全，只能创建预定义的子类型。
 *
 * @see RoomNotificationMode 房间通知模式
 */
sealed interface RoomNotificationSettingsEvents {
    /**
     * 更改房间通知模式
     *
     * 用户选择不同的通知模式（如全部消息、仅提及和关键词、静音）。
     *
     * @property mode 通知模式
     */
    data class ChangeRoomNotificationMode(val mode: RoomNotificationMode) : RoomNotificationSettingsEvents

    /**
     * 设置通知模式
     *
     * 用户切换是否使用自定义通知设置或默认设置。
     *
     * @property isDefault 是否使用默认设置
     */
    data class SetNotificationMode(val isDefault: Boolean) : RoomNotificationSettingsEvents

    /**
     * 删除自定义通知设置
     *
     * 用户删除自定义通知设置，恢复使用默认设置。
     */
    data object DeleteCustomNotification : RoomNotificationSettingsEvents

    /**
     * 清除设置通知错误
     *
     * 清除设置通知模式时产生的错误状态。
     */
    data object ClearSetNotificationError : RoomNotificationSettingsEvents

    /**
     * 清除恢复默认错误
     *
     * 清除恢复默认设置时产生的错误状态。
     */
    data object ClearRestoreDefaultError : RoomNotificationSettingsEvents
}
