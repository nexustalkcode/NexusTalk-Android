/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.notifications

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import io.element.android.libraries.matrix.api.room.RoomNotificationMode
import io.element.android.libraries.pushproviders.api.Distributor
import kotlinx.collections.immutable.ImmutableList

/**
 * 通知设置页面状态数据类
 *
 * @property matrixSettings Matrix 通知设置状态（未初始化、有效或无效）
 * @property appSettings 应用通知设置状态
 * @property changeNotificationSettingAction 更改通知设置的操作状态
 * @property currentPushDistributor 当前推送分发器
 * @property availablePushDistributors 可用的推送分发器列表
 * @property showChangePushProviderDialog 是否显示更改推送提供商对话框
 * @property fullScreenIntentPermissionsState 全屏意图权限状态
 * @property eventSink 事件处理函数
 */
data class NotificationSettingsState(
    val matrixSettings: MatrixSettings,
    val appSettings: AppSettings,
    val changeNotificationSettingAction: AsyncAction<Unit>,
    val currentPushDistributor: AsyncData<Distributor>,
    val availablePushDistributors: ImmutableList<Distributor>,
    val showChangePushProviderDialog: Boolean,
    val fullScreenIntentPermissionsState: FullScreenIntentPermissionsState,
    val eventSink: (NotificationSettingsEvents) -> Unit,
) {
    /**
     * Matrix 通知设置密封接口
     */
    sealed interface MatrixSettings {
        /** 未初始化状态 */
        data object Uninitialized : MatrixSettings

        /**
         * 有效的设置状态
         *
         * @property atRoomNotificationsEnabled 房间内提及通知是否启用
         * @property callNotificationsEnabled 通话通知是否启用
         * @property inviteForMeNotificationsEnabled 邀请我通知是否启用
         * @property defaultGroupNotificationMode 默认群组通知模式
         * @property defaultOneToOneNotificationMode 默认一对一通知模式
         */
        data class Valid(
            val atRoomNotificationsEnabled: Boolean,
            val callNotificationsEnabled: Boolean,
            val inviteForMeNotificationsEnabled: Boolean,
            val defaultGroupNotificationMode: RoomNotificationMode?,
            val defaultOneToOneNotificationMode: RoomNotificationMode?,
        ) : MatrixSettings

        /**
         * 无效的设置状态（配置不匹配）
         *
         * @property fixFailed 修复是否失败
         */
        data class Invalid(
            val fixFailed: Boolean
        ) : MatrixSettings
    }

    /**
     * 应用通知设置数据类
     *
     * @property systemNotificationsEnabled 系统通知是否启用
     * @property appNotificationsEnabled 应用通知是否启用
     */
    data class AppSettings(
        val systemNotificationsEnabled: Boolean,
        val appNotificationsEnabled: Boolean,
    )

    /**
     * 是否显示高级设置
     * 当当前推送分发器处于失败状态或可用推送分发器数量大于1时显示
     */
    val showAdvancedSettings: Boolean = currentPushDistributor.isFailure() || availablePushDistributors.size > 1
}
