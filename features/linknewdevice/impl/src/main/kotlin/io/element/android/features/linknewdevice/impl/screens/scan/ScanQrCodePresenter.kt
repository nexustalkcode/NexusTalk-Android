/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.scan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.element.android.features.linknewdevice.impl.LinkNewDesktopHandler
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.linknewdevice.LinkDesktopStep
import kotlinx.coroutines.launch

/**
 * 扫描二维码 Presenter
 *
 * 负责处理链接新设备时扫描二维码的业务逻辑和状态管理。
 * 管理二维码扫描流程和结果处理。
 *
 * @property linkNewDesktopHandler 链接新桌面设备处理器
 */
@Inject
class ScanQrCodePresenter(
    private val linkNewDesktopHandler: LinkNewDesktopHandler,
) : Presenter<ScanQrCodeState> {
    /**
     * 生成界面状态
     *
     * @return ScanQrCodeState 扫描二维码状态
     */
    @Composable
    override fun present(): ScanQrCodeState {
        val coroutineScope = rememberCoroutineScope()
        // 扫描状态，初始为 Loading
        var scanAction: AsyncAction<Unit> by remember { mutableStateOf(AsyncAction.Loading) }

        // 观察流程以响应 LinkDesktopStep.InvalidQrCode
        val linkDesktopStep by linkNewDesktopHandler.stepFlow.collectAsState()

        LaunchedEffect(Unit) {
            // 每次进入页面都创建新的 handler，确保二维码有效
            linkNewDesktopHandler.createNewHandler()
        }

        LaunchedEffect(linkDesktopStep) {
            when (val step = linkDesktopStep) {
                is LinkDesktopStep.InvalidQrCode -> {
                    // 扫描到无效二维码，显示错误
                    scanAction = AsyncAction.Failure(Exception(step.error))
                }
                else -> Unit
            }
        }

        /**
         * 处理用户事件
         *
         * @param event 扫描二维码事件
         */
        fun handleEvent(event: ScanQrCodeEvent) {
            when (event) {
                ScanQrCodeEvent.TryAgain -> {
                    // 重新尝试时恢复加载态
                    scanAction = AsyncAction.Loading
                }
                is ScanQrCodeEvent.QrCodeScanned -> coroutineScope.launch {
                    // 扫码成功后停止扫描并显示加载器
                    scanAction = AsyncAction.Success(Unit)
                    try {
                        linkNewDesktopHandler.onScannedCode(event.data)
                    } catch (e: Exception) {
                        // 理论上不会走到这里，错误通过 stepFlow 通知
                        scanAction = AsyncAction.Failure(e)
                    }
                }
            }
        }

        return ScanQrCodeState(
            scanAction = scanAction,
            eventSink = ::handleEvent,
        )
    }
}
