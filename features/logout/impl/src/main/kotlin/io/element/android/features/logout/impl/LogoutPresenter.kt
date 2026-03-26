/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runCatchingUpdatingState
import io.element.android.libraries.core.bool.orTrue
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.encryption.BackupState
import io.element.android.libraries.matrix.api.encryption.BackupUploadState
import io.element.android.libraries.matrix.api.encryption.EncryptionService
import io.element.android.libraries.workmanager.api.WorkManagerScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 退出登录界面逻辑控制器
 *
 * 负责管理退出登录界面的业务逻辑，包括：
 * - 监控加密备份上传状态
 * - 判断是否为最后一个设备
 * - 检查密钥备份状态
 * - 处理退出登录操作
 *
 * @property matrixClient Matrix 客户端实例，用于执行退出登录操作
 * @property encryptionService 加密服务，用于获取备份和恢复状态
 * @property workManagerScheduler 工作管理器调度器，用于取消待处理的工作任务
 */
@Inject
class LogoutPresenter(
    private val matrixClient: MatrixClient,
    private val encryptionService: EncryptionService,
    private val workManagerScheduler: WorkManagerScheduler,
) : Presenter<LogoutState> {
    /**
     * 生成退出登录界面的状态
     *
     * @return LogoutState 退出登录界面的当前状态
     */
    @Composable
    override fun present(): LogoutState {
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

        // 是否已等待很长时间（用于显示网络连接提示）
        var waitingForALongTime by remember { mutableStateOf(false) }
        LaunchedEffect(backupUploadState) {
            if (backupUploadState is BackupUploadState.Waiting) {
                delay(2_000)
                waitingForALongTime = true
            } else {
                waitingForALongTime = false
            }
        }

        // 是否为最后一个设备的订阅
        val isLastDevice by encryptionService.isLastDevice.collectAsState()
        // 备份状态的订阅
        val backupState by encryptionService.backupStateStateFlow.collectAsState()
        // 恢复状态的订阅
        val recoveryState by encryptionService.recoveryStateStateFlow.collectAsState()

        // 服务器上是否存在备份的状态
        val doesBackupExistOnServerAction: MutableState<AsyncData<Boolean>> = remember {
            mutableStateOf(AsyncData.Uninitialized)
        }

        // 当备份状态未知时，获取密钥备份状态
        LaunchedEffect(backupState) {
            if (backupState == BackupState.UNKNOWN) {
                getKeyBackupStatus(doesBackupExistOnServerAction)
            }
        }

        /**
         * 处理用户交互事件
         * @param event 退出登录事件
         */
        fun handleEvent(event: LogoutEvents) {
            when (event) {
                is LogoutEvents.Logout -> {
                    // 如果正在确认中或忽略 SDK 错误，则执行退出登录
                    if (logoutAction.value.isConfirming() || event.ignoreSdkError) {
                        localCoroutineScope.logout(logoutAction, event.ignoreSdkError)
                    } else {
                        // 否则显示确认对话框
                        logoutAction.value = AsyncAction.ConfirmingNoParams
                    }
                }
                LogoutEvents.CloseDialogs -> {
                    // 关闭对话框，重置状态
                    logoutAction.value = AsyncAction.Uninitialized
                }
            }
        }

        return LogoutState(
            isLastDevice = isLastDevice,
            backupState = backupState,
            doesBackupExistOnServer = doesBackupExistOnServerAction.value.dataOrNull().orTrue(),
            recoveryState = recoveryState,
            backupUploadState = backupUploadState,
            waitingForALongTime = waitingForALongTime,
            logoutAction = logoutAction.value,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 获取密钥备份状态
     * 检查服务器上是否存在备份
     *
     * @param action 用于更新状态的 MutableState
     */
    private fun CoroutineScope.getKeyBackupStatus(action: MutableState<AsyncData<Boolean>>) = launch {
        suspend {
            encryptionService.doesBackupExistOnServer().getOrThrow()
        }.runCatchingUpdatingState(action)
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
            // 取消任何待处理的工作（如通知同步）
            workManagerScheduler.cancel(matrixClient.sessionId)

            matrixClient.logout(userInitiated = true, ignoreSdkError)
        }.runCatchingUpdatingState(logoutAction)
    }
}
