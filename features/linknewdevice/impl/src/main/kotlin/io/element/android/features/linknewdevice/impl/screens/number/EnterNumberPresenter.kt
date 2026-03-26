/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.linknewdevice.impl.screens.number

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.features.linknewdevice.impl.LinkNewMobileHandler
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.log.logger.LoggerTag
import io.element.android.libraries.matrix.api.linknewdevice.CheckCodeSender
import io.element.android.libraries.matrix.api.linknewdevice.LinkMobileStep
import io.element.android.libraries.matrix.api.logs.LoggerTags
import kotlinx.coroutines.launch
import timber.log.Timber

/** 日志标签 */
private val tag = LoggerTag("EnterNumberPresenter", LoggerTags.linkNewDevice)

/**
 * 输入号码 Presenter
 *
 * 负责处理链接新设备时输入设备号码的业务逻辑和状态管理。
 * 管理验证码的输入和发送流程。
 *
 * @property navigator 输入号码导航器
 * @property linkNewMobileHandler 链接新移动设备处理器
 */
@AssistedInject
class EnterNumberPresenter(
    @Assisted private val navigator: EnterNumberNavigator,
    private val linkNewMobileHandler: LinkNewMobileHandler,
) : Presenter<EnterNumberState> {
    /**
     * 工厂接口
     */
    @AssistedFactory
    interface Factory {
        /**
         * 创建 Presenter 实例
         *
         * @param navigator 输入号码导航器
         * @return EnterNumberPresenter 实例
         */
        fun create(navigator: EnterNumberNavigator): EnterNumberPresenter
    }

    /**
     * 生成界面状态
     *
     * @return EnterNumberState 输入号码状态
     */
    @Composable
    override fun present(): EnterNumberState {
        val coroutineScope = rememberCoroutineScope()
        // 当前输入的数字字符串
        var number by remember { mutableStateOf("") }
        // 发送校验码的异步状态
        var sendingCode by remember<MutableState<AsyncAction<Unit>>> { mutableStateOf(AsyncAction.Uninitialized) }

        // 观察流程以响应 ErrorType.InvalidCheckCode
        val linkMobileStep by linkNewMobileHandler.stepFlow.collectAsState()

        // 用于发送校验码的实例（来自扫码完成后的步骤）
        var checkCodeSender: CheckCodeSender? by remember { mutableStateOf(null) }

        LaunchedEffect(linkMobileStep) {
            when (val step = linkMobileStep) {
                is LinkMobileStep.QrScanned -> {
                    // 扫码完成后会携带 sender，用于校验+发送两位数字
                    checkCodeSender = step.checkCodeSender
                }
                else -> Unit
            }
        }

        /**
         * 处理用户事件
         *
         * @param event 输入号码事件
         */
        fun handleEvent(event: EnterNumberEvent) {
            when (event) {
                is EnterNumberEvent.UpdateNumber -> {
                    // 输入变化时重置发送状态
                    sendingCode = AsyncAction.Uninitialized
                    // 作为安全措施，只保留数字
                    number = event.number.filter { it.isDigit() }
                }
                EnterNumberEvent.Continue -> coroutineScope.launch {
                    // 获取当前的验证码发送者
                    val sender = checkCodeSender
                    if (sender == null) {
                        // 理论上不应该发生，记录日志并标记失败
                        Timber.tag(tag.value).e("No check code sender available")
                        sendingCode = AsyncAction.Failure(IllegalStateException("No check code sender available"))
                    } else {
                        sendingCode = AsyncAction.Loading
                        // 转为无符号字节，与底层协议一致（两位数字）
                        val uByte = number.toUByte()
                        // 先本地校验格式再发送
                        val isValid = sender.validate(uByte)
                        if (isValid) {
                            sender.send(uByte)
                                .fold(
                                    onSuccess = {
                                        Timber.tag(tag.value).d("Code sent successfully")
                                        // 保持加载状态，不将 sendingCode 设置为 AsyncAction.Success(Unit)
                                    },
                                    onFailure = {
                                        Timber.tag(tag.value).e(it, "Failed to send number code")
                                        sendingCode = AsyncAction.Failure(it)
                                    }
                                )
                        } else {
                            // 校验失败，跳转到错误提示页
                            navigator.navigateToWrongNumberError()
                        }
                    }
                }
            }
        }

        return EnterNumberState(
            number = number,
            sendingCode = sendingCode,
            eventSink = ::handleEvent,
        )
    }
}
