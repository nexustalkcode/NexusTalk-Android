/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.confirmaccountprovider

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.login.impl.R
import io.element.android.features.login.impl.login.LoginModeView
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.atomic.pages.HeaderFooterPage
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.matrix.api.auth.OidcDetails
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 账户提供商确认页面的 Compose UI 组件
 *
 * 遵循 Jetpack Compose 声明式 UI 范式，展示用户选择的账户提供商信息，
 * 并提供继续登录/注册或更改提供商的操作选项
 *
 * 页面功能：
 * - 显示当前选中的 Homeserver 提供商名称和地址
 * - 根据登录/注册模式显示不同的标题和副标题文案
 * - 展示当前登录模式的详细信息（支持密码登录、SSO、OIDC 等）
 * - 提供"继续"按钮触发认证流程
 * - 提供"更改"按钮允许用户重新选择提供商
 *
 * @param state 页面状态数据，由 [ConfirmAccountProviderState] 提供，包含所有 UI 渲染所需信息
 * @param onOidcDetails OIDC 认证详情回调，当用户需要查看或处理 OIDC 认证信息时触发
 * @param onNeedLoginPassword 需要登录密码回调，当用户选择的登录方式需要输入密码时触发
 * @param onLearnMoreClick 了解更多点击回调，用于打开帮助页面或显示更多信息
 * @param onCreateAccountContinue 创建账户继续回调，在注册流程中用户点击继续时触发
 * @param onChange 更改提供商回调，用户点击"更改"按钮时触发，用于重新选择 Homeserver
 * @param modifier 修饰符，用于自定义布局和样式
 */
@Composable
fun ConfirmAccountProviderView(
    /** 页面状态数据，包含账户提供商信息、登录模式、加载状态等 */
    state: ConfirmAccountProviderState,
    /** OIDC 认证详情回调，当登录模式需要 OIDC 认证时由 LoginModeView 触发 */
    onOidcDetails: (OidcDetails) -> Unit,
    /** 需要登录密码回调，当登录方式需要传统密码认证时触发 */
    onNeedLoginPassword: () -> Unit,
    /** 了解更多点击回调，用于显示登录方式相关的帮助信息 */
    onLearnMoreClick: () -> Unit,
    /** 创建账户继续回调，在注册流程中用于继续注册流程 */
    onCreateAccountContinue: (url: String) -> Unit,
    /** 更改提供商回调，允许用户返回选择其他 Homeserver 提供商 */
    onChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    /** 计算是否处于加载状态，用于控制按钮禁用和显示加载动画 */
    val isLoading by remember(state.loginMode) {
        derivedStateOf {
            state.loginMode is AsyncData.Loading
        }
    }
    /** 事件接收器简写，便于在点击事件中调用 */
    val eventSink = state.eventSink

    /**
     * Header 区域 - 顶部标题栏
     *
     * 包含：
     * - 用户图标（使用用户头像图标）
     * - 标题：根据登录/注册模式显示"登录到"或"创建账户到" + 提供商名称
     * - 副标题：显示选择该提供商的原因说明
     */
    HeaderFooterPage(
        modifier = modifier,
        header = {
            IconTitleSubtitleMolecule(
                modifier = Modifier.padding(top = 60.dp),
                iconStyle = BigIcon.Style.Default(CompoundIcons.UserProfileSolid()),
                title = stringResource(
                    id = if (state.isAccountCreation) {
                        R.string.screen_account_provider_signup_title
                    } else {
                        R.string.screen_account_provider_signin_title
                    },
                    state.accountProvider.title
                ),
                subTitle = stringResource(
                    id = if (state.isAccountCreation) {
                        R.string.screen_account_provider_signup_subtitle
                    } else {
                        R.string.screen_account_provider_signin_subtitle
                    },
                )
            )
        },
        /**
         * Footer 区域 - 底部按钮栏
         *
         * 包含：
         * - 主要操作按钮（"继续"），点击触发 Continue 事件
         * - 次要操作按钮（"更改"），点击触发 onChange 回调
         */
        footer = {
            ButtonColumnMolecule {
                /**
                 * 主要继续按钮
                 *
                 * 行为：
                 * - 点击触发 ConfirmAccountProviderEvents.Continue 事件
                 * - 加载状态下禁用并显示进度动画
                 * - 提交启用且不在加载时按钮可用
                 */
                Button(
                    text = stringResource(id = CommonStrings.action_continue),
                    showProgress = isLoading,
                    onClick = { eventSink.invoke(ConfirmAccountProviderEvents.Continue) },
                    enabled = state.submitEnabled || isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.loginContinue)
                )
                /**
                 * 更改提供商按钮
                 *
                 * 行为：
                 * - 点击触发 onChange 回调
                 * - 加载状态下禁用，避免操作冲突
                 */
                TextButton(
                    text = stringResource(id = R.string.screen_account_provider_change),
                    onClick = onChange,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.loginChangeServer)
                )
            }
        }
    ) {
        /**
         * 主内容区域 - 登录模式视图
         *
         * 渲染 [LoginModeView] 组件，展示当前支持的登录方式，
         * 根据 loginMode 状态显示：
         * - 未初始化：显示加载指示器
         * - 加载中：显示加载进度
         * - 成功：显示具体的登录选项（密码、SSO、OIDC 等）
         * - 失败：显示错误信息和重试选项
         */
        LoginModeView(
            loginMode = state.loginMode,
            onClearError = {
                eventSink(ConfirmAccountProviderEvents.ClearError)
            },
            onLearnMoreClick = onLearnMoreClick,
            onOidcDetails = onOidcDetails,
            onNeedLoginPassword = onNeedLoginPassword,
            onCreateAccountContinue = onCreateAccountContinue,
        )
    }
}

/**
 * 账户提供商确认页面的预览组件
 *
 * 用于在 Android Studio 中实时预览 UI 效果，
 * 使用 [ConfirmAccountProviderStateProvider] 提供的多种状态进行日夜模式预览
 *
 * @param state 预览状态数据，由预览参数提供者提供
 */
@PreviewsDayNight
@Composable
internal fun ConfirmAccountProviderViewPreview(
    @PreviewParameter(ConfirmAccountProviderStateProvider::class) state: ConfirmAccountProviderState
) = ElementPreview {
    ConfirmAccountProviderView(
        state = state,
        onOidcDetails = {},
        onNeedLoginPassword = {},
        onCreateAccountContinue = {},
        onLearnMoreClick = {},
        onChange = {},
    )
}
