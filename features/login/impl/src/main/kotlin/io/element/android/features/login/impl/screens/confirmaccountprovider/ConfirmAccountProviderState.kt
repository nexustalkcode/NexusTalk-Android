/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.confirmaccountprovider

import io.element.android.features.login.impl.accountprovider.AccountProvider
import io.element.android.features.login.impl.login.LoginMode
import io.element.android.libraries.architecture.AsyncData

/**
 * 账户提供商确认页面的 UI 状态数据类
 *
 * 遵循 MVI 架构模式，封装了该页面的所有界面状态信息
 * 状态由 [ConfirmAccountProviderPresenter] 生成，由 [ConfirmAccountProviderView] 消费
 *
 * @property accountProvider 当前用户选中的账户提供商信息，包含提供商名称、URL 等
 * @property isAccountCreation 标识当前流程是否为账户创建（注册）流程，true 表示注册，false 表示登录
 * @property loginMode 登录模式的异步数据状态，用于显示加载进度、登录选项或错误信息
 * @property eventSink 事件接收器函数，用于将用户交互事件传递给 Presenter 进行处理
 */
data class ConfirmAccountProviderState(
    /** 当前用户选中的账户提供商信息，包含提供商名称、URL、标识等 */
    val accountProvider: AccountProvider,
    /** 标识当前流程是否为账户创建（注册）流程，true 表示注册，false 表示登录 */
    val isAccountCreation: Boolean,
    /** 登录模式的异步数据状态，用于显示加载进度、登录选项或错误信息 */
    val loginMode: AsyncData<LoginMode>,
    /** 事件接收器函数，用于将用户交互事件传递给 Presenter 进行处理 */
    val eventSink: (ConfirmAccountProviderEvents) -> Unit
) {
    /**
     * 判断提交按钮是否可用
     *
     * 提交按钮在以下条件下可用：
     * 1. 账户提供商 URL 不为空
     * 2. 当前未处于加载完成状态（只能点击一次，避免重复提交）
     *
     * @return true 表示按钮可用，false 表示按钮禁用
     */
    val submitEnabled: Boolean get() = accountProvider.url.isNotEmpty() && (loginMode is AsyncData.Uninitialized || loginMode is AsyncData.Loading)
}
