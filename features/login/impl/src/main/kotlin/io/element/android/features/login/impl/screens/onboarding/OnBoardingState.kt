/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding

import androidx.annotation.DrawableRes
import io.element.android.features.login.impl.login.LoginMode
import io.element.android.features.login.impl.screens.onboarding.classic.LoginWithClassicState
import io.element.android.libraries.architecture.AsyncData

/**
 * 初始页面状态数据类
 *
 * 表示应用首次启动或用户未登录时的初始页面状态。
 * 包含登录所需的各种配置信息和 UI 状态。
 *
 * @property isAddingAccount 是否正在添加账户（已有账户存在）
 * @property productionApplicationName 生产环境的应用名称
 * @property defaultAccountProvider 默认的账户提供商 URL
 * @property mustChooseAccountProvider 是否必须选择账户提供商
 * @property canLoginWithQrCode 是否支持二维码登录
 * @property canCreateAccount 是否支持创建账户
 * @property canReportBug 是否可以报告问题
 * @property version 应用版本号
 * @property onBoardingLogoResId 初始页面 Logo 资源 ID
 * @property loginMode 登录模式（异步数据）
 * @property isSignInLoading 登录按钮是否显示加载状态
 * @property isCreateAccountLoading 创建账户按钮是否显示加载状态
 * @property loginWithClassicState 经典登录状态
 * @property eventSink 事件处理函数
 */
data class OnBoardingState(
    val isAddingAccount: Boolean,
    val productionApplicationName: String,
    val defaultAccountProvider: String?,
    val mustChooseAccountProvider: Boolean,
    val canLoginWithQrCode: Boolean,
    val canCreateAccount: Boolean,
    val canReportBug: Boolean,
    val version: String,
    @DrawableRes
    val onBoardingLogoResId: Int?,
    val loginMode: AsyncData<LoginMode>,
    val isSignInLoading: Boolean = false,
    val isCreateAccountLoading: Boolean = false,
    val loginWithClassicState: LoginWithClassicState,
    val eventSink: (OnBoardingEvents) -> Unit,
) {
    /**
     * 是否可以提交登录
     *
     * 当有默认账户提供商且登录模式为未初始化或加载中时返回 true
     */
    val submitEnabled: Boolean
        get() = defaultAccountProvider != null && (loginMode is AsyncData.Uninitialized || loginMode is AsyncData.Loading)
}
