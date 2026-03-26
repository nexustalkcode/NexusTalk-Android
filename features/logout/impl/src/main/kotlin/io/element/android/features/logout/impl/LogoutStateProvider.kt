/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.encryption.BackupState
import io.element.android.libraries.matrix.api.encryption.BackupUploadState
import io.element.android.libraries.matrix.api.encryption.RecoveryState
import io.element.android.libraries.matrix.api.encryption.SteadyStateException

/**
 * 退出登录状态提供者
 *
 * 用于在 Compose 预览中提供各种 LogoutState 的示例数据。
 * 继承自 PreviewParameterProvider，用于支持多状态预览。
 */
open class LogoutStateProvider : PreviewParameterProvider<LogoutState> {
    /**
     * 提供预览状态序列
     * 包含各种可能的退出登录状态：
     * - 默认状态
     * - 最后一个设备
     * - 备份上传中
     * - 备份完成
     * - 确认中
     * - 加载中
     * - 失败
     * - 网络异常
     * - 无恢复密钥
     * - 无备份
     * - 等待中
     * - 等待很长时间
     */
    override val values: Sequence<LogoutState>
        get() = sequenceOf(
            aLogoutState(),
            aLogoutState(isLastDevice = true),
            aLogoutState(isLastDevice = false, backupUploadState = BackupUploadState.Uploading(66, 200)),
            aLogoutState(isLastDevice = true, backupUploadState = BackupUploadState.Done),
            aLogoutState(logoutAction = AsyncAction.ConfirmingNoParams),
            aLogoutState(logoutAction = AsyncAction.Loading),
            aLogoutState(logoutAction = AsyncAction.Failure(Exception("Failed to logout"))),
            aLogoutState(backupUploadState = BackupUploadState.SteadyException(SteadyStateException.Connection("No network"))),
            // 最后一个会话无恢复
            aLogoutState(isLastDevice = true, recoveryState = RecoveryState.DISABLED),
            // 最后一个会话无备份
            aLogoutState(isLastDevice = true, backupState = BackupState.UNKNOWN, doesBackupExistOnServer = false),
            aLogoutState(
                isLastDevice = false,
                backupUploadState = BackupUploadState.Waiting,
            ),
            aLogoutState(
                isLastDevice = false,
                backupUploadState = BackupUploadState.Waiting,
                waitingForALongTime = true,
            ),
        )
}

/**
 * 创建 LogoutState 测试数据的辅助函数
 *
 * @param isLastDevice 是否为最后一个设备
 * @param backupState 备份状态
 * @param doesBackupExistOnServer 服务器上是否存在备份
 * @param recoveryState 恢复状态
 * @param backupUploadState 备份上传状态
 * @param waitingForALongTime 是否已等待很长时间
 * @param logoutAction 退出登录操作的异步状态
 * @param eventSink 事件处理函数
 * @return LogoutState 实例
 */
fun aLogoutState(
    isLastDevice: Boolean = false,
    backupState: BackupState = BackupState.ENABLED,
    doesBackupExistOnServer: Boolean = true,
    recoveryState: RecoveryState = RecoveryState.ENABLED,
    backupUploadState: BackupUploadState = BackupUploadState.Unknown,
    waitingForALongTime: Boolean = false,
    logoutAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (LogoutEvents) -> Unit = {},
) = LogoutState(
    isLastDevice = isLastDevice,
    backupState = backupState,
    doesBackupExistOnServer = doesBackupExistOnServer,
    recoveryState = recoveryState,
    backupUploadState = backupUploadState,
    waitingForALongTime = waitingForALongTime,
    logoutAction = logoutAction,
    eventSink = eventSink,
)
