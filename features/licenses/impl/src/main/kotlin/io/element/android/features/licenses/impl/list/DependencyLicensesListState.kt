/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.licenses.impl.list

import io.element.android.features.licenses.impl.model.DependencyLicenseItem
import io.element.android.libraries.architecture.AsyncData
import kotlinx.collections.immutable.ImmutableList

/**
 * 依赖项许可证列表状态数据类
 *
 * 表示依赖项许可证列表界面的当前状态。
 *
 * @property licenses 许可证列表的异步数据状态
 * @property filter 当前过滤文本
 * @property eventSink 事件处理函数
 */
data class DependencyLicensesListState(
    val licenses: AsyncData<ImmutableList<DependencyLicenseItem>>,
    val filter: String,
    val eventSink: (DependencyLicensesListEvent) -> Unit,
)
