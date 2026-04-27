/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.reset

import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.encryption.EncryptionService
import io.element.android.libraries.matrix.api.encryption.IdentityResetHandle
import io.element.android.libraries.matrix.api.verification.SessionVerificationService
import io.element.android.libraries.matrix.api.verification.SessionVerifiedStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

private const val resetIdentityTraceTag = "ResetIdentityTrace"

/**
 * 重置身份流程管理器
 *
 * 负责管理身份重置流程的协调工作，包括：
 * - 启动身份重置
 * - 跟踪重置状态
 * - 处理重置完成后的回调
 *
 * @property encryptionService 加密服务
 * @property sessionCoroutineScope 会话协程作用域
 * @property sessionVerificationService 会话验证服务
 */
@Inject
class ResetIdentityFlowManager(
    private val encryptionService: EncryptionService,
    @SessionCoroutineScope private val sessionCoroutineScope: CoroutineScope,
    private val sessionVerificationService: SessionVerificationService,
) {
    /** 重置句柄数据流 */
    private val resetHandleFlow: MutableStateFlow<AsyncData<IdentityResetHandle?>> = MutableStateFlow(AsyncData.Uninitialized)

    /** 当前重置句柄数据流 */
    val currentHandleFlow: StateFlow<AsyncData<IdentityResetHandle?>> = resetHandleFlow

    /** 重置完成等待任务 */
    private var whenResetIsDoneWaitingJob: Job? = null

    /**
     * 设置重置完成后的回调
     *
     * @param block 回调代码块
     */
    fun whenResetIsDone(block: () -> Unit) {
        // 这里记录“开始等待 Verified”的时机，方便判断首次回到 App 后是否根本没收到状态更新。
        Timber.tag(resetIdentityTraceTag).i(
            "ResetIdentityFlowManager.whenResetIsDone register currentVerification=%s currentHandle=%s waitingJobActive=%s",
            sessionVerificationService.sessionVerifiedStatus.value,
            currentHandleFlow.value.debugDescription(),
            whenResetIsDoneWaitingJob?.isActive == true,
        )
        whenResetIsDoneWaitingJob?.cancel()
        whenResetIsDoneWaitingJob = sessionCoroutineScope.launch {
            sessionVerificationService.sessionVerifiedStatus
                .onEach { status ->
                    Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowManager.whenResetIsDone observed verification=%s", status)
                }
                .filterIsInstance<SessionVerifiedStatus.Verified>()
                .first()
            Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowManager.whenResetIsDone observed Verified, executing callback")
            block()
        }
    }

    /**
     * 获取重置句柄
     *
     * 如果已经存在有效的重置句柄，则返回现有数据流；
     * 否则启动新的重置流程。
     *
     * @return 包含重置句柄的异步数据流
     */
    fun getResetHandle(): StateFlow<AsyncData<IdentityResetHandle?>> {
        Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowManager.getResetHandle currentState=%s", resetHandleFlow.value.debugDescription())
        return if (resetHandleFlow.value.isLoading() || resetHandleFlow.value.isSuccess()) {
            Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowManager.getResetHandle reuse existing state")
            resetHandleFlow
        } else {
            resetHandleFlow.value = AsyncData.Loading()
            Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowManager.getResetHandle -> Loading")

            sessionCoroutineScope.launch {
                encryptionService.startIdentityReset()
                    .onSuccess { handle ->
                        Timber.tag(resetIdentityTraceTag).i(
                            "ResetIdentityFlowManager.getResetHandle success handle=%s",
                            handle?.let { it::class.simpleName } ?: "null",
                        )
                        resetHandleFlow.value = AsyncData.Success(handle)
                    }
                    .onFailure {
                        Timber.tag(resetIdentityTraceTag).e(it, "ResetIdentityFlowManager.getResetHandle failed")
                        resetHandleFlow.value = AsyncData.Failure(it)
                    }
            }

            resetHandleFlow
        }
    }

    /**
     * 取消重置流程
     */
    suspend fun cancel() {
        Timber.tag(resetIdentityTraceTag).i(
            "ResetIdentityFlowManager.cancel currentState=%s currentVerification=%s waitingJobActive=%s",
            currentHandleFlow.value.debugDescription(),
            sessionVerificationService.sessionVerifiedStatus.value,
            whenResetIsDoneWaitingJob?.isActive == true,
        )
        currentHandleFlow.value.dataOrNull()?.cancel()
        resetHandleFlow.value = AsyncData.Uninitialized

        whenResetIsDoneWaitingJob?.cancel()
        whenResetIsDoneWaitingJob = null
        Timber.tag(resetIdentityTraceTag).i("ResetIdentityFlowManager.cancel finished")
    }
}

private fun AsyncData<IdentityResetHandle?>.debugDescription(): String = when (this) {
    is AsyncData.Uninitialized -> "Uninitialized"
    is AsyncData.Loading -> "Loading"
    is AsyncData.Success -> "Success(data=${data?.let { it::class.simpleName } ?: "null"})"
    is AsyncData.Failure -> "Failure(error=${error::class.simpleName})"
}
