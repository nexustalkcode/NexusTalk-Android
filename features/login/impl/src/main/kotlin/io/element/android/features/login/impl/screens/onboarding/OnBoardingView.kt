/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.login.impl.R
import io.element.android.features.login.impl.login.LoginModeView
import io.element.android.features.login.impl.screens.onboarding.classic.ConfirmingLoginWithElementClassic
import io.element.android.features.login.impl.screens.onboarding.classic.LoginWithClassicEvent
import io.element.android.features.login.impl.screens.onboarding.classic.LoginWithClassicState
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.atomic.atoms.ElementLogoAtom
import io.element.android.libraries.designsystem.atomic.atoms.ElementLogoAtomSize
import io.element.android.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import io.element.android.libraries.designsystem.atomic.pages.FlowStepPage
import io.element.android.libraries.designsystem.atomic.pages.OnBoardingPage
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.async.AsyncActionView
import io.element.android.libraries.designsystem.components.button.GradientButton
import io.element.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.matrix.api.auth.OidcDetails
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 初始页面视图
 *
 * 渲染应用首次启动时的用户界面，包括 Logo、应用名称和登录选项。
 * 支持登录、创建账户、二维码登录等多种登录方式。
 *
 * @param state 初始页面状态
 * @param onBackClick 返回按钮点击事件
 * @param onSignInWithQrCode 二维码登录点击事件
 * @param onSignIn 登录点击事件
 * @param onCreateAccount 创建账户点击事件
 * @param onOidcDetails OIDC 详情事件
 * @param onNeedLoginPassword 需要登录密码事件
 * @param onLearnMoreClick 了解更多点击事件
 * @param onCreateAccountContinue 继续创建账户事件
 * @param onReportProblem 报告问题点击事件
 * @param modifier 修饰符
 */
@Composable
fun OnBoardingView(
    state: OnBoardingState,
    onBackClick: () -> Unit,
    onSignInWithQrCode: () -> Unit,
    onSignIn: (mustChooseAccountProvider: Boolean) -> Unit,
    onCreateAccount: () -> Unit,
    onOidcDetails: (OidcDetails) -> Unit,
    onNeedLoginPassword: () -> Unit,
    onLearnMoreClick: () -> Unit,
    onCreateAccountContinue: (url: String) -> Unit,
    onReportProblem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val loginView = @Composable {
        LoginModeView(
            loginMode = state.loginMode,
            onClearError = {
                state.eventSink(OnBoardingEvents.ClearError)
            },
            onLearnMoreClick = onLearnMoreClick,
            onOidcDetails = onOidcDetails,
            onNeedLoginPassword = onNeedLoginPassword,
            onCreateAccountContinue = onCreateAccountContinue,
        )
    }
    val buttons = @Composable {
        OnBoardingButtons(
            state = state,
            onSignInWithQrCode = onSignInWithQrCode,
            onSignIn = onSignIn,
            onCreateAccount = onCreateAccount,
            onReportProblem = onReportProblem,
        )
    }

    if (state.isAddingAccount) {
        AddOtherAccountScaffold(
            modifier = modifier,
            loginView = loginView,
            buttons = buttons,
            onBackClick = onBackClick,
        )
    } else {
        AddFirstAccountScaffold(
            modifier = modifier,
            state = state,
            loginView = loginView,
            buttons = buttons,
        )
    }

    LoginWithElementClassicView(
        state = state.loginWithClassicState,
    )

    // 网页授权返回后显示加载框
    if (state.loginMode is AsyncData.Loading) {
        ProgressDialog(
            text = stringResource(id = R.string.screen_onboarding_signing_in),
        )
    }
}

@Composable
private fun LoginWithElementClassicView(
    state: LoginWithClassicState,
) {
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        state.eventSink(LoginWithClassicEvent.RefreshData)
    }
    AsyncActionView(
        async = state.loginWithClassicAction,
        confirmationDialog = { confirming ->
            when (confirming) {
                is ConfirmingLoginWithElementClassic -> {
                    // TODO i18n
                    ConfirmationDialog(
                        title = "Sign in with Element Classic",
                        content = "You are signing in as ${confirming.userId} on Element Classic." +
                            " Your existing session on Element Classic will not be signed out. Do you want to continue?",
                        submitText = stringResource(CommonStrings.action_continue),
                        onSubmitClick = { state.eventSink(LoginWithClassicEvent.DoLoginWithClassic) },
                        onDismiss = { state.eventSink(LoginWithClassicEvent.CloseDialog) },
                    )
                }
            }
        },
        onErrorDismiss = {
            state.eventSink(LoginWithClassicEvent.CloseDialog)
        },
        onSuccess = {
            // noop, the view will be closed
        }
    )
}

@Composable
private fun AddFirstAccountScaffold(
    state: OnBoardingState,
    loginView: @Composable () -> Unit,
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    OnBoardingPage(
        modifier = modifier,
        renderBackground = state.onBoardingLogoResId == null,
        content = {
            if (state.onBoardingLogoResId != null) {
                OnBoardingLogo(
                    onBoardingLogoResId = state.onBoardingLogoResId,
                )
            } else {
                OnBoardingContent(state = state)
            }
            loginView()
        },
        footer = {
            buttons()
        }
    )
}

@Composable
private fun AddOtherAccountScaffold(
    loginView: @Composable () -> Unit,
    buttons: @Composable () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowStepPage(
        modifier = modifier,
        title = stringResource(CommonStrings.common_add_account),
        iconStyle = BigIcon.Style.Default(CompoundIcons.HomeSolid()),
        buttons = { buttons() },
        content = loginView,
        onBackClick = onBackClick,
    )
}

@Composable
private fun OnBoardingContent(state: OnBoardingState) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = BiasAlignment(
                horizontalBias = 0f,
                verticalBias = -0.4f
            )
        ) {
            Image(
                modifier = Modifier
                    .size(103.dp),
                painter = painterResource(id = io.element.android.libraries.designsystem.R.drawable.element_logo),
                contentDescription = null
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = BiasAlignment(
                horizontalBias = 0f,
                verticalBias = 0.6f
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.screen_onboarding_welcome_title).format(stringResource(io.element.android.appconfig.R.string.app_name)),
                    color = ElementTheme.colors.textPrimary,
                    style = ElementTheme.typography.fontHeadingLgBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.screen_onboarding_welcome_message, stringResource(io.element.android.appconfig.R.string.app_name)),
                    color = ElementTheme.colors.textSecondary,
                    style = ElementTheme.typography.fontBodyLgRegular.copy(fontSize = 17.sp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun OnBoardingLogo(
    onBoardingLogoResId: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = onBoardingLogoResId),
            contentDescription = null
        )
    }
}

@Composable
private fun OnBoardingButtons(
    state: OnBoardingState,
    onSignInWithQrCode: () -> Unit,
    onSignIn: (mustChooseAccountProvider: Boolean) -> Unit,
    onCreateAccount: () -> Unit,
    onReportProblem: () -> Unit,
) {
    ButtonColumnMolecule {
        val signInButtonStringRes = if (state.canLoginWithQrCode || state.canCreateAccount) {
            R.string.screen_onboarding_sign_in
        } else {
            CommonStrings.action_continue
        }
        if (state.loginWithClassicState.canLoginWithClassic) {
            Button(
                text = "Sign in with Element Classic",
                leadingIcon = IconSource.Vector(CompoundIcons.Mobile()),
                onClick = {
                    state.loginWithClassicState.eventSink(
                        LoginWithClassicEvent.StartLoginWithClassic
                    )
                },
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(percent = 50))
                    .fillMaxWidth(),
            )
        }
        val defaultAccountProvider = state.defaultAccountProvider
        if (defaultAccountProvider == null) {
            GradientButton(
                text = stringResource(id = signInButtonStringRes),
                onClick = {
                    onSignIn(state.mustChooseAccountProvider)
                },
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(23.dp))
                    .fillMaxWidth()
                    .testTag(TestTags.onBoardingSignIn),
                size = ButtonSize.Large,
                cornerRadius = 23.dp,
            )
        } else {
            GradientButton(
                text = stringResource(id = signInButtonStringRes),
                showProgress = state.isSignInLoading,
                onClick = {
                    state.eventSink(OnBoardingEvents.OnSignIn(defaultAccountProvider))
                },
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(23.dp))
                    .fillMaxWidth()
                    .testTag(TestTags.onBoardingSignIn),
                size = ButtonSize.Large,
                cornerRadius = 23.dp,
                enabled = state.submitEnabled || state.isSignInLoading,
            )
        }
        if (state.canCreateAccount) {
            val signUpEnabled = state.submitEnabled || state.isSignInLoading || state.isCreateAccountLoading
            Button(
                text = stringResource(id = R.string.screen_onboarding_sign_up),
                showProgress = state.isCreateAccountLoading,
                onClick = { state.eventSink(OnBoardingEvents.OnCreateAccount) },
                enabled = signUpEnabled,
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(percent = 50))
                    .fillMaxWidth(),
                textGradientColors = if (signUpEnabled) {
                    listOf(
                        Color(0xFF4EF4BE),
                        Color(0xFF018FE7),
                    )
                } else null,
            )
        }
        Spacer(modifier = Modifier.height(120.dp))

    }
}

@PreviewsDayNight
@Composable
internal fun OnBoardingViewPreview(
    @PreviewParameter(OnBoardingStateProvider::class) state: OnBoardingState
) = ElementPreview {
    OnBoardingView(
        state = state,
        onBackClick = {},
        onSignInWithQrCode = {},
        onSignIn = {},
        onCreateAccount = {},
        onReportProblem = {},
        onOidcDetails = {},
        onNeedLoginPassword = {},
        onLearnMoreClick = {},
        onCreateAccountContinue = {},
    )
}
