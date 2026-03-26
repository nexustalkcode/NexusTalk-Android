/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.pip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Inject
import io.element.android.features.call.impl.utils.PipController
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.log.logger.LoggerTag
import kotlinx.coroutines.launch
import timber.log.Timber

/** 日志标签，用于日志输出 */
private val loggerTag = LoggerTag("PiP")

/**
 * 画中画 Presenter
 *
 * 负责处理通话画中画模式的业务逻辑和状态管理。
 * 管理画中画模式的进入、退出和状态跟踪。
 *
 * @property pipSupportProvider 画中画支持提供者，用于检查设备是否支持画中画功能
 *
 * @see PipSupportProvider 画中画支持提供者接口
 * @see PictureInPictureState 画中画状态
 * @see PictureInPictureEvents 画中画事件
 * @see PipController 画中画控制器接口
 */
@Inject
class PictureInPicturePresenter(
    pipSupportProvider: PipSupportProvider,
) : Presenter<PictureInPictureState> {
    private val isPipSupported = pipSupportProvider.isPipSupported()
    private var pipView: PipView? = null

    /**
     * 生成界面状态
     *
     * @return PictureInPictureState 画中画状态
     */
    @Composable
    override fun present(): PictureInPictureState {
        val coroutineScope = rememberCoroutineScope()
        var isInPictureInPicture by remember { mutableStateOf(false) }
        var pipController by remember { mutableStateOf<PipController?>(null) }

        /**
         * 处理用户事件
         *
         * @param event 画中画事件
         */
        fun handleEvent(event: PictureInPictureEvents) {
            when (event) {
                is PictureInPictureEvents.SetPipController -> {
                    pipController = event.pipController
                }
                PictureInPictureEvents.EnterPictureInPicture -> {
                    coroutineScope.launch {
                        switchToPip(pipController)
                    }
                }
                is PictureInPictureEvents.OnPictureInPictureModeChanged -> {
                    Timber.tag(loggerTag.value).d("onPictureInPictureModeChanged: ${event.isInPip}")
                    isInPictureInPicture = event.isInPip
                    if (event.isInPip) {
                        pipController?.enterPip()
                    } else {
                        pipController?.exitPip()
                    }
                }
            }
        }

        return PictureInPictureState(
            supportPip = isPipSupported,
            isInPictureInPicture = isInPictureInPicture,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 设置画中画视图
     *
     * @param pipView 画中画视图
     */
    fun setPipView(pipView: PipView?) {
        if (isPipSupported) {
            Timber.tag(loggerTag.value).d("Setting PiP params")
            this.pipView = pipView
            pipView?.setPipParams()
        } else {
            Timber.tag(loggerTag.value).d("setPipView: PiP is not supported")
        }
    }

    /**
     * 进入画中画模式
     *
     * 如果 Element Call 允许，则进入画中画模式。
     *
     * @param pipController 画中画控制器
     */
    private suspend fun switchToPip(pipController: PipController?) {
        if (isPipSupported) {
            if (pipController == null) {
                Timber.tag(loggerTag.value).w("webPipApi is not available")
            }
            if (pipController == null || pipController.canEnterPip()) {
                Timber.tag(loggerTag.value).d("Switch to PiP mode")
                pipView?.enterPipMode()
                    ?.also { Timber.tag(loggerTag.value).d("Switch to PiP mode result: $it") }
            } else {
                Timber.tag(loggerTag.value).w("Cannot enter PiP mode, hangup the call")
                pipView?.hangUp()
            }
        }
    }
}
