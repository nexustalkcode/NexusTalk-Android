/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.reset.password

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction

/**
 * 重置身份密码状态提供器
 *
 * 用于 Compose Preview 的状态提供器，提供不同状态的 [ResetIdentityPasswordState] 示例。
 */
class ResetIdentityPasswordStateProvider : PreviewParameterProvider<ResetIdentityPasswordState> {
    /** 预览状态序列 */
    override val values: Sequence<ResetIdentityPasswordState>
        get() = sequenceOf(
            aResetIdentityPasswordState(),
            aResetIdentityPasswordState(resetAction = AsyncAction.Loading),
            aResetIdentityPasswordState(resetAction = AsyncAction.Success(Unit)),
            aResetIdentityPasswordState(resetAction = AsyncAction.Failure(IllegalStateException("Failed"))),
        )
}

/**
 * 创建重置身份密码状态的辅助函数
 *
 * @param resetAction 重置操作状态
 * @param eventSink 事件处理函数
 * @return 重置身份密码状态实例
 */
private fun aResetIdentityPasswordState(
    resetAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (ResetIdentityPasswordEvent) -> Unit = {},
) = ResetIdentityPasswordState(
    resetAction = resetAction,
    eventSink = eventSink,
)
