/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.confirmaccountprovider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.features.login.impl.accountprovider.AccountProviderDataSource
import io.element.android.features.login.impl.login.LoginHelper
import io.element.android.libraries.architecture.Presenter
import kotlinx.coroutines.launch

/**
 * ConfirmAccountProviderPresenter - 账户提供商确认页面的 Presenter
 *
 * 职责说明：
 * - 遵循 MVI（Model-View-Intent）架构模式，处理账户确认页面的业务逻辑
 * - 负责收集账户提供商数据源的数据，生成界面状态
 * - 处理用户交互事件（继续登录/注册、清除错误等）
 * - 作为 View 层与业务逻辑层之间的桥梁，隔离 UI 与数据操作
 *
 * 数据流向：
 * [AccountProviderDataSource] -> [Presenter] -> [ConfirmAccountProviderState] -> [ConfirmAccountProviderView]
 *
 * @param params 传入参数，包含是否为账户创建流程的标识
 * @param accountProviderDataSource 账户提供商数据源，用于监听当前选中的服务器提供商信息
 * @param loginHelper 登录辅助类，封装了登录、注册等核心业务逻辑
 */
@AssistedInject
class ConfirmAccountProviderPresenter(
    /** 传入参数，包含是否为账户创建流程的标识，用于区分登录和注册两种不同的操作 */
    @Assisted private val params: Params,
    /** 账户提供商数据源，用于获取当前用户选中的 Homeserver 提供商信息（包含 URL、标识等） */
    private val accountProviderDataSource: AccountProviderDataSource,
    /** 登录辅助类，处理实际的登录/注册认证流程，包括 OAuth 跳转和凭据提交 */
    private val loginHelper: LoginHelper,
) : Presenter<ConfirmAccountProviderState> {
    /**
     * Presenter 构造参数数据类
     *
     * 定义了创建 [ConfirmAccountProviderPresenter] 实例所需的配置参数，
     * 通过依赖注入框架传递
     *
     * @property isAccountCreation 标识当前流程是否为账户创建（注册）流程
     *        - true：表示用户正在进行新账户注册
     *        - false：表示用户正在进行现有账户登录
     */
    data class Params(
        /** 标识当前流程是否为账户创建（注册）流程，true 为注册，false 为登录 */
        val isAccountCreation: Boolean,
    )

    /**
     * Presenter 工厂接口
     *
     * 用于依赖注入框架创建 [ConfirmAccountProviderPresenter] 实例，
     * 实现了 [AssistedFactory] 接口以支持构造时注入
     *
     * 使用场景：
     * - 由 Appyx 框架或 Dagger/Hilt 等 DI 框架在创建节点时调用
     * - 传递 [Params] 参数以配置 Presenter 的初始状态
     */
    @AssistedFactory
    interface Factory {
        /**
         * 创建 Presenter 实例
         *
         * @param params 构造参数，包含流程类型配置
         * @return 配置好的 ConfirmAccountProviderPresenter 实例
         */
        fun create(params: Params): ConfirmAccountProviderPresenter
    }

    /**
     * 生成页面状态
     *
     * 实现 [Presenter] 接口的核心方法，
     * 遵循 MVI 架构模式生成 UI 所需的状态数据
     *
     * 状态生成逻辑：
     * 1. 从 [AccountProviderDataSource] 收集当前选中的账户提供商信息
     * 2. 从 [LoginHelper] 收集当前的登录模式状态
     * 3. 创建局部协程作用域用于处理异步事件
     * 4. 定义事件处理函数 [handleEvent]，根据事件类型调用相应的业务逻辑
     * 5. 组装并返回 [ConfirmAccountProviderState] 状态对象
     *
     * 数据流向：
     * [accountProviderDataSource.flow] -> [loginMode] -> [ConfirmAccountProviderState]
     *
     * @return 包含页面渲染所需的完整状态数据，包括提供商信息、流程类型、登录模式和事件接收器
     */
    @Composable
    override fun present(): ConfirmAccountProviderState {
        /** 从数据源收集当前选中的账户提供商信息（响应式 Flow） */
        val accountProvider by accountProviderDataSource.flow.collectAsState()
        /** 创建局部协程作用域，用于处理需要挂起的操作（如登录认证） */
        val localCoroutineScope = rememberCoroutineScope()

        /** 从登录辅助类收集当前的登录模式状态（响应式 Flow） */
        val loginMode by loginHelper.collectLoginMode()

        /**
         * 事件处理函数
         *
         * 根据 [ConfirmAccountProviderEvents] 事件类型，
         * 调用相应的业务逻辑处理用户交互
         *
         * 事件处理逻辑：
         * - [ConfirmAccountProviderEvents.Continue]：在协程中调用登录辅助类的 submit 方法，
         *        根据 isAccountCreation 标识决定执行登录还是注册流程
         * - [ConfirmAccountProviderEvents.ClearError]：清除登录过程中产生的错误状态
         *
         * @param event 用户交互事件，由 View 层传入
         */
        fun handleEvent(event: ConfirmAccountProviderEvents) {
            when (event) {
                /** 继续事件：执行登录或注册认证流程 */
                ConfirmAccountProviderEvents.Continue -> localCoroutineScope.launch {
                    loginHelper.submit(
                        isAccountCreation = params.isAccountCreation,
                        homeserverUrl = accountProvider.url,
                        loginHint = null,
                    )
                }
                /** 清除错误事件：清除登录过程中产生的错误状态 */
                ConfirmAccountProviderEvents.ClearError -> loginHelper.clearError()
            }
        }

        /** 组装并返回页面状态，供 View 层消费渲染 */
        return ConfirmAccountProviderState(
            accountProvider = accountProvider,
            isAccountCreation = params.isAccountCreation,
            loginMode = loginMode,
            eventSink = ::handleEvent,
        )
    }
}
