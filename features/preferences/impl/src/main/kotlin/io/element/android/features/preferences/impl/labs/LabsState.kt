/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.labs

import io.element.android.libraries.featureflag.ui.model.FeatureUiModel
import kotlinx.collections.immutable.ImmutableList

/**
 * 实验室功能页面状态数据类
 *
 * @property features 可用的实验功能列表
 * @property isApplyingChanges 是否正在应用更改
 * @property eventSink 事件处理函数
 */
data class LabsState(
    val features: ImmutableList<FeatureUiModel>,
    val isApplyingChanges: Boolean,
    val eventSink: (LabsEvents) -> Unit,
)
