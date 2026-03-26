/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.sessionverification.choosemode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.zacsweers.metro.Inject
import io.element.android.features.logout.api.direct.DirectLogoutEvents
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.coroutine.mapState
import io.element.android.libraries.matrix.api.encryption.EncryptionService
import io.element.android.libraries.matrix.api.encryption.RecoveryState

/**
 * 选择自验证方式 Presenter
 *
 * 负责处理首次用户体验中选择会话验证方式的业务逻辑和状态管理。
 * 确定用户可用的验证选项（使用另一台设备或输入恢复密钥）。
 *
 * @property encryptionService 加密服务
 * @property directLogoutPresenter 直接退出登录 Presenter
 */
@Inject
class ChooseSelfVerificationModePresenter(
    private val encryptionService: EncryptionService,
    private val directLogoutPresenter: Presenter<DirectLogoutState>,
) : Presenter<ChooseSelfVerificationModeState> {
    /**
     * 生成界面状态
     *
     * @return ChooseSelfVerificationModeState 选择自验证方式状态
     */
    @Composable
    override fun present(): ChooseSelfVerificationModeState {
        val hasDevicesToVerifyAgainst by encryptionService.hasDevicesToVerifyAgainst.collectAsState()
        val canEnterRecoveryKey by encryptionService.recoveryStateStateFlow
            .mapState { recoveryState ->
                when (recoveryState) {
                    RecoveryState.WAITING_FOR_SYNC,
                    RecoveryState.UNKNOWN -> AsyncData.Loading()
                    RecoveryState.INCOMPLETE -> AsyncData.Success(true)
                    RecoveryState.ENABLED,
                    RecoveryState.DISABLED -> AsyncData.Success(false)
                }
            }
            .collectAsState()
        val buttonsState by remember {
            derivedStateOf {
                val canUseAnotherDevice = hasDevicesToVerifyAgainst.dataOrNull()
                val canEnterRecoveryKey = canEnterRecoveryKey.dataOrNull()
                if (canUseAnotherDevice == null || canEnterRecoveryKey == null) {
                    AsyncData.Loading()
                } else {
                    AsyncData.Success(
                        ChooseSelfVerificationModeState.ButtonsState(
                            canUseAnotherDevice = canUseAnotherDevice,
                            canEnterRecoveryKey = canEnterRecoveryKey,
                        )
                    )
                }
            }
        }

        val directLogoutState = directLogoutPresenter.present()

        /**
         * 处理用户事件
         *
         * @param event 选择自验证方式事件
         */
        fun handleEvent(event: ChooseSelfVerificationModeEvent) {
            when (event) {
                ChooseSelfVerificationModeEvent.SignOut -> directLogoutState.eventSink(DirectLogoutEvents.Logout(ignoreSdkError = false))
            }
        }

        return ChooseSelfVerificationModeState(
            buttonsState = buttonsState,
            directLogoutState = directLogoutState,
            eventSink = ::handleEvent,
        )
    }
}
