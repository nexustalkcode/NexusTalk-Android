/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.userprofile.impl.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.features.enterprise.api.SessionEnterpriseService
import io.element.android.features.startchat.api.StartDMAction
import io.element.android.features.userprofile.api.UserProfileEvents
import io.element.android.features.userprofile.api.UserProfileState
import io.element.android.features.userprofile.api.UserProfileState.ConfirmationDialog
import io.element.android.features.userprofile.api.UserProfileVerificationState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.bool.orFalse
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.room.powerlevels.canCall
import io.element.android.libraries.matrix.api.room.powerlevels.use
import io.element.android.libraries.matrix.api.user.MatrixUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AssistedInject
/**
 * 用户资料页 Presenter。
 *
 * 负责拉取指定用户的资料、DM 房间、可通话状态和拉黑状态，
 * 并把页面事件转换成对应的业务动作。
 */
class UserProfilePresenter(
    @Assisted private val userId: UserId,
    private val client: MatrixClient,
    private val startDMAction: StartDMAction,
    private val sessionEnterpriseService: SessionEnterpriseService,
    private val featureFlagService: FeatureFlagService,
) : Presenter<UserProfileState> {
    /**
     * 创建 [UserProfilePresenter] 的 Assisted 工厂。
     */
    @AssistedFactory
    interface Factory {
        fun create(userId: UserId): UserProfilePresenter
    }

    /**
     * 获取与当前用户对应的私聊房间 ID。
     */
    @Composable
    private fun getDmRoomId(): State<RoomId?> {
        return produceState(initialValue = null) {
            value = client.findDM(userId).getOrNull()
        }
    }

    /**
     * 计算当前资料页是否允许直接发起通话。
     *
     * @param roomId 当前私聊房间 ID；如果还没有私聊房间则不可直接通话。
     */
    @Composable
    private fun getCanCall(roomId: RoomId?): State<Boolean> {
        val isVideoCallEnabled by remember {
            featureFlagService.isFeatureEnabledFlow(FeatureFlags.VideoCall)
        }.collectAsState(initial = true)
        val isElementCallAvailable by produceState(initialValue = false, roomId) {
            value = sessionEnterpriseService.isElementCallAvailable()
        }
        return produceState(initialValue = false, isVideoCallEnabled, isElementCallAvailable, roomId) {
            value = when {
                isVideoCallEnabled.not() -> false
                isElementCallAvailable.not() -> false
                client.isMe(userId) -> false
                else ->
                    roomId
                        ?.let { client.getRoom(it) }
                        ?.use { room ->
                            room.roomPermissions().use(false) { perms -> perms.canCall() }
                        }
                        .orFalse()
            }
        }
    }

    /**
     * 生成用户资料页状态并处理页面事件。
     */
    @Composable
    override fun present(): UserProfileState {
        val coroutineScope = rememberCoroutineScope()
        val isCurrentUser = remember { client.isMe(userId) }
        var confirmationDialog by remember { mutableStateOf<ConfirmationDialog?>(null) }
        val startDmActionState: MutableState<AsyncAction<RoomId>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val isBlocked: MutableState<AsyncData<Boolean>> = remember { mutableStateOf(AsyncData.Uninitialized) }
        val dmRoomId by getDmRoomId()
        val canCall by getCanCall(dmRoomId)
        LaunchedEffect(Unit) {
            client.ignoredUsersFlow
                .map { ignoredUsers -> userId in ignoredUsers }
                .distinctUntilChanged()
                .onEach { isBlocked.value = AsyncData.Success(it) }
                .launchIn(this)
        }
        val userProfile by produceState<MatrixUser?>(null) { value = client.getProfile(userId).getOrNull() }

        fun handleEvent(event: UserProfileEvents) {
            when (event) {
                is UserProfileEvents.BlockUser -> {
                    if (event.needsConfirmation) {
                        confirmationDialog = ConfirmationDialog.Block
                    } else {
                        confirmationDialog = null
                        coroutineScope.blockUser(isBlocked)
                    }
                }
                is UserProfileEvents.UnblockUser -> {
                    if (event.needsConfirmation) {
                        confirmationDialog = ConfirmationDialog.Unblock
                    } else {
                        confirmationDialog = null
                        coroutineScope.unblockUser(isBlocked)
                    }
                }
                UserProfileEvents.ClearConfirmationDialog -> confirmationDialog = null
                UserProfileEvents.ClearBlockUserError -> {
                    isBlocked.value = AsyncData.Success(isBlocked.value.dataOrNull().orFalse())
                }
                UserProfileEvents.StartDM -> {
                    coroutineScope.launch {
                        startDMAction.execute(
                            matrixUser = userProfile ?: MatrixUser(userId),
                            createIfDmDoesNotExist = startDmActionState.value is AsyncAction.Confirming,
                            actionState = startDmActionState,
                        )
                    }
                }
                UserProfileEvents.ClearStartDMState -> {
                    startDmActionState.value = AsyncAction.Uninitialized
                }
                // Do nothing for other event as they are handled by the RoomMemberDetailsPresenter if needed
                UserProfileEvents.WithdrawVerification,
                is UserProfileEvents.CopyToClipboard -> Unit
            }
        }

        return UserProfileState(
            userId = userId,
            userName = userProfile?.displayName,
            avatarUrl = userProfile?.avatarUrl,
            isBlocked = isBlocked.value,
            verificationState = UserProfileVerificationState.UNKNOWN,
            startDmActionState = startDmActionState.value,
            displayConfirmationDialog = confirmationDialog,
            isCurrentUser = isCurrentUser,
            dmRoomId = dmRoomId,
            canCall = canCall,
            snackbarMessage = null,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 拉黑当前资料页对应的用户。
     *
     * @param isBlockedState 用于回写 UI 拉黑状态的状态容器。
     */
    private fun CoroutineScope.blockUser(
        isBlockedState: MutableState<AsyncData<Boolean>>,
    ) = launch {
        isBlockedState.value = AsyncData.Loading(false)
        client.ignoreUser(userId)
            .onFailure {
                isBlockedState.value = AsyncData.Failure(it, false)
            }
        // Note: on success, ignoredUsersFlow will emit new item.
    }

    /**
     * 取消拉黑当前资料页对应的用户。
     *
     * @param isBlockedState 用于回写 UI 拉黑状态的状态容器。
     */
    private fun CoroutineScope.unblockUser(
        isBlockedState: MutableState<AsyncData<Boolean>>,
    ) = launch {
        isBlockedState.value = AsyncData.Loading(true)
        client.unignoreUser(userId)
            .onFailure {
                isBlockedState.value = AsyncData.Failure(it, true)
            }
        // Note: on success, ignoredUsersFlow will emit new item.
    }
}
