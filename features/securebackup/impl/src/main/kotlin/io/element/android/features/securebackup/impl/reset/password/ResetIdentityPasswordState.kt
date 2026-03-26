/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl.reset.password

import io.element.android.libraries.architecture.AsyncAction

/**
 * 重置身份密码状态数据类
 *
 * @property resetAction 重置操作的异步状态
 * @property eventSink 事件处理函数
 */
data class ResetIdentityPasswordState(
    /** 重置操作的异步状态 */
    val resetAction: AsyncAction<Unit>,
    /** 事件处理函数 */
    val eventSink: (ResetIdentityPasswordEvent) -> Unit,
)
