/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.enter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.securebackup.impl.R
import io.element.android.features.securebackup.impl.setup.views.RecoveryKeyViewState
import io.element.android.features.securebackup.impl.tools.RecoveryKeyVisualTransformation
import io.element.android.libraries.designsystem.components.async.AsyncActionView
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.button.GradientButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.Surface
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.designsystem.theme.components.TextField
import io.element.android.libraries.designsystem.theme.components.TextFieldValidity
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SecureBackupEnterRecoveryKeyView(
    state: SecureBackupEnterRecoveryKeyState,
    onSuccess: () -> Unit,
    onBackClick: () -> Unit,
    onResetRecoveryKeyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AsyncActionView(
        async = state.submitAction,
        onSuccess = { onSuccess() },
        progressDialog = { },
        errorTitle = { stringResource(id = R.string.screen_recovery_key_confirm_error_title) },
        errorMessage = { stringResource(id = R.string.screen_recovery_key_confirm_error_content) },
        onErrorDismiss = { state.eventSink(SecureBackupEnterRecoveryKeyEvents.ClearDialog) },
    )

    ScaffoldContent(
        state = state,
        onBackClick = onBackClick,
        onResetRecoveryKeyClick = onResetRecoveryKeyClick,
        modifier = modifier,
    )
}

@Composable
private fun ScaffoldContent(
    state: SecureBackupEnterRecoveryKeyState,
    onBackClick: () -> Unit,
    onResetRecoveryKeyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ElementTheme.colors.bgSubtleSecondary)
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            BackButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            HeaderSection()
            HorizontalDivider(modifier = Modifier.height(2.dp), color = ElementTheme.colors.textPrimary.copy(alpha = 0.1f))
            WarningCard()
            RecoveryKeyInputSection(state = state)
            WhyRecoveryKeyCard()
            Spacer(modifier = Modifier.height(8.dp))
            ResetRecoveryKeyAction(onResetRecoveryKeyClick = onResetRecoveryKeyClick)
            Spacer(modifier = Modifier.height(12.dp))
        }

        ActionBar(
            state = state,
            onBackClick = onBackClick,
        )
    }
}

@Composable
private fun ResetRecoveryKeyAction(
    onResetRecoveryKeyClick: () -> Unit,
) {
    TextButton(
        text = stringResource(R.string.screen_recovery_key_confirm_lost_recovery_key),
        onClick = onResetRecoveryKeyClick,
        modifier = Modifier.fillMaxWidth(),
        size = ButtonSize.LargeLowPadding,
    )
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Surface(
            shape = CircleShape,
            color = ElementTheme.colors.bgSubtleSecondary,
        ) {
            Icon(
                modifier = Modifier.size(25.dp,25.dp),
                imageVector = CompoundIcons.ShieldV1(),
                contentDescription = null,
                tint = ElementTheme.colors.textPrimary,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(R.string.screen_recovery_key_confirm_custom_title),
                style = ElementTheme.typography.fontBodyMdRegular.copy(fontSize = 16.sp),
                color = ElementTheme.colors.textPrimary,
            )
            Text(
                text = stringResource(R.string.screen_recovery_key_confirm_custom_subtitle),
                style = ElementTheme.typography.fontBodyMdRegular.copy(fontSize = 11.sp),
                color = ElementTheme.colors.textSecondary,
            )
        }
    }
}

@Composable
private fun WarningCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = ElementTheme.colors.bgCanvasDefault,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = CompoundIcons.ErrorSolid(),
                contentDescription = null,
                tint = ElementTheme.colors.textCriticalPrimary,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.screen_recovery_key_confirm_warning_title),
                    style = ElementTheme.typography.fontBodyLgMedium,
                    color = ElementTheme.colors.textCriticalPrimary,
                )
                Text(
                    text = stringResource(R.string.screen_recovery_key_confirm_warning_message),
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecoveryKeyInputSection(
    state: SecureBackupEnterRecoveryKeyState,
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    var isFocused by remember { mutableStateOf(false) }
    val isImeVisible = WindowInsets.isImeVisible
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isImeVisible, isFocused) {
        if (isImeVisible && isFocused) {
            coroutineScope.launch {
                delay(100.milliseconds)
                bringIntoViewRequester.bringIntoView()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.screen_recovery_key_confirm_input_title),
            style = ElementTheme.typography.fontBodyLgMedium,
        )
        RecoveryKeyTextField(
            modifier = Modifier
                .bringIntoViewRequester(bringIntoViewRequester)
                .onFocusChanged { isFocused = it.isFocused },
            state = state.recoveryKeyViewState,
            onChange = {
                state.eventSink(SecureBackupEnterRecoveryKeyEvents.OnRecoveryKeyChange(it))
            },
            onSubmit = {
                state.eventSink(SecureBackupEnterRecoveryKeyEvents.Submit)
            },
            toggleRecoveryKeyVisibility = {
                state.eventSink(SecureBackupEnterRecoveryKeyEvents.ChangeRecoveryKeyFieldContentsVisibility(it))
            },
        )
    }
}

@Composable
private fun RecoveryKeyTextField(
    state: RecoveryKeyViewState,
    onChange: (String) -> Unit,
    onSubmit: () -> Unit,
    toggleRecoveryKeyVisibility: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.inProgress) {
        toggleRecoveryKeyVisibility(false)
    }
    val keyHasSpace = state.formattedRecoveryKey.orEmpty().contains(" ")
    val visualTransformation = remember(keyHasSpace, state.displayTextFieldContents) {
        if (state.displayTextFieldContents) {
            if (keyHasSpace) {
                VisualTransformation.None
            } else {
                RecoveryKeyVisualTransformation()
            }
        } else {
            PasswordVisualTransformation()
        }
    }

    TextField(
        modifier = modifier.fillMaxWidth(),
        value = state.formattedRecoveryKey.orEmpty(),
        onValueChange = onChange,
        enabled = state.inProgress.not(),
        minLines = 2,
        placeholder = stringResource(id = R.string.screen_recovery_key_confirm_key_placeholder),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onDone = { onSubmit() }
        ),
        validity = TextFieldValidity.None,
        trailingIcon = {
            val image = if (state.displayTextFieldContents) CompoundIcons.VisibilityOn() else CompoundIcons.VisibilityOff()
            Box(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .clickable { toggleRecoveryKeyVisibility(!state.displayTextFieldContents) }
            ) {
                Icon(
                    imageVector = image,
                    contentDescription = if (state.displayTextFieldContents) {
                        stringResource(io.element.android.libraries.ui.strings.CommonStrings.a11y_hide_password)
                    } else {
                        stringResource(io.element.android.libraries.ui.strings.CommonStrings.a11y_show_password)
                    },
                )
            }
        },
    )
}

@Composable
private fun WhyRecoveryKeyCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = ElementTheme.colors.bgCanvasDefault,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = CompoundIcons.Info(),
                    contentDescription = null,
                    tint = ElementTheme.colors.textPrimary,
                )
                Text(
                    text = stringResource(R.string.screen_recovery_key_confirm_why_title),
                    style = ElementTheme.typography.fontBodyLgMedium,
                    color = ElementTheme.colors.textPrimary,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WhyRecoveryKeyBullet(text = stringResource(R.string.screen_recovery_key_confirm_why_point_1))
                WhyRecoveryKeyBullet(text = stringResource(R.string.screen_recovery_key_confirm_why_point_2))
                WhyRecoveryKeyBullet(text = stringResource(R.string.screen_recovery_key_confirm_why_point_3))
                WhyRecoveryKeyBullet(text = stringResource(R.string.screen_recovery_key_confirm_why_point_4))
            }
        }
    }
}

@Composable
private fun WhyRecoveryKeyBullet(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = "\u2022",
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary,
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary,
        )
    }
}

@Composable
private fun ActionBar(
    state: SecureBackupEnterRecoveryKeyState,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GradientButton(
            modifier = Modifier
                .shadow(if (state.isSubmitEnabled) 4.dp else 0.dp, RoundedCornerShape(23.dp))
                .fillMaxWidth()
                .testTag(TestTags.onBoardingSignIn),
            size = ButtonSize.Large,
            text = stringResource(R.string.screen_recovery_key_confirm_submit_action),
            onClick = { state.eventSink(SecureBackupEnterRecoveryKeyEvents.Submit) },
            enabled = state.isSubmitEnabled,
            showProgress = state.submitAction.isLoading(),
            leadingIcon = IconSource.Vector(CompoundIcons.ShieldV1()),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(percent = 50))
                .fillMaxWidth(),
            text = stringResource(R.string.screen_recovery_key_confirm_skip_action),
            onClick = onBackClick,
            size = ButtonSize.LargeLowPadding,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun SecureBackupEnterRecoveryKeyViewPreview(
    @PreviewParameter(SecureBackupEnterRecoveryKeyStateProvider::class) state: SecureBackupEnterRecoveryKeyState
) = ElementPreview {
    SecureBackupEnterRecoveryKeyView(
        state = state,
        onSuccess = {},
        onBackClick = {},
        onResetRecoveryKeyClick = {},
    )
}
