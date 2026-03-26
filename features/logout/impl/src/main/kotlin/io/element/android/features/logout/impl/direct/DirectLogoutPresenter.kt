/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl.direct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.features.logout.api.direct.DirectLogoutEvents
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.features.logout.impl.tools.isBackingUp
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runCatchingUpdatingState
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.encryption.BackupUploadState
import io.element.android.libraries.matrix.api.encryption.EncryptionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 直接退出登录界面逻辑控制器
 *
 * 负责管理直接退出登录（不经过完整流程）的业务逻辑。
 * 用于在特定场景下快速退出登录，无需关注密钥备份等复杂状态。
 *
 * @property matrixClient Matrix 客户端实例，用于执行退出登录操作
 * @property encryptionService 加密服务，用于获取备份上传状态和设备信息
 */
@Inject
class DirectLogoutPresenter(
    private val matrixClient: MatrixClient,
    private val encryptionService: EncryptionService,
) : Presenter<DirectLogoutState> {
    /**
     * 生成直接退出登录界面的状态
     *
     * @return DirectLogoutState 直接退出登录界面的当前状态
     */
    @Composable
    override fun present(): DirectLogoutState {
        val localCoroutineScope = rememberCoroutineScope()

        // 退出登录操作的异步状态
        val logoutAction: MutableState<AsyncAction<Unit>> = remember {
            mutableStateOf(AsyncAction.Uninitialized)
        }

        // 备份上传状态的流式收集
        val backupUploadState: BackupUploadState by remember {
            encryptionService.waitForBackupUploadSteadyState()
        }
            .collectAsState(initial = BackupUploadState.Unknown)

        // 是否为最后一个设备的订阅
        val isLastDevice by encryptionService.isLastDevice.collectAsState()

        /**
         * 处理用户交互事件
         * @param event 直接退出登录事件
         */
        fun handleEvent(event: DirectLogoutEvents) {
            when (event) {
                is DirectLogoutEvents.Logout -> {
                    // 如果正在确认中或忽略 SDK 错误，则执行退出登录
                    if (logoutAction.value.isConfirming() || event.ignoreSdkError) {
                        localCoroutineScope.logout(logoutAction, event.ignoreSdkError)
                    } else {
                        // 否则显示确认对话框
                        logoutAction.value = AsyncAction.ConfirmingNoParams
                    }
                }
                DirectLogoutEvents.CloseDialogs -> {
                    // 关闭对话框，重置状态
                    logoutAction.value = AsyncAction.Uninitialized
                }
            }
        }

        return DirectLogoutState(
            // 只有不是最后一个设备且备份不在进行中时才允许直接退出
            canDoDirectSignOut = !isLastDevice &&
                !backupUploadState.isBackingUp(),
            logoutAction = logoutAction.value,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 执行退出登录操作
     *
     * @param logoutAction 退出登录操作的异步状态
     * @param ignoreSdkError 是否忽略 SDK 错误
     */
    private fun CoroutineScope.logout(
        logoutAction: MutableState<AsyncAction<Unit>>,
        ignoreSdkError: Boolean,
    ) = launch {
        suspend {
            matrixClient.logout(userInitiated = true, ignoreSdkError)
        }.runCatchingUpdatingState(logoutAction)
    }
}
