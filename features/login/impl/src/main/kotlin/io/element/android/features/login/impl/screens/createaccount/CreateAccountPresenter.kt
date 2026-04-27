/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.createaccount

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.extensions.flatMap
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.matrix.api.auth.MatrixAuthenticationService
import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AssistedInject
/**
 * 创建账号页面 Presenter。
 *
 * 负责维护注册页加载进度，并在收到外部注册结果消息后导入新会话。
 */
class CreateAccountPresenter(
    @Assisted private val url: String,
    private val authenticationService: MatrixAuthenticationService,
    private val messageParser: MessageParser,
    private val buildMeta: BuildMeta,
) : Presenter<CreateAccountState> {
    /**
     * 创建 Presenter 的 Assisted 工厂。
     */
    @AssistedFactory
    interface Factory {
        fun create(url: String): CreateAccountPresenter
    }

    /**
     * 生成创建账号页面状态并处理页面事件。
     */
    @Composable
    override fun present(): CreateAccountState {
        val coroutineScope = rememberCoroutineScope()
        val pageProgress: MutableState<Int> = remember { mutableIntStateOf(0) }
        val createAction: MutableState<AsyncAction<SessionId>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        fun handleEvent(event: CreateAccountEvents) {
            when (event) {
                is CreateAccountEvents.SetPageProgress -> {
                    pageProgress.value = event.progress
                }
                is CreateAccountEvents.OnMessageReceived -> {
                    // Ignore unexpected message
                    if (event.message.contains("isTrusted")) return
                    coroutineScope.importSession(event.message, createAction)
                }
            }
        }

        return CreateAccountState(
            url = url,
            pageProgress = pageProgress.value,
            isDebugBuild = buildMeta.isDebuggable,
            createAction = createAction.value,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 解析注册完成消息并把外部会话导入到本地认证服务。
     *
     * @param message 注册页回传的原始消息。
     * @param loggedInState 需要回写的创建账号异步状态。
     */
    private fun CoroutineScope.importSession(message: String, loggedInState: MutableState<AsyncAction<SessionId>>) = launch {
        loggedInState.value = AsyncAction.Loading
        runCatchingExceptions {
            messageParser.parse(message)
        }.flatMap { externalSession ->
            authenticationService.importCreatedSession(externalSession)
        }.onSuccess { sessionId ->
            loggedInState.value = AsyncAction.Success(sessionId)
        }.onFailure { failure ->
            loggedInState.value = AsyncAction.Failure(failure)
        }
    }
}
