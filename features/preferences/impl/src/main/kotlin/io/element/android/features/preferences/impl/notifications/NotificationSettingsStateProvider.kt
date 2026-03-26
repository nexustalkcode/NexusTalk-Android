/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.notifications

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import io.element.android.libraries.fullscreenintent.api.aFullScreenIntentPermissionsState
import io.element.android.libraries.matrix.api.room.RoomNotificationMode
import io.element.android.libraries.pushproviders.api.Distributor
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * 通知设置状态提供者
 *
 * 用于在预览模式下提供通知设置页面的示例状态数据。
 *
 * @see NotificationSettingsState 通知设置状态
 */
open class NotificationSettingsStateProvider : PreviewParameterProvider<NotificationSettingsState> {
    override val values: Sequence<NotificationSettingsState>
        get() = sequenceOf(
            aValidNotificationSettingsState(systemNotificationsEnabled = false),
            aValidNotificationSettingsState(),
            aValidNotificationSettingsState(changeNotificationSettingAction = AsyncAction.Loading),
            aValidNotificationSettingsState(changeNotificationSettingAction = AsyncAction.Failure(RuntimeException("error"))),
            aValidNotificationSettingsState(
                availablePushDistributors = listOf(aDistributor("Firebase")),
                changeNotificationSettingAction = AsyncAction.Failure(RuntimeException("error")),
            ),
            aValidNotificationSettingsState(availablePushDistributors = listOf(aDistributor("Firebase"))),
            aValidNotificationSettingsState(showChangePushProviderDialog = true),
            aValidNotificationSettingsState(
                availablePushDistributors = listOf(
                    aDistributor("Firebase"),
                    aDistributor("ntfy", "app.id1"),
                    aDistributor("ntfy", "app.id2"),
                ),
                showChangePushProviderDialog = true,
            ),
            aValidNotificationSettingsState(currentPushDistributor = AsyncData.Loading()),
            aValidNotificationSettingsState(currentPushDistributor = AsyncData.Failure(Exception("Failed to change distributor"))),
            aInvalidNotificationSettingsState(),
            aInvalidNotificationSettingsState(fixFailed = true),
            aValidNotificationSettingsState(fullScreenIntentPermissionsState = aFullScreenIntentPermissionsState(permissionGranted = false)),
            aValidNotificationSettingsState(appNotificationEnabled = false),
        )
}

/**
 * 创建有效的通知设置状态示例
 *
 * @param changeNotificationSettingAction 更改设置操作状态
 * @param atRoomNotificationsEnabled 房间提及通知是否启用
 * @param callNotificationsEnabled 通话通知是否启用
 * @param inviteForMeNotificationsEnabled 邀请通知是否启用
 * @param systemNotificationsEnabled 系统通知是否启用
 * @param appNotificationEnabled 应用通知是否启用
 * @param currentPushDistributor 当前推送分发器
 * @param availablePushDistributors 可用推送分发器列表
 * @param showChangePushProviderDialog 是否显示更改推送提供商对话框
 * @param fullScreenIntentPermissionsState 全屏意图权限状态
 * @param eventSink 事件处理函数
 * @return 有效的通知设置状态
 */
fun aValidNotificationSettingsState(
    changeNotificationSettingAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    atRoomNotificationsEnabled: Boolean = true,
    callNotificationsEnabled: Boolean = true,
    inviteForMeNotificationsEnabled: Boolean = true,
    systemNotificationsEnabled: Boolean = true,
    appNotificationEnabled: Boolean = true,
    currentPushDistributor: AsyncData<Distributor> = AsyncData.Success(aDistributor("Firebase")),
    availablePushDistributors: List<Distributor> = listOf(
        aDistributor("Firebase"),
        aDistributor("ntfy"),
    ),
    showChangePushProviderDialog: Boolean = false,
    fullScreenIntentPermissionsState: FullScreenIntentPermissionsState = aFullScreenIntentPermissionsState(),
    eventSink: (NotificationSettingsEvents) -> Unit = {},
) = NotificationSettingsState(
    matrixSettings = NotificationSettingsState.MatrixSettings.Valid(
        atRoomNotificationsEnabled = atRoomNotificationsEnabled,
        callNotificationsEnabled = callNotificationsEnabled,
        inviteForMeNotificationsEnabled = inviteForMeNotificationsEnabled,
        defaultGroupNotificationMode = RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY,
        defaultOneToOneNotificationMode = RoomNotificationMode.ALL_MESSAGES,
    ),
    appSettings = NotificationSettingsState.AppSettings(
        systemNotificationsEnabled = systemNotificationsEnabled,
        appNotificationsEnabled = appNotificationEnabled,
    ),
    changeNotificationSettingAction = changeNotificationSettingAction,
    currentPushDistributor = currentPushDistributor,
    availablePushDistributors = availablePushDistributors.toImmutableList(),
    showChangePushProviderDialog = showChangePushProviderDialog,
    fullScreenIntentPermissionsState = fullScreenIntentPermissionsState,
    eventSink = eventSink,
)

/**
 * 创建无效的通知设置状态示例
 *
 * @param fixFailed 修复是否失败
 * @param eventSink 事件处理函数
 * @return 无效的通知设置状态
 */
fun aInvalidNotificationSettingsState(
    fixFailed: Boolean = false,
    eventSink: (NotificationSettingsEvents) -> Unit = {},
) = NotificationSettingsState(
    matrixSettings = NotificationSettingsState.MatrixSettings.Invalid(
        fixFailed = fixFailed,
    ),
    appSettings = NotificationSettingsState.AppSettings(
        systemNotificationsEnabled = false,
        appNotificationsEnabled = true,
    ),
    changeNotificationSettingAction = AsyncAction.Uninitialized,
    currentPushDistributor = AsyncData.Uninitialized,
    availablePushDistributors = persistentListOf(),
    showChangePushProviderDialog = false,
    fullScreenIntentPermissionsState = aFullScreenIntentPermissionsState(),
    eventSink = eventSink,
)

/**
 * 创建示例推送分发器
 *
 * @param name 分发器名称
 * @param value 分发器值
 * @return 推送分发器
 */
fun aDistributor(
    name: String = "Name",
    value: String = "$name Value",
) = Distributor(
    value = value,
    name = name,
)
