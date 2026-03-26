/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.changeserver

import io.element.android.libraries.architecture.AsyncData

/**
 * 更改服务器状态数据类
 *
 * @property changeServerAction 更改服务器操作的异步状态
 * @property eventSink 事件处理函数
 */
data class ChangeServerState(
    val changeServerAction: AsyncData<Unit>,
    val eventSink: (ChangeServerEvents) -> Unit
)
