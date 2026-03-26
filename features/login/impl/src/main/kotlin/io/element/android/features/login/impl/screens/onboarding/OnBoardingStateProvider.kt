/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.login.impl.login.LoginMode
import io.element.android.features.login.impl.screens.onboarding.classic.LoginWithClassicState
import io.element.android.features.login.impl.screens.onboarding.classic.aLoginWithClassicState
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.R

/**
 * 初始页面状态提供者
 *
 * 为预览和测试提供 OnBoardingState 示例数据。
 *
 * @see OnBoardingState 初始页面状态
 */
open class OnBoardingStateProvider : PreviewParameterProvider<OnBoardingState> {
    /**
     * 提供预览状态序列
     */
    override val values: Sequence<OnBoardingState>
        get() = sequenceOf(
            anOnBoardingState(),
            anOnBoardingState(canLoginWithQrCode = true),
            anOnBoardingState(canCreateAccount = true),
            anOnBoardingState(canLoginWithQrCode = true, canCreateAccount = true),
            anOnBoardingState(canLoginWithQrCode = true, canCreateAccount = true, canReportBug = true),
            anOnBoardingState(defaultAccountProvider = "element.io", canCreateAccount = false, canReportBug = true),
            anOnBoardingState(customLogoResId = R.drawable.sample_background),
            anOnBoardingState(
                isAddingAccount = true,
                canLoginWithQrCode = true,
                canCreateAccount = true,
            ),
        )
}

/**
 * 创建示例初始页面状态
 *
 * @param isAddingAccount 是否正在添加账户
 * @param productionApplicationName 生产环境应用名称
 * @param defaultAccountProvider 默认账户提供商
 * @param mustChooseAccountProvider 是否必须选择账户提供商
 * @param canLoginWithQrCode 是否支持二维码登录
 * @param canCreateAccount 是否支持创建账户
 * @param canReportBug 是否可以报告问题
 * @param version 版本号
 * @param customLogoResId 自定义 Logo 资源 ID
 * @param loginMode 登录模式
 * @param loginWithClassicState 经典登录状态
 * @param eventSink 事件处理函数
 * @return OnBoardingState 示例实例
 */
fun anOnBoardingState(
    isAddingAccount: Boolean = false,
    productionApplicationName: String = "Element",
    defaultAccountProvider: String? = null,
    mustChooseAccountProvider: Boolean = false,
    canLoginWithQrCode: Boolean = false,
    canCreateAccount: Boolean = false,
    canReportBug: Boolean = false,
    version: String = "1.0.0",
    @DrawableRes
    customLogoResId: Int? = null,
    loginMode: AsyncData<LoginMode> = AsyncData.Uninitialized,
    loginWithClassicState: LoginWithClassicState = aLoginWithClassicState(),
    eventSink: (OnBoardingEvents) -> Unit = {},
) = OnBoardingState(
    isAddingAccount = isAddingAccount,
    productionApplicationName = productionApplicationName,
    defaultAccountProvider = defaultAccountProvider,
    mustChooseAccountProvider = mustChooseAccountProvider,
    canLoginWithQrCode = canLoginWithQrCode,
    canCreateAccount = canCreateAccount,
    canReportBug = canReportBug,
    version = version,
    loginMode = loginMode,
    onBoardingLogoResId = customLogoResId,
    loginWithClassicState = loginWithClassicState,
    eventSink = eventSink,
)
