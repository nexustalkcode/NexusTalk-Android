/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.confirmaccountprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.login.impl.accountprovider.AccountProvider
import io.element.android.features.login.impl.accountprovider.anAccountProvider
import io.element.android.features.login.impl.login.LoginMode
import io.element.android.features.login.impl.screens.createaccount.AccountCreationNotSupported
import io.element.android.libraries.architecture.AsyncData

/**
 * 账户提供商确认页面的 Compose 预览参数提供者
 *
 * 实现了 [PreviewParameterProvider] 接口，用于在 Android Studio/IDEA 中
 * 为 [ConfirmAccountProviderView] 提供多种预览状态数据
 *
 * 使用场景：
 * - 在 IDE 预览中展示不同状态的 UI 效果
 * - 支持登录模式、注册模式、错误状态等多种组合的预览
 *
 * 提供的预览状态包括：
 * 1. 默认登录状态
 * 2. 注册流程状态
 * 3. 注册不支持的错误状态
 */
open class ConfirmAccountProviderStateProvider : PreviewParameterProvider<ConfirmAccountProviderState> {
    /**
     * 提供多种状态序列用于 UI 预览
     *
     * 包含以下测试场景：
     * - 默认登录模式（isAccountCreation = false）
     * - 注册流程模式（isAccountCreation = true）
     * - 注册不支持的错误场景
     */
    override val values: Sequence<ConfirmAccountProviderState>
        get() = sequenceOf(
            aConfirmAccountProviderState(),
            aConfirmAccountProviderState(
                isAccountCreation = true,
            ),
            aConfirmAccountProviderState(
                isAccountCreation = true,
                loginMode = AsyncData.Failure(AccountCreationNotSupported())
            ),
        )
}

/**
 * 创建账户提供商确认状态数据的辅助工厂函数
 *
 * 用于快速创建测试用的 [ConfirmAccountProviderState] 实例，
 * 支持自定义各属性的默认值
 *
 * @param accountProvider 账户提供商数据，默认为测试用提供商
 * @param isAccountCreation 是否为账户创建流程，默认为 false（登录流程）
 * @param loginMode 登录模式状态，默认为未初始化状态
 * @param eventSink 事件接收器，默认为空函数
 * @return 配置好的 ConfirmAccountProviderState 实例
 */
private fun aConfirmAccountProviderState(
    accountProvider: AccountProvider = anAccountProvider(),
    isAccountCreation: Boolean = false,
    loginMode: AsyncData<LoginMode> = AsyncData.Uninitialized,
    eventSink: (ConfirmAccountProviderEvents) -> Unit = {},
) = ConfirmAccountProviderState(
    accountProvider = accountProvider,
    isAccountCreation = isAccountCreation,
    loginMode = loginMode,
    eventSink = eventSink
)
