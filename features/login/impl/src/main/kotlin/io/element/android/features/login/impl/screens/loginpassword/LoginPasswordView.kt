/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.loginpassword

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.login.impl.R
import io.element.android.features.login.impl.error.loginError
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.components.form.textFieldState
import io.element.android.libraries.designsystem.modifiers.onTabOrEnterKeyFocusNext
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.TextField
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 密码登录页面组件
 *
 * 提供用户名和密码登录功能，支持：
 * - 用户名/邮箱输入（支持自动填充）
 * - 密码输入（支持显示/隐藏切换）
 * - 表单提交处理
 * - 登录错误提示
 *
 * 页面会根据登录状态显示加载指示器，并在登录失败时显示错误对话框。
 *
 * @param state 登录页面状态对象，包含表单数据、账户提供商信息和登录状态
 * @param onBackClick 返回按钮点击回调，用于返回上一页面
 * @param modifier 组件修饰符，用于自定义布局和样式
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPasswordView(
    state: LoginPasswordState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val autofillManager = LocalAutofillManager.current

    BackHandler {
        autofillManager?.cancel()
        onBackClick()
    }

    val isLoading by remember(state.loginAction) {
        derivedStateOf {
            state.loginAction is AsyncData.Loading
        }
    }
    val focusManager = LocalFocusManager.current

    fun submit() {
        // Clear focus to prevent keyboard issues with textfields
        focusManager.clearFocus(force = true)

        autofillManager?.commit()

        state.eventSink(LoginPasswordEvents.Submit)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton(onClick = {
                        autofillManager?.cancel()
                        onBackClick()
                    })
                },
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(padding)
                .consumeWindowInsets(padding)
                .verticalScroll(state = scrollState)
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        ) {
            // Title
            IconTitleSubtitleMolecule(
                modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp),
                iconStyle = BigIcon.Style.Default(CompoundIcons.UserProfileSolid()),
                title = stringResource(
                    id = R.string.screen_account_provider_signin_title,
                    state.accountProvider.title
                ),
                subTitle = stringResource(id = R.string.screen_login_subtitle)
            )
            Spacer(Modifier.height(40.dp))
            LoginForm(
                state = state,
                isLoading = isLoading,
                onSubmit = ::submit
            )
            // Min spacing
            Spacer(Modifier.height(24.dp))
            // Flexible spacing to keep the submit button at the bottom
            Spacer(modifier = Modifier.weight(1f))
            // Submit
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                ButtonColumnMolecule {
                    Button(
                        text = stringResource(CommonStrings.action_continue),
                        showProgress = isLoading,
                        onClick = ::submit,
                        enabled = state.submitEnabled || isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(TestTags.loginContinue)
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            if (state.loginAction is AsyncData.Failure) {
                LoginErrorDialog(error = state.loginAction.error, onDismiss = {
                    state.eventSink(LoginPasswordEvents.ClearError)
                })
            }
        }
    }
}

/**
 * 登录表单组件
 *
 * 渲染用户名和密码输入字段，包含输入验证和状态管理。
 * 表单字段支持键盘导航（Tab/Enter键）和自动填充功能。
 *
 * @param state 登录页面状态对象，提供表单数据和事件处理
 * @param isLoading 是否处于加载状态，加载时禁用输入字段
 * @param onSubmit 表单提交回调，触发登录操作
 */
@Composable
private fun LoginForm(
    state: LoginPasswordState,
    isLoading: Boolean,
    onSubmit: () -> Unit,
) {
    var loginFieldState by textFieldState(stateValue = state.formState.login)
    var passwordFieldState by textFieldState(stateValue = state.formState.password)

    val focusManager = LocalFocusManager.current
    val eventSink = state.eventSink

    Column {
        TextField(
            label = stringResource(R.string.screen_login_form_header),
            value = loginFieldState,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .onTabOrEnterKeyFocusNext(focusManager)
                .testTag(TestTags.loginEmailUsername)
                .semantics {
                    contentType = ContentType.Username
                },
            placeholder = stringResource(CommonStrings.common_username),
            onValueChange = {
                val sanitized = it.sanitize()
                loginFieldState = sanitized
                eventSink(LoginPasswordEvents.SetLogin(sanitized))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            singleLine = true,
            trailingIcon = if (loginFieldState.isNotEmpty()) {
                {
                    Box(Modifier.clickable {
                        loginFieldState = ""
                        eventSink(LoginPasswordEvents.SetLogin(""))
                    }) {
                        Icon(
                            imageVector = CompoundIcons.Close(),
                            contentDescription = stringResource(CommonStrings.action_clear),
                            tint = ElementTheme.colors.iconSecondary
                        )
                    }
                }
            } else {
                null
            },
        )
        var passwordVisible by remember { mutableStateOf(false) }
        if (state.loginAction is AsyncData.Loading) {
            // Ensure password is hidden when user submits the form
            passwordVisible = false
        }
        Spacer(Modifier.height(20.dp))
        TextField(
            value = passwordFieldState,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .onTabOrEnterKeyFocusNext(focusManager)
                .testTag(TestTags.loginPassword)
                .semantics {
                    contentType = ContentType.Password
                },
            onValueChange = {
                val sanitized = it.sanitize()
                passwordFieldState = sanitized
                eventSink(LoginPasswordEvents.SetPassword(sanitized))
            },
            placeholder = stringResource(CommonStrings.common_password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image =
                    if (passwordVisible) CompoundIcons.VisibilityOn() else CompoundIcons.VisibilityOff()
                val description =
                    if (passwordVisible) stringResource(CommonStrings.a11y_hide_password) else stringResource(CommonStrings.a11y_show_password)
                Box(Modifier.clickable { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription = description,
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { onSubmit() }
            ),
            singleLine = true,
        )
    }
}

/**
 * 清理输入字符串
 *
 * 移除字符串中所有换行符，防止粘贴内容时引入非法字符。
 * 换行符可能导致表单验证或提交时出现问题。
 *
 * @return 清理后的字符串，不包含任何换行符
 */
private fun String.sanitize(): String {
    return replace("\n", "")
}

/**
 * 登录错误对话框组件
 *
 * 显示登录失败时的错误信息，对话框包含标题和错误描述。
 *
 * @param error 登录失败的异常对象，用于获取错误信息
 * @param onDismiss 对话框关闭回调，用于清除错误状态
 */
@Composable
private fun LoginErrorDialog(error: Throwable, onDismiss: () -> Unit) {
    ErrorDialog(
        title = stringResource(id = CommonStrings.dialog_title_error),
        content = stringResource(loginError(error)),
        onSubmit = onDismiss
    )
}

@PreviewsDayNight
@Composable
internal fun LoginPasswordViewPreview(@PreviewParameter(LoginPasswordStateProvider::class) state: LoginPasswordState) = ElementPreview {
    LoginPasswordView(
        state = state,
        onBackClick = {},
    )
}
