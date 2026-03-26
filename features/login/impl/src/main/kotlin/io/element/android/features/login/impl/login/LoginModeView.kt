/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.features.login.impl.R
import io.element.android.features.login.impl.dialogs.SlidingSyncNotSupportedDialog
import io.element.android.features.login.impl.error.ChangeServerError
import io.element.android.features.login.impl.screens.createaccount.AccountCreationNotSupported
import io.element.android.libraries.androidutils.system.openGooglePlay
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.LocalBuildMeta
import io.element.android.libraries.matrix.api.auth.AuthenticationException
import io.element.android.libraries.matrix.api.auth.OidcDetails
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 登录模式视图组件
 *
 * 根据不同的登录模式状态显示相应的界面：
 * - 加载状态：显示加载指示器
 * - 成功状态：根据登录模式类型导航到相应的登录页面
 * - 失败状态：显示相应的错误对话框
 *
 * @param loginMode 当前登录模式状态，包含登录类型或错误信息
 * @param onClearError 清除错误状态的回调函数
 * @param onLearnMoreClick 点击"了解更多"按钮的回调函数，用于显示Sliding Sync相关信息
 * @param onOidcDetails OIDC登录详情回调，当登录模式为OIDC时触发
 * @param onNeedLoginPassword 需要密码登录回调，当登录模式为密码登录时触发
 * @param onCreateAccountContinue 继续创建账户回调，当登录模式为账户创建时触发，参数为创建账户的URL
 */
@Composable
fun LoginModeView(
    loginMode: AsyncData<LoginMode>,
    onClearError: () -> Unit,
    onLearnMoreClick: () -> Unit,
    onOidcDetails: (OidcDetails) -> Unit,
    onNeedLoginPassword: () -> Unit,
    onCreateAccountContinue: (url: String) -> Unit
) {
    val context = LocalContext.current
    when (loginMode) {
        is AsyncData.Failure -> {
            when (val error = loginMode.error) {
                is ChangeServerError -> {
                    when (error) {
                        ChangeServerError.InvalidServer ->
                            ErrorDialog(
                                content = stringResource(R.string.screen_change_server_error_invalid_homeserver),
                                onSubmit = onClearError,
                            )
                        is ChangeServerError.UnsupportedServer -> {
                            ErrorDialog(
                                content = stringResource(R.string.screen_login_error_unsupported_authentication),
                                onSubmit = onClearError,
                            )
                        }
                        is ChangeServerError.Error -> {
                            ErrorDialog(
                                content = error.messageStr ?: stringResource(CommonStrings.error_unknown),
                                onSubmit = onClearError,
                            )
                        }
                        is ChangeServerError.SlidingSyncAlert -> {
                            SlidingSyncNotSupportedDialog(
                                onLearnMoreClick = {
                                    onLearnMoreClick()
                                    onClearError()
                                },
                                onDismiss = onClearError,
                            )
                        }
                        is ChangeServerError.NeedElementPro -> {
                            ConfirmationDialog(
                                title = stringResource(R.string.screen_change_server_error_element_pro_required_title),
                                content = stringResource(
                                    R.string.screen_change_server_error_element_pro_required_message,
                                    error.unauthorisedAccountProviderTitle,
                                ),
                                submitText = stringResource(R.string.screen_change_server_error_element_pro_required_action_android),
                                onSubmitClick = {
                                    context.openGooglePlay(error.applicationId)
                                    onClearError()
                                },
                                onDismiss = onClearError,
                            )
                        }
                        is ChangeServerError.UnauthorizedAccountProvider -> {
                            ErrorDialog(
                                content = stringResource(
                                    id = R.string.screen_change_server_error_unauthorized_homeserver,
                                    LocalBuildMeta.current.applicationName,
                                    error.unauthorisedAccountProviderTitle,
                                ),
                                onSubmit = onClearError,
                            )
                        }
                    }
                }
                is AccountCreationNotSupported -> {
                    ErrorDialog(
                        content = stringResource(CommonStrings.error_account_creation_not_possible),
                        onSubmit = onClearError,
                    )
                }
                is AuthenticationException.AccountAlreadyLoggedIn -> {
                    ErrorDialog(
                        content = stringResource(CommonStrings.error_account_already_logged_in, error.userId),
                        onSubmit = onClearError,
                    )
                }
                else -> {
                    ErrorDialog(
                        content = stringResource(CommonStrings.error_unknown),
                        onSubmit = onClearError,
                    )
                }
            }
        }
        is AsyncData.Loading -> Unit // The Continue button shows the loading state
        is AsyncData.Success -> {
            when (val loginModeData = loginMode.data) {
                is LoginMode.Oidc -> onOidcDetails(loginModeData.oidcDetails)
                LoginMode.PasswordLogin -> onNeedLoginPassword()
                is LoginMode.AccountCreation -> onCreateAccountContinue(loginModeData.url)
            }
            // Also clear the data, to let the next screen be able to go back
            onClearError()
        }
        AsyncData.Uninitialized -> Unit
    }
}

/**
 * 登录模式视图预览组件
 *
 * 用于在IDE预览中展示登录模式视图的错误状态展示效果
 *
 * @param error 用于预览的错误类型，测试不同错误对话框的显示效果
 */
@PreviewsDayNight
@Composable
internal fun LoginModeViewPreview(@PreviewParameter(LoginModeViewErrorProvider::class) error: Throwable) {
    ElementPreview {
        LoginModeView(
            loginMode = AsyncData.Failure(error),
            onClearError = {},
            onLearnMoreClick = {},
            onOidcDetails = {},
            onNeedLoginPassword = {},
            onCreateAccountContinue = {}
        )
    }
}
