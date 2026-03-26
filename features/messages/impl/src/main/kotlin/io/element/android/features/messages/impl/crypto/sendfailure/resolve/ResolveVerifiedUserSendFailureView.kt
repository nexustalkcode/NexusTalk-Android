/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.sendfailure.resolve

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailure
import io.element.android.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.element.android.libraries.designsystem.theme.components.OutlinedButton
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 解决已验证用户发送失败视图
 *
 * 这是一个 Composable 函数，用于渲染解决发送失败提示的底部弹窗。
 * 当加密消息发送失败时，此组件会向用户提供处理失败的选项。
 *
 * 组件功能：
 * - 显示失败原因（未验证设备或身份变更）
 * - 提供"解决并重试"按钮（执行加密操作后重发）
 * - 提供"直接重试"按钮（不解决失败直接重发）
 * - 提供"暂时关闭"按钮（忽略失败）
 *
 * 失败类型对应的操作：
 * - 未验证设备: "验证他们的设备" 或 "忽略并重试"
 * - 身份变更: "固定新身份" 或 "撤回验证并重发"
 *
 * UI 交互：
 * - 使用 ModalBottomSheet 展示底部弹窗
 * - 根据 verifiedUserSendFailure 的变化自动显示/隐藏弹窗
 * - 操作按钮显示加载状态
 *
 * @param state 解决发送失败状态，包含失败信息和操作状态
 * @param modifier 可选的修饰符，用于自定义组件样式和布局
 *
 * @see ResolveVerifiedUserSendFailureState 解决发送失败状态数据类
 * @see ResolveVerifiedUserSendFailureEvents 解决发送失败事件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResolveVerifiedUserSendFailureView(
    state: ResolveVerifiedUserSendFailureState,
    modifier: Modifier = Modifier,
) {
    // 底部弹窗状态管理
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    /**
     * 关闭底部弹窗
     * 触发 Dismiss 事件，让 Presenter 清除解决器状态
     */
    fun dismiss() {
        state.eventSink(ResolveVerifiedUserSendFailureEvents.Dismiss)
    }

    /**
     * 处理重试按钮点击
     * 触发 Retry 事件，直接尝试重新发送消息
     */
    fun onRetryClick() {
        state.eventSink(ResolveVerifiedUserSendFailureEvents.Retry)
    }

    /**
     * 处理解决并重试按钮点击
     * 触发 ResolveAndResend 事件，先解决失败再重发消息
     */
    fun onResolveAndResendClick() {
        state.eventSink(ResolveVerifiedUserSendFailureEvents.ResolveAndResend)
    }

    // 监听失败状态变化，自动显示/隐藏底部弹窗
    LaunchedEffect(state.verifiedUserSendFailure) {
        if (state.verifiedUserSendFailure is VerifiedUserSendFailure.None) {
            sheetState.hide()
            showSheet = false
        } else {
            showSheet = true
        }
    }

    Box(modifier = modifier) {
        if (showSheet) {
            ModalBottomSheet(
                modifier = Modifier
                    .systemBarsPadding()
                    .navigationBarsPadding(),
                sheetState = sheetState,
                onDismissRequest = ::dismiss,
            ) {
                IconTitleSubtitleMolecule(
                    modifier = Modifier.padding(24.dp),
                    title = state.verifiedUserSendFailure.title(),
                    subTitle = state.verifiedUserSendFailure.subtitle(),
                    iconStyle = BigIcon.Style.AlertSolid,
                )
                ButtonColumnMolecule(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        text = state.verifiedUserSendFailure.resolveAction(),
                        showProgress = state.resolveAction.isLoading(),
                        onClick = ::onResolveAndResendClick
                    )
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = CommonStrings.action_retry),
                        showProgress = state.retryAction.isLoading(),
                        onClick = ::onRetryClick
                    )
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = CommonStrings.action_cancel_for_now),
                        onClick = ::dismiss,
                    )
                }
            }
        }
    }
}

/**
 * 获取失败类型的标题文本
 *
 * 根据失败类型返回对应的标题字符串资源。
 *
 * @return 标题字符串
 * - 未验证设备（他人）: "向 {用户} 发送消息失败"
 * - 未验证设备（自己）: "从你的未验证设备发送失败"
 * - 身份变更: "向 {用户} 发送消息失败"
 * - 无失败: 空字符串
 */
@Composable
private fun VerifiedUserSendFailure.title(): String {
    return when (this) {
        is VerifiedUserSendFailure.UnsignedDevice.FromOther -> stringResource(
            id = CommonStrings.screen_resolve_send_failure_unsigned_device_title,
            userDisplayName
        )
        VerifiedUserSendFailure.UnsignedDevice.FromYou -> stringResource(id = CommonStrings.screen_resolve_send_failure_you_unsigned_device_title)
        is VerifiedUserSendFailure.ChangedIdentity -> stringResource(
            id = CommonStrings.screen_resolve_send_failure_changed_identity_title,
            userDisplayName
        )
        VerifiedUserSendFailure.None -> ""
    }
}

/**
 * 获取失败类型的副标题文本
 *
 * 根据失败类型返回对应的副标题字符串资源，提供更详细的问题说明。
 *
 * @return 副标题字符串，包含失败原因的详细说明
 */
@Composable
private fun VerifiedUserSendFailure.subtitle(): String {
    return when (this) {
        is VerifiedUserSendFailure.UnsignedDevice.FromOther -> stringResource(
            id = CommonStrings.screen_resolve_send_failure_unsigned_device_subtitle,
            userDisplayName,
            userDisplayName,
        )
        VerifiedUserSendFailure.UnsignedDevice.FromYou -> stringResource(id = CommonStrings.screen_resolve_send_failure_you_unsigned_device_subtitle)
        is VerifiedUserSendFailure.ChangedIdentity -> stringResource(
            id = CommonStrings.screen_resolve_send_failure_changed_identity_subtitle,
            userDisplayName
        )
        VerifiedUserSendFailure.None -> ""
    }
}

/**
 * 获取解决操作按钮的文本
 *
 * 根据失败类型返回对应的操作按钮文本。
 *
 * @return 操作按钮字符串
 * - 未验证设备: "验证他们的设备" 或 "在设置中验证"
 * - 身份变更: "固定身份" 或 "撤回验证"
 * - 无失败: 空字符串
 */
@Composable
private fun VerifiedUserSendFailure.resolveAction(): String {
    return when (this) {
        is VerifiedUserSendFailure.UnsignedDevice -> stringResource(id = CommonStrings.screen_resolve_send_failure_unsigned_device_primary_button_title)
        is VerifiedUserSendFailure.ChangedIdentity -> stringResource(id = CommonStrings.screen_resolve_send_failure_changed_identity_primary_button_title)
        VerifiedUserSendFailure.None -> ""
    }
}

@PreviewsDayNight
@Composable
internal fun ResolveVerifiedUserSendFailureViewPreview(
    @PreviewParameter(ResolveVerifiedUserSendFailureStateProvider::class) state: ResolveVerifiedUserSendFailureState
) = ElementPreview {
    ResolveVerifiedUserSendFailureView(state)
}
