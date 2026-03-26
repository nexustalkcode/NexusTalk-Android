/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.members.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.features.userprofile.api.UserProfileEvents
import io.element.android.features.userprofile.api.UserProfilePresenterFactory
import io.element.android.features.userprofile.api.UserProfileState
import io.element.android.features.userprofile.api.UserProfileVerificationState
import io.element.android.libraries.androidutils.clipboard.ClipboardHelper
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.designsystem.utils.snackbar.LocalSnackbarDispatcher
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.designsystem.utils.snackbar.collectSnackbarMessageAsState
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.encryption.EncryptionService
import io.element.android.libraries.matrix.api.encryption.identity.IdentityState
import io.element.android.libraries.matrix.api.encryption.identity.IdentityStateChange
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.ui.room.getRoomMemberAsState
import io.element.android.libraries.matrix.ui.room.roomMemberIdentityStateChange
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 房间成员详情 Presenter
 *
 * 负责处理房间成员详情页面的业务逻辑。
 * 依赖于 UserProfilePresenter，但会使用房间成员信息覆盖某些字段。
 * 负责获取房间成员信息、加密身份验证状态和用户资料。
 *
 * @property roomMemberId 房间成员的 UserID
 * @property room 已加入的房间
 * @property encryptionService 加密服务
 * @property clipboardHelper 剪贴板助手
 * @property userProfilePresenterFactory 用户资料 Presenter 工厂
 * @see Presenter Presenter 基类
 * @see UserProfileState 用户资料状态
 * @see AssistedInject 依赖注入注解
 */
@AssistedInject
class RoomMemberDetailsPresenter(
    /** 房间成员的 UserID */
    @Assisted private val roomMemberId: UserId,
    /** 已加入的房间 */
    private val room: JoinedRoom,
    /** 加密服务 */
    private val encryptionService: EncryptionService,
    /** 剪贴板助手 */
    private val clipboardHelper: ClipboardHelper,
    /** 用户资料 Presenter 工厂 */
    userProfilePresenterFactory: UserProfilePresenterFactory,
) : Presenter<UserProfileState> {
    /**
     * Presenter 工厂接口
     *
     * 用于创建 RoomMemberDetailsPresenter 实例。
     */
    interface Factory {
        /**
         * 创建房间成员详情 Presenter
         *
         * @param roomMemberId 房间成员的 UserID
         * @return RoomMemberDetailsPresenter 实例
         */
        fun create(roomMemberId: UserId): RoomMemberDetailsPresenter
    }

    /** 用户资料 Presenter 实例 */
    private val userProfilePresenter = userProfilePresenterFactory.create(roomMemberId)

    @Composable
    override fun present(): UserProfileState {
        val coroutineScope = rememberCoroutineScope()

        val snackbarDispatcher = LocalSnackbarDispatcher.current
        val snackbarMessage by snackbarDispatcher.collectSnackbarMessageAsState()
        val roomMember by room.getRoomMemberAsState(roomMemberId)
        LaunchedEffect(Unit) {
            // Update room member info when opening this screen
            // We don't need to assign the result as it will be automatically propagated by `room.getRoomMemberAsState`
            room.getUpdatedMember(roomMemberId)
        }

        val roomUserName: String? by produceState(
            initialValue = roomMember?.displayName,
            key1 = roomMember,
        ) {
            value = room.userDisplayName(roomMemberId).getOrNull() ?: roomMember?.displayName
        }

        val roomUserAvatar: String? by produceState(
            initialValue = roomMember?.avatarUrl,
            key1 = roomMember,
        ) {
            value = room.userAvatarUrl(roomMemberId).getOrNull() ?: roomMember?.avatarUrl
        }

        val userProfileState = userProfilePresenter.present()

        val identityStateChanges = produceState<IdentityStateChange?>(initialValue = null) {
            // Fetch the initial identity state manually
            val identityState = encryptionService.getUserIdentity(roomMemberId).getOrNull()
            value = identityState?.let { IdentityStateChange(roomMemberId, it) }

            // Subscribe to the identity changes
            room.roomMemberIdentityStateChange(waitForEncryption = false)
                .map { it.find { it.identityRoomMember.userId == roomMemberId } }
                .map { roomMemberIdentityStateChange ->
                    // If we didn't receive any info, manually fetch it
                    roomMemberIdentityStateChange?.identityState ?: encryptionService.getUserIdentity(roomMemberId).getOrNull()
                }
                .filterNotNull()
                .collect { value = IdentityStateChange(roomMemberId, it) }
        }

        val verificationState by remember {
            derivedStateOf {
                when (identityStateChanges.value?.identityState) {
                    IdentityState.VerificationViolation -> UserProfileVerificationState.VERIFICATION_VIOLATION
                    IdentityState.Verified -> UserProfileVerificationState.VERIFIED
                    IdentityState.Pinned, IdentityState.PinViolation -> UserProfileVerificationState.UNVERIFIED
                    else -> UserProfileVerificationState.UNKNOWN
                }
            }
        }

        fun handleEvent(event: UserProfileEvents) {
            when (event) {
                UserProfileEvents.WithdrawVerification -> coroutineScope.launch {
                    encryptionService.withdrawVerification(roomMemberId)
                }
                is UserProfileEvents.CopyToClipboard -> {
                    clipboardHelper.copyPlainText(event.text)
                    snackbarDispatcher.post(SnackbarMessage(CommonStrings.common_copied_to_clipboard))
                }
                else -> userProfileState.eventSink(event)
            }
        }

        return userProfileState.copy(
            userName = roomUserName ?: userProfileState.userName,
            avatarUrl = roomUserAvatar ?: userProfileState.avatarUrl,
            verificationState = verificationState,
            snackbarMessage = snackbarMessage,
            eventSink = ::handleEvent,
        )
    }
}
