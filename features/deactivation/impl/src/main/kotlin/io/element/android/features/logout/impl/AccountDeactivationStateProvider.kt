/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction

/**
 * 账户停用状态提供者
 *
 * 用于 Compose Preview 的状态提供者，提供不同状态的账户停用界面预览。
 * 继承自 PreviewParameterProvider，为预览功能提供多种状态场景。
 */
open class AccountDeactivationStateProvider : PreviewParameterProvider<AccountDeactivationState> {
    /** 已填写的表单状态，包含擦除数据和密码 */
    private val filledForm = aDeactivateFormState(eraseData = true, password = "password")

    /**
     * 提供预览用的状态序列
     *
     * 包含多种场景：初始状态、填写表单状态、确认中、加载中、失败状态
     */
    override val values: Sequence<AccountDeactivationState>
        get() = sequenceOf(
            // 初始状态 - 空表单
            anAccountDeactivationState(),
            // 已填写表单状态
            anAccountDeactivationState(
                deactivateFormState = filledForm
            ),
            // 确认对话框显示状态
            anAccountDeactivationState(
                deactivateFormState = filledForm,
                accountDeactivationAction = AsyncAction.ConfirmingNoParams,
            ),
            // 加载中状态
            anAccountDeactivationState(
                deactivateFormState = filledForm,
                accountDeactivationAction = AsyncAction.Loading
            ),
            // 失败状态
            anAccountDeactivationState(
                deactivateFormState = filledForm,
                accountDeactivationAction = AsyncAction.Failure(Exception("Failed to deactivate account"))
            ),
        )
}

/**
 * 创建停用表单状态的辅助函数
 *
 * @property eraseData 是否擦除数据，默认为 false
 * @property password 账户密码，默认为空字符串
 * @return DeactivateFormState 停用表单状态
 */
internal fun aDeactivateFormState(
    eraseData: Boolean = false,
    password: String = "",
) = DeactivateFormState(
    eraseData = eraseData,
    password = password,
)

/**
 * 创建账户停用状态的辅助函数
 *
 * @param deactivateFormState 停用表单状态，默认为默认表单状态
 * @param accountDeactivationAction 异步操作状态，默认为未初始化
 * @param eventSink 事件处理函数，默认为空函数
 * @return AccountDeactivationState 账户停用状态
 */
internal fun anAccountDeactivationState(
    deactivateFormState: DeactivateFormState = aDeactivateFormState(),
    accountDeactivationAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (AccountDeactivationEvents) -> Unit = {},
) = AccountDeactivationState(
    deactivateFormState = deactivateFormState,
    accountDeactivationAction = accountDeactivationAction,
    eventSink = eventSink,
)
