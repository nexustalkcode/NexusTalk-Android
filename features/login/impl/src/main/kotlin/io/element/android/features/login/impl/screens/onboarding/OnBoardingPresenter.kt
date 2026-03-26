/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.appconfig.OnBoardingConfig
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.features.enterprise.api.canConnectToAnyHomeserver
import io.element.android.features.login.impl.accesscontrol.DefaultAccountProviderAccessControl
import io.element.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.element.android.features.login.impl.login.LoginHelper
import io.element.android.features.login.impl.screens.onboarding.classic.LoginWithClassicState
import io.element.android.features.rageshake.api.RageshakeFeatureAvailability
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.sessionstorage.api.SessionStore
import io.element.android.libraries.ui.utils.MultipleTapToUnlock
import kotlinx.coroutines.launch

/**
 * 初始页面 Presenter
 *
 * 负责处理初始页面的业务逻辑，管理登录流程的状态。
 * 处理账户提供商选择、企业策略检查、登录模式获取等功能。
 *
 * @property params 节点参数
 * @property buildMeta 构建元数据
 * @property enterpriseService 企业服务
 * @property defaultAccountProviderAccessControl 默认账户提供商访问控制
 * @property rageshakeFeatureAvailability 崩溃报告功能可用性
 * @property loginHelper 登录辅助工具
 * @property onBoardingLogoResIdProvider 初始页面 Logo 资源 ID 提供者
 * @property sessionStore 会话存储
 * @property accountProviderDataSource 账户提供商数据源
 * @property loginWithClassicPresenter 经典登录 Presenter
 */
@AssistedInject
class OnBoardingPresenter(
    @Assisted private val params: OnBoardingNode.Params,
    private val buildMeta: BuildMeta,
    private val enterpriseService: EnterpriseService,
    private val defaultAccountProviderAccessControl: DefaultAccountProviderAccessControl,
    private val rageshakeFeatureAvailability: RageshakeFeatureAvailability,
    private val loginHelper: LoginHelper,
    private val onBoardingLogoResIdProvider: OnBoardingLogoResIdProvider,
    private val sessionStore: SessionStore,
    private val accountProviderDataSource: AccountProviderDataSource,
    private val loginWithClassicPresenter: Presenter<LoginWithClassicState>,
) : Presenter<OnBoardingState> {
    /**
     * Presenter 工厂接口
     */
    @AssistedFactory
    interface Factory {
        /**
         * 创建 Presenter 实例
         *
         * @param params 节点参数
         * @return OnBoardingPresenter 实例
         */
        fun create(
            params: OnBoardingNode.Params,
        ): OnBoardingPresenter
    }

    private val multipleTapToUnlock = MultipleTapToUnlock()

    @Composable
    override fun present(): OnBoardingState {
        /** 从数据源收集当前选中的账户提供商信息（响应式 Flow） */
        val accountProvider by accountProviderDataSource.flow.collectAsState()

        val localCoroutineScope = rememberCoroutineScope()
        val forcedAccountProvider = remember {
            // 如果 defaultHomeserverList() 返回单一列表，则这是默认账户提供商
            // 在这种情况下，用户可以使用此 homeserver 登录，或使用二维码登录
            enterpriseService.defaultHomeserverList().singleOrNull()
        }
        val canConnectToAnyHomeserver = remember {
            enterpriseService.canConnectToAnyHomeserver()
        }
        val mustChooseAccountProvider = remember {
            !canConnectToAnyHomeserver && enterpriseService.defaultHomeserverList().size > 1
        }
        val linkAccountProvider by produceState<String?>(initialValue = null) {
            // 来自链接的账户提供商（如果企业服务允许）
            value = params.accountProvider?.takeIf {
                try {
                    defaultAccountProviderAccessControl.assertIsAllowedToConnectToAccountProvider(it, it)
                    true
                } catch (_: Exception) {
                    false
                }
            }
        }
        val defaultAccountProvider = remember(linkAccountProvider) {
            // 如果有强制的账户提供商，则使用它
            // 否则使用参数中传递的账户提供商（如果允许）
            forcedAccountProvider ?: linkAccountProvider
        }
        val canLoginWithQrCode by produceState(initialValue = false, linkAccountProvider) {
            value = linkAccountProvider == null
        }
        val canReportBug by remember { rageshakeFeatureAvailability.isAvailable() }.collectAsState(false)
        var showReportBug by rememberSaveable { mutableStateOf(false) }
        val onBoardingLogoResId = remember {
            onBoardingLogoResIdProvider.get()
        }
        val isAddingAccount by produceState(initialValue = false) {
            // 如果至少有一个会话已存储，则正在添加账户
            value = sessionStore.numberOfSessions() > 0
        }

        val loginMode by loginHelper.collectLoginMode()

        val loginWithClassicState = loginWithClassicPresenter.present()

        // 单独的加载状态跟踪
        var isSignInLoading by rememberSaveable { mutableStateOf(false) }
        var isCreateAccountLoading by rememberSaveable { mutableStateOf(false) }

        fun handleEvent(event: OnBoardingEvents) {
            when (event) {
                is OnBoardingEvents.OnSignIn -> localCoroutineScope.launch {
                    isSignInLoading = true
                    // 确保设置了当前账户提供商
                    accountProviderDataSource.setUrl(event.defaultAccountProvider)
                    loginHelper.submit(
                        isAccountCreation = false,
                        homeserverUrl = event.defaultAccountProvider,
                        loginHint = params.loginHint?.takeIf { forcedAccountProvider == null },
                    )
                    isSignInLoading = false
                }
                is OnBoardingEvents.OnCreateAccount -> localCoroutineScope.launch {
                    isCreateAccountLoading = true
                    // 创建账户处理：跳过确认页面，直接提交
                    loginHelper.submit(
                        isAccountCreation = true,
                        homeserverUrl = accountProvider.url,
                        loginHint = null,
                    )
                    isCreateAccountLoading = false
                }
                OnBoardingEvents.ClearError -> loginHelper.clearError()
                OnBoardingEvents.OnVersionClick -> {
                    if (canReportBug) {
                        if (multipleTapToUnlock.unlock(localCoroutineScope)) {
                            showReportBug = true
                        }
                    }
                }
            }
        }

        return OnBoardingState(
            isAddingAccount = isAddingAccount,
            productionApplicationName = buildMeta.productionApplicationName,
            //设置登录链接为默认的链接
            defaultAccountProvider = accountProvider.url,
            mustChooseAccountProvider = mustChooseAccountProvider,
            canLoginWithQrCode = canLoginWithQrCode,
            canCreateAccount = defaultAccountProvider == null && canConnectToAnyHomeserver && OnBoardingConfig.CAN_CREATE_ACCOUNT,
            canReportBug = canReportBug && showReportBug,
            loginMode = loginMode,
            isSignInLoading = isSignInLoading,
            isCreateAccountLoading = isCreateAccountLoading,
            version = buildMeta.versionName,
            onBoardingLogoResId = onBoardingLogoResId,
            loginWithClassicState = loginWithClassicState,
            eventSink = ::handleEvent,
        )
    }
}
