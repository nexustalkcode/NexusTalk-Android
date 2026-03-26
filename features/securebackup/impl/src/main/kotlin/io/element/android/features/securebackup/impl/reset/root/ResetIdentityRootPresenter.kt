/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.reset.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.element.android.libraries.architecture.Presenter

/**
 * 重置身份根页面 Presenter
 *
 * 负责处理重置身份根页面的业务逻辑。
 * 管理确认对话框的显示状态。
 *
 * @see ResetIdentityRootState 重置身份根页面状态
 */
class ResetIdentityRootPresenter : Presenter<ResetIdentityRootState> {
    @Composable
    override fun present(): ResetIdentityRootState {
        var displayConfirmDialog by remember { mutableStateOf(false) }

        fun handleEvent(event: ResetIdentityRootEvent) {
            displayConfirmDialog = when (event) {
                ResetIdentityRootEvent.Continue -> true
                ResetIdentityRootEvent.DismissDialog -> false
            }
        }

        return ResetIdentityRootState(
            displayConfirmationDialog = displayConfirmDialog,
            eventSink = ::handleEvent,
        )
    }
}
