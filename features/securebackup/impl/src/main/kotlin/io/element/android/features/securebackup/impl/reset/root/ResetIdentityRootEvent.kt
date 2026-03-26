/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.reset.root

/**
 * 重置身份根页面事件密封接口
 *
 * 定义了重置身份根页面的用户交互事件。
 */
sealed interface ResetIdentityRootEvent {
    /** 继续事件 */
    data object Continue : ResetIdentityRootEvent

    /** 关闭对话框事件 */
    data object DismissDialog : ResetIdentityRootEvent
}
