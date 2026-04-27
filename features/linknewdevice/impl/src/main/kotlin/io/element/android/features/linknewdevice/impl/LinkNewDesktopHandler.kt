/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl

import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.element.android.libraries.core.log.logger.LoggerTag
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.linknewdevice.LinkDesktopHandler
import io.element.android.libraries.matrix.api.linknewdevice.LinkDesktopStep
import io.element.android.libraries.matrix.api.logs.LoggerTags
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

private val loggerTag = LoggerTag("LinkNewDesktopHandler", LoggerTags.linkNewDevice)

@Inject
@SingleIn(SessionScope::class)
/**
 * 管理“把桌面端设备关联到当前会话”的底层流程。
 *
 * 该处理器包装 Matrix SDK 提供的 desktop linking handler，
 * 并把步骤状态转发为 UI 层更容易订阅的 [stepFlow]。
 */
class LinkNewDesktopHandler(
    private val matrixClient: MatrixClient,
) {
    private val sessionScope = matrixClient.sessionCoroutineScope
    private val linkDesktopStepFlow = MutableStateFlow<LinkDesktopStep>(
        LinkDesktopStep.Uninitialized
    )

    val stepFlow: StateFlow<LinkDesktopStep>
        get() = linkDesktopStepFlow.asStateFlow()

    private var currentJob: Job? = null
    private var handler: LinkDesktopHandler? = null

    /**
     * 重新创建桌面端关联处理器实例。
     *
     * 会先取消之前的观察任务，避免旧 handler 继续推送状态。
     */
    fun createNewHandler() {
        // 创建前先取消旧任务，避免重复监听
        currentJob?.cancel()
        currentJob = null
        handler = matrixClient.createLinkDesktopHandler().getOrNull()
    }

    /**
     * 取消当前流程并恢复到未初始化状态。
     */
    fun reset() {
        // 重置状态并清理当前任务
        currentJob?.cancel()
        currentJob = null
        sessionScope.launch {
            linkDesktopStepFlow.emit(LinkDesktopStep.Uninitialized)
        }
    }

    /**
     * 处理扫描到的桌面端二维码数据。
     *
     * @param data 扫码得到的原始字节数组。
     */
    fun onScannedCode(data: ByteArray) {
        // 扫码后触发桌面端流程
        currentJob?.cancel()
        currentJob = null
        val currentHandler = handler
        if (currentHandler == null) {
            // 未初始化时给出日志提示
            Timber.tag(loggerTag.value).e("onScannedCode: Handler is not initialized. Call createNewHandler() first.")
        } else {
            currentJob = matrixClient.sessionCoroutineScope.launch {
                // 监听流程状态并转发到 UI
                currentHandler.linkDesktopStep.onEach {
                    linkDesktopStepFlow.emit(it)
                }.launchIn(this)
                // 处理二维码内容，启动配对流程
                currentHandler.handleScannedQrCode(data)
            }
        }
    }
}
