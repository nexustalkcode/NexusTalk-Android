/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dev.zacsweers.metro.Inject
import io.element.android.features.login.impl.error.ChangeServerError
import io.element.android.features.login.impl.screens.chooseaccountprovider.ChooseAccountProviderPresenter
import io.element.android.features.login.impl.screens.confirmaccountprovider.ConfirmAccountProviderPresenter
import io.element.android.features.login.impl.screens.createaccount.AccountCreationNotSupported
import io.element.android.features.login.impl.screens.onboarding.OnBoardingPresenter
import io.element.android.features.login.impl.web.WebClientUrlForAuthenticationRetriever
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.runCatchingUpdatingState
import io.element.android.libraries.matrix.api.auth.MatrixAuthenticationService
import io.element.android.libraries.matrix.api.auth.OidcPrompt
import io.element.android.libraries.oidc.api.OidcAction
import io.element.android.libraries.oidc.api.OidcActionFlow

/**
 * 登录流程管理器，负责处理 OIDC 登录和密码登录的核心逻辑。
 *
 * 此类提供以下功能：
 * - 收集和监听 OIDC 登录动作
 * - 提交登录请求，支持多种登录模式
 * - 管理登录状态和错误处理
 *
 * 此类被以下Presenter使用以避免代码重复：
 * - [OnBoardingPresenter]
 * - [ConfirmAccountProviderPresenter]
 * - [ChooseAccountProviderPresenter]
 */
@Inject
class LoginHelper(
    private val oidcActionFlow: OidcActionFlow,
    private val authenticationService: MatrixAuthenticationService,
    private val webClientUrlForAuthenticationRetriever: WebClientUrlForAuthenticationRetriever,
) {
    /**
     * 登录模式的当前状态。
     *
     * 用于跟踪登录流程的当前状态：
     * - Uninitialized: 未初始化状态
     * - Loading: 正在处理登录请求
     * - Success: 登录成功
     * - Failure: 登录失败
     */
    private val loginModeState: MutableState<AsyncData<LoginMode>> = mutableStateOf(AsyncData.Uninitialized)

    /**
     * 收集并监听登录模式的状态变化。
     *
     * 此方法是一个 Composable 函数，用于：
     * 1. 监听 OIDC 动作流中的变化
     * 2. 当有新的 OIDC 动作时，调用 [onOidcAction] 方法处理
     * 3. 返回当前登录状态供 UI 层使用
     *
     * @return 包含当前登录模式的 [State] 对象，可用于 Compose UI 状态收集
     */
    @Composable
    fun collectLoginMode(): State<AsyncData<LoginMode>> {
        LaunchedEffect(Unit) {
            oidcActionFlow.collect { oidcAction ->
                if (oidcAction != null) {
                    onOidcAction(oidcAction)
                }
            }
        }
        return loginModeState
    }

    /**
     * 清除当前的错误状态。
     *
     * 调用此方法会将 [loginModeState] 重置为 [AsyncData.Uninitialized] 状态。
     * 通常在用户重新尝试登录或离开登录页面时调用。
     */
    fun clearError() {
        loginModeState.value = AsyncData.Uninitialized
    }

    /**
     * 提交登录请求，根据服务器支持的情况选择合适的登录方式。
     *
     * 此方法会根据以下条件确定登录模式：
     * 1. 如果服务器支持 OIDC 登录，则使用 OIDC 方式
     * 2. 如果不支持 OIDC 但支持账号创建，则使用网页账号创建
     * 3. 如果只支持密码登录，则使用密码登录
     *
     * @param isAccountCreation 是否为账号创建流程
     * @param homeserverUrl  homeserver 的 URL 地址
     * @param loginHint  登录提示信息，可选的用户标识提示
     */
    suspend fun submit(
        isAccountCreation: Boolean,
        homeserverUrl: String,
        loginHint: String?,
    ) {
        suspend {
            authenticationService.setHomeserver(homeserverUrl).map { matrixHomeServerDetails ->
                if (matrixHomeServerDetails.supportsOidcLogin) {
                    // 如果支持 OIDC 登录，立即获取 OIDC URL
                    val oidcPrompt = if (isAccountCreation) OidcPrompt.Create else OidcPrompt.Login
                    LoginMode.Oidc(
                        authenticationService.getOidcUrl(prompt = oidcPrompt, loginHint = loginHint).getOrThrow()
                    )
                } else if (isAccountCreation) {
                    val url = webClientUrlForAuthenticationRetriever.retrieve(homeserverUrl)
                    LoginMode.AccountCreation(url)
                } else if (matrixHomeServerDetails.supportsPasswordLogin) {
                    LoginMode.PasswordLogin
                } else {
                    error("Unsupported login flow")
                }
            }.getOrThrow()
        }.runCatchingUpdatingState(
            state = loginModeState,
            errorTransform = {
                when (it) {
                    is AccountCreationNotSupported -> it
                    else -> ChangeServerError.from(it)
                }
            }
        )
    }

    /**
     * 处理接收到的 OIDC 动作。
     *
     * 此方法处理两种类型的 OIDC 动作：
     * - [OidcAction.GoBack]: 用户取消 OIDC 登录流程，返回上一页
     * - [OidcAction.Success]: OIDC 登录成功，使用返回的 URL 完成登录
     *
     * 注意：对于 GoBack 动作，如果当前状态不是 Loading，则忽略该动作。
     * 这是为了避免在登录出错后用户重试时，旧的 GoBack 动作干扰新的登录流程。
     *
     * @param oidcAction 接收到的 OIDC 动作
     */
    private suspend fun onOidcAction(oidcAction: OidcAction) {
        if (oidcAction is OidcAction.GoBack && oidcAction.toUnblock && loginModeState.value !is AsyncData.Loading) {
            // 忽略非 Loading 状态的 GoBack 动作。这个动作来自 LoginFlowNode。
            // 这种情况可能发生在登录出错后，用户尝试在同一账号上再次登录时。
            return
        }
        loginModeState.value = AsyncData.Loading()
        when (oidcAction) {
            is OidcAction.GoBack -> {
                authenticationService.cancelOidcLogin()
                    .onSuccess {
                        loginModeState.value = AsyncData.Uninitialized
                    }
                    .onFailure { failure ->
                        loginModeState.value = AsyncData.Failure(failure)
                    }
            }
            is OidcAction.Success -> {
                authenticationService.loginWithOidc(oidcAction.url)
                    .onFailure { failure ->
                        loginModeState.value = AsyncData.Failure(failure)
                    }
            }
        }
        // 重置 OIDC 动作流，以便接收新的动作
        oidcActionFlow.reset()
    }
}
