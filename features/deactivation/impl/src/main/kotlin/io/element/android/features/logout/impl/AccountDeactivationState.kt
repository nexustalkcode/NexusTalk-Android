/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl

import android.os.Parcelable
import io.element.android.libraries.architecture.AsyncAction
import kotlinx.parcelize.Parcelize

/**
 * 账户停用状态数据类
 *
 * 表示账户停用界面的完整状态，包含停用表单状态和停用操作的异步状态。
 *
 * @property deactivateFormState 停用表单状态
 * @property accountDeactivationAction 账户停用操作的异步状态
 * @property eventSink 事件处理函数
 */
data class AccountDeactivationState(
    /** 停用表单状态，包含是否擦除数据和密码 */
    val deactivateFormState: DeactivateFormState,
    /** 账户停用操作的异步状态（未初始化/确认中/加载中/成功/失败） */
    val accountDeactivationAction: AsyncAction<Unit>,
    /** 事件处理函数，用于将用户操作传递给 Presenter */
    val eventSink: (AccountDeactivationEvents) -> Unit,
) {
    /** 是否可以提交表单 */
    val submitEnabled: Boolean
        get() = accountDeactivationAction is AsyncAction.Uninitialized &&
            deactivateFormState.password.isNotEmpty()
}

/**
 * 停用表单状态数据类
 *
 * @property eraseData 是否擦除数据
 * @property password 账户密码
 */
@Parcelize
data class DeactivateFormState(
    /** 是否在停用账户时擦除所有消息数据 */
    val eraseData: Boolean,
    /** 账户密码，用于验证用户身份 */
    val password: String
) : Parcelable {
    companion object {
        /** 默认停用表单状态 */
        val Default = DeactivateFormState(false, "")
    }
}
