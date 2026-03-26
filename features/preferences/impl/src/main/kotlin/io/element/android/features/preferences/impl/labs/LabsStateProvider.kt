/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.labs

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.designsystem.icons.CompoundDrawables
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.featureflag.ui.model.FeatureUiModel
import kotlinx.collections.immutable.toImmutableList

/**
 * 实验室功能状态提供者
 *
 * 用于在预览模式下提供实验室功能页面的示例状态数据。
 *
 * @see LabsState 实验室功能状态
 */
internal class LabsStateProvider : PreviewParameterProvider<LabsState> {
    override val values: Sequence<LabsState>
        get() = sequenceOf(
            aLabsState(features = aFeatureList()),
            aLabsState(features = aFeatureList(), isApplyingChanges = true),
        )
}

/**
 * 创建示例 LabsState 对象
 *
 * @param features 功能列表
 * @param isApplyingChanges 是否正在应用更改
 * @return LabsState 示例状态
 */
internal fun aLabsState(
    features: List<FeatureUiModel> = emptyList(),
    isApplyingChanges: Boolean = false,
) = LabsState(
    features = features.toImmutableList(),
    isApplyingChanges = isApplyingChanges,
    eventSink = {},
)

/**
 * 创建示例功能列表
 *
 * @return 功能 UI 模型列表
 */
internal fun aFeatureList() = listOf(
    FeatureUiModel(
        key = "feature_1",
        title = "Feature 1",
        description = "This is a description of feature 1.",
        isEnabled = true,
        icon = IconSource.Resource(CompoundDrawables.ic_compound_threads),
    ),
    FeatureUiModel(
        key = "feature_2",
        title = "Feature 2",
        description = "This is a description of feature 2.",
        isEnabled = false,
        icon = IconSource.Resource(CompoundDrawables.ic_compound_video_call),
    )
)
