/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.sessionverification.choosemode

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.ftue.impl.R
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.atomic.atoms.LoadingButtonAtom
import io.element.android.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.atomic.pages.HeaderFooterPage
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.OutlinedButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 选择自验证方式视图组件
 *
 * 这是选择会话验证方式页面的 Compose UI 组件。
 * 展示了身份确认的标题和副标题，并提供多种验证方式供用户选择。
 *
 * @param state 选择自验证方式状态
 * @param onUseAnotherDevice 点击使用另一台设备按钮回调
 * @param onUseRecoveryKey 点击使用恢复密钥按钮回调
 * @param onResetKey 点击重置密钥按钮回调
 * @param onLearnMore 点击了解更多按钮回调
 * @param modifier 修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseSelfVerificationModeView(
    state: ChooseSelfVerificationModeState,
    onBack: () -> Unit,
    onUseAnotherDevice: () -> Unit,
    onUseRecoveryKey: () -> Unit,
    onResetKey: () -> Unit,
    onLearnMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }
    HeaderFooterPage(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    TextButton(
                        text = stringResource(CommonStrings.action_signout),
                        onClick = { state.eventSink(ChooseSelfVerificationModeEvent.SignOut) }
                    )
                }
            )
        },
        header = {
            IconTitleSubtitleMolecule(
                modifier = Modifier.padding(bottom = 16.dp),
                iconStyle = BigIcon.Style.Default(CompoundIcons.LockSolid()),
                title = stringResource(id = R.string.screen_identity_confirmation_title),
                subTitle = stringResource(id = R.string.screen_identity_confirmation_subtitle)
            )
        },
        footer = {
            ChooseSelfVerificationModeButtons(
                state = state,
                onUseAnotherDevice = onUseAnotherDevice,
                onUseRecoveryKey = onUseRecoveryKey,
                onResetKey = onResetKey,
            )
        }
    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center,
//        ) {
//            Text(
//                modifier = Modifier
//                    .clickable(onClick = onLearnMore)
//                    .padding(vertical = 4.dp, horizontal = 16.dp),
//                text = stringResource(CommonStrings.action_learn_more),
//                style = ElementTheme.typography.fontBodyLgMedium
//            )
//        }
    }
}

/**
 * 选择自验证方式按钮组件
 *
 * 根据按钮状态显示不同的操作按钮，包括：
 * - 使用另一台设备验证（如果可用）
 * - 使用恢复密钥验证（如果可用）
 * - 无法确认身份时的重置选项
 *
 * @param state 选择自验证方式状态
 * @param onUseAnotherDevice 点击使用另一台设备按钮回调
 * @param onUseRecoveryKey 点击使用恢复密钥按钮回调
 * @param onResetKey 点击重置密钥按钮回调
 */
@Composable
private fun ChooseSelfVerificationModeButtons(
    state: ChooseSelfVerificationModeState,
    onUseAnotherDevice: () -> Unit,
    onUseRecoveryKey: () -> Unit,
    onResetKey: () -> Unit,
) {
    ButtonColumnMolecule(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        when (state.buttonsState) {
            AsyncData.Uninitialized,
            is AsyncData.Failure,
            is AsyncData.Loading -> {
                LoadingButtonAtom()
            }
            is AsyncData.Success -> {
                if (state.buttonsState.data.canUseAnotherDevice) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.screen_identity_use_another_device),
                        onClick = onUseAnotherDevice,
                    )
                }
                if (state.buttonsState.data.canEnterRecoveryKey) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.screen_session_verification_enter_recovery_key),
                        onClick = onUseRecoveryKey,
                    )
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.screen_identity_confirmation_cannot_confirm),
                    onClick = onResetKey,
                )
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun ChooseSelfVerificationModeViewPreview(
    @PreviewParameter(ChooseSelfVerificationModeStateProvider::class) state: ChooseSelfVerificationModeState
) = ElementPreview {
    ChooseSelfVerificationModeView(
        state = state,
        onBack = {},
        onUseAnotherDevice = {},
        onUseRecoveryKey = {},
        onResetKey = {},
        onLearnMore = {},
    )
}
