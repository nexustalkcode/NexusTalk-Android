/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.element.android.features.linknewdevice.impl.LinkNewMobileHandler
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.linknewdevice.LinkMobileStep
import kotlinx.coroutines.launch

/**
 * 链接新设备根页面 Presenter
 *
 * 负责处理链接新设备根页面的业务逻辑和状态管理。
 * 管理链接功能的初始化和状态展示。
 *
 * @property matrixClient Matrix 客户端
 * @property linkNewMobileHandler 链接新移动设备处理器
 */
@Inject
class LinkNewDeviceRootPresenter(
    private val matrixClient: MatrixClient,
    private val linkNewMobileHandler: LinkNewMobileHandler,
) : Presenter<LinkNewDeviceRootState> {
    /**
     * 生成界面状态
     *
     * @return LinkNewDeviceRootState 链接新设备根页面状态
     */
    @Composable
    override fun present(): LinkNewDeviceRootState {
        val coroutineScope = rememberCoroutineScope()
        // 是否支持链接新设备
        var isSupported by remember { mutableStateOf<AsyncData<Boolean>>(AsyncData.Uninitialized) }
        // 二维码生成状态（仅关心是否准备好）
        var qrCodeData by remember { mutableStateOf<AsyncData<Unit>>(AsyncData.Uninitialized) }

        LaunchedEffect(Unit) {
            // 查询当前账户是否支持此功能
            matrixClient.canLinkNewDevice().fold(
                onSuccess = { supported ->
                    isSupported = AsyncData.Success(supported)
                },
                onFailure = {
                    isSupported = AsyncData.Failure(it)
                }
            )
        }

        // 监听移动端链接流程的状态变化
        val step by linkNewMobileHandler.stepFlow.collectAsState()

        LaunchedEffect(step) {
            when (val finalStep = step) {
                is LinkMobileStep.Uninitialized -> {
                    // 未开始生成二维码
                    qrCodeData = AsyncData.Uninitialized
                }
                is LinkMobileStep.QrReady -> {
                    // 二维码就绪
                    qrCodeData = AsyncData.Success(Unit)
                }
                is LinkMobileStep.Error -> {
                    // 生成二维码失败
                    qrCodeData = AsyncData.Failure(finalStep.errorType)
                }
                else -> Unit
            }
        }

        /**
         * 处理用户事件
         *
         * @param event 链接新设备根页面事件
         */
        fun handleEvent(event: LinkNewDeviceRootEvent) {
            when (event) {
                LinkNewDeviceRootEvent.LinkMobileDevice -> coroutineScope.launch {
                    // 开始生成二维码，进入加载态
                    qrCodeData = AsyncData.Loading()
                    // 重置并启动新的二维码生成流程
                    linkNewMobileHandler.reset()
                    linkNewMobileHandler.createAndStartNewHandler()
                }
                LinkNewDeviceRootEvent.CloseDialog -> coroutineScope.launch {
                    // 关闭错误弹窗时重置流程
                    linkNewMobileHandler.reset()
                }
            }
        }

        return LinkNewDeviceRootState(
            isSupported = isSupported,
            qrCodeData = qrCodeData,
            eventSink = ::handleEvent,
        )
    }
}
