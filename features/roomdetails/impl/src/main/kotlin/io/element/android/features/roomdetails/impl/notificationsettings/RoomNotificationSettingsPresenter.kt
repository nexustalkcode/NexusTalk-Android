/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.notificationsettings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runCatchingUpdatingState
import io.element.android.libraries.core.coroutine.suspendWithMinimumDuration
import io.element.android.libraries.matrix.api.notificationsettings.NotificationSettingsService
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.room.RoomNotificationMode
import io.element.android.libraries.matrix.api.room.RoomNotificationSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * 房间通知设置 Presenter
 *
 * 负责处理房间通知设置页面的业务逻辑。
 * 管理通知模式的获取、设置、保存和观察。
 *
 * @property room 已加入的房间
 * @property notificationSettingsService 通知设置服务
 * @property showUserDefinedSettingStyle 是否显示用户定义设置样式
 * @see Presenter Presenter 基类
 * @see RoomNotificationSettingsState 通知设置状态
 * @see AssistedInject 依赖注入注解
 */
@AssistedInject
class RoomNotificationSettingsPresenter(
    /** 已加入的房间 */
    private val room: JoinedRoom,
    /** 通知设置服务 */
    private val notificationSettingsService: NotificationSettingsService,
    /** 是否显示用户定义设置样式 */
    @Assisted private val showUserDefinedSettingStyle: Boolean,
) : Presenter<RoomNotificationSettingsState> {
    /**
     * Presenter 工厂接口
     *
     * 用于创建 RoomNotificationSettingsPresenter 实例。
     *
     * @see AssistedFactory 辅助工厂注解
     */
    @AssistedFactory
    interface Factory {
        /**
         * 创建通知设置 Presenter
         *
         * @param showUserDefinedSettingStyle 是否显示用户定义设置样式
         * @return RoomNotificationSettingsPresenter 实例
         */
        fun create(showUserDefinedSettingStyle: Boolean): RoomNotificationSettingsPresenter
    }

    @Composable
    override fun present(): RoomNotificationSettingsState {
        var shouldDisplayMentionsOnlyDisclaimer by remember { mutableStateOf(false) }
        val defaultRoomNotificationMode: MutableState<RoomNotificationMode?> = rememberSaveable {
            mutableStateOf(null)
        }
        val localCoroutineScope = rememberCoroutineScope()
        val setNotificationSettingAction: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val restoreDefaultAction: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        val roomNotificationSettings: MutableState<AsyncData<RoomNotificationSettings>> = remember {
            mutableStateOf(AsyncData.Uninitialized)
        }

        // We store state of which mode the user has set via the notification service before the new push settings have been updated.
        // We show this state immediately to the user and debounce updates to notification settings to hide some invalid states returned
        // by the rust sdk during these two events that cause the radio buttons ot toggle quickly back and forth.
        // This is a client side work-around until bulk push rule updates are supported.
        // ref: https://github.com/matrix-org/matrix-spec-proposals/pull/3934
        val pendingRoomNotificationMode: MutableState<RoomNotificationMode?> = remember {
            mutableStateOf(null)
        }

        // We store state of whether the user has set the notifications settings to default or custom via the notification service.
        // We show this state immediately to the user and debounce updates to notification settings to hide some invalid states returned
        // by the rust sdk during these two events that cause the switch ot toggle quickly back and forth.
        // This is a client side work-around until bulk push rule updates are supported.
        // ref: https://github.com/matrix-org/matrix-spec-proposals/pull/3934
        val pendingSetDefault: MutableState<Boolean?> = remember {
            mutableStateOf(null)
        }

        val displayName by produceState(room.info().name) {
            room.roomInfoFlow.collect { value = it.name }
        }

        val isRoomEncrypted by produceState(room.info().isEncrypted) {
            room.roomInfoFlow.collect { value = it.isEncrypted }
        }

        LaunchedEffect(Unit) {
            getDefaultRoomNotificationMode(defaultRoomNotificationMode)
            fetchNotificationSettings(pendingRoomNotificationMode, roomNotificationSettings)
            observeNotificationSettings(pendingRoomNotificationMode, roomNotificationSettings)
        }

        LaunchedEffect(isRoomEncrypted) {
            shouldDisplayMentionsOnlyDisclaimer = isRoomEncrypted == true &&
                !notificationSettingsService.canHomeServerPushEncryptedEventsToDevice().getOrDefault(true)
        }

        fun handleEvent(event: RoomNotificationSettingsEvents) {
            when (event) {
                is RoomNotificationSettingsEvents.ChangeRoomNotificationMode -> {
                    localCoroutineScope.setRoomNotificationMode(event.mode, pendingRoomNotificationMode, pendingSetDefault, setNotificationSettingAction)
                }
                is RoomNotificationSettingsEvents.SetNotificationMode -> {
                    if (event.isDefault) {
                        localCoroutineScope.restoreDefaultRoomNotificationMode(restoreDefaultAction, pendingSetDefault)
                    } else {
                        defaultRoomNotificationMode.value?.let {
                            localCoroutineScope.setRoomNotificationMode(it, pendingRoomNotificationMode, pendingSetDefault, setNotificationSettingAction)
                        }
                    }
                }
                is RoomNotificationSettingsEvents.DeleteCustomNotification -> {
                    localCoroutineScope.restoreDefaultRoomNotificationMode(restoreDefaultAction, pendingSetDefault)
                }
                RoomNotificationSettingsEvents.ClearSetNotificationError -> {
                    setNotificationSettingAction.value = AsyncAction.Uninitialized
                }
                RoomNotificationSettingsEvents.ClearRestoreDefaultError -> {
                    restoreDefaultAction.value = AsyncAction.Uninitialized
                }
            }
        }

        return RoomNotificationSettingsState(
            showUserDefinedSettingStyle = showUserDefinedSettingStyle,
            roomName = displayName.orEmpty(),
            roomNotificationSettings = roomNotificationSettings.value,
            pendingRoomNotificationMode = pendingRoomNotificationMode.value,
            pendingSetDefault = pendingSetDefault.value,
            defaultRoomNotificationMode = defaultRoomNotificationMode.value,
            setNotificationSettingAction = setNotificationSettingAction.value,
            restoreDefaultAction = restoreDefaultAction.value,
            displayMentionsOnlyDisclaimer = shouldDisplayMentionsOnlyDisclaimer,
            eventSink = ::handleEvent,
        )
    }

    @OptIn(FlowPreview::class)
    private fun CoroutineScope.observeNotificationSettings(
        pendingModeState: MutableState<RoomNotificationMode?>,
        roomNotificationSettings: MutableState<AsyncData<RoomNotificationSettings>>
    ) {
        notificationSettingsService.notificationSettingsChangeFlow
            .debounce(0.5.seconds)
            .onEach {
                fetchNotificationSettings(pendingModeState, roomNotificationSettings)
            }
            .launchIn(this)
    }

    private fun CoroutineScope.fetchNotificationSettings(
        pendingModeState: MutableState<RoomNotificationMode?>,
        roomNotificationSettings: MutableState<AsyncData<RoomNotificationSettings>>
    ) = launch {
        suspend {
            val isEncrypted = room.info().isEncrypted ?: room.getUpdatedIsEncrypted().getOrThrow()
            pendingModeState.value = null
            notificationSettingsService.getRoomNotificationSettings(room.roomId, isEncrypted, room.isOneToOne).getOrThrow()
        }.runCatchingUpdatingState(roomNotificationSettings)
    }

    private fun CoroutineScope.getDefaultRoomNotificationMode(
        defaultRoomNotificationMode: MutableState<RoomNotificationMode?>
    ) = launch {
        val isEncrypted = room.info().isEncrypted ?: room.getUpdatedIsEncrypted().getOrThrow()
        defaultRoomNotificationMode.value = notificationSettingsService.getDefaultRoomNotificationMode(
            isEncrypted,
            room.isOneToOne
        ).getOrThrow()
    }

    private fun CoroutineScope.setRoomNotificationMode(
        mode: RoomNotificationMode,
        pendingModeState: MutableState<RoomNotificationMode?>,
        pendingDefaultState: MutableState<Boolean?>,
        action: MutableState<AsyncAction<Unit>>
    ) = launch {
        suspendWithMinimumDuration {
            pendingModeState.value = mode
            pendingDefaultState.value = false
            val result = notificationSettingsService.setRoomNotificationMode(room.roomId, mode)
            if (result.isFailure) {
                pendingModeState.value = null
                pendingDefaultState.value = null
            }
            result.getOrThrow()
        }.runCatchingUpdatingState(action)
    }

    private fun CoroutineScope.restoreDefaultRoomNotificationMode(
        action: MutableState<AsyncAction<Unit>>,
        pendingDefaultState: MutableState<Boolean?>
    ) = launch {
        suspendWithMinimumDuration {
            pendingDefaultState.value = true
            val result = notificationSettingsService.restoreDefaultRoomNotificationMode(room.roomId)
            if (result.isFailure) {
                pendingDefaultState.value = null
            }
            result.getOrThrow()
        }.runCatchingUpdatingState(action)
    }
}
