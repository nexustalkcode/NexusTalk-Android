/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.notifications

/**
 * 通知设置事件密封接口
 *
 * 定义通知设置页面中可能发生的各种用户交互事件。
 */
sealed interface NotificationSettingsEvents {
    /** 刷新系统通知启用状态 */
    data object RefreshSystemNotificationsEnabled : NotificationSettingsEvents
    /** 设置通知是否启用 */
    data class SetNotificationsEnabled(val enabled: Boolean) : NotificationSettingsEvents
    /** 设置房间提及通知是否启用 */
    data class SetAtRoomNotificationsEnabled(val enabled: Boolean) : NotificationSettingsEvents
    /** 设置通话通知是否启用 */
    data class SetCallNotificationsEnabled(val enabled: Boolean) : NotificationSettingsEvents
    /** 设置邀请我通知是否启用 */
    data class SetInviteForMeNotificationsEnabled(val enabled: Boolean) : NotificationSettingsEvents
    /** 修复配置不匹配问题 */
    data object FixConfigurationMismatch : NotificationSettingsEvents
    /** 清除配置不匹配错误 */
    data object ClearConfigurationMismatchError : NotificationSettingsEvents
    /** 清除通知更改错误 */
    data object ClearNotificationChangeError : NotificationSettingsEvents
    /** 更改推送提供商 */
    data object ChangePushProvider : NotificationSettingsEvents
    /** 取消更改推送提供商 */
    data object CancelChangePushProvider : NotificationSettingsEvents
    /** 设置推送提供商 */
    data class SetPushProvider(val index: Int) : NotificationSettingsEvents
}
