/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.about

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.collections.immutable.toImmutableList

/**
 * 关于页面状态提供者
 *
 * 用于在预览模式下提供关于页面的示例状态数据。
 *
 * @see AboutState 关于页面状态
 */
open class AboutStateProvider : PreviewParameterProvider<AboutState> {
    override val values: Sequence<AboutState>
        get() = sequenceOf(
            anAboutState(),
        )
}

/**
 * 创建示例 AboutState 对象
 *
 * @param elementLegals 法律信息列表，默认为获取所有法律信息
 * @return AboutState 示例状态
 */
fun anAboutState(
    elementLegals: List<ElementLegal> = getAllLegals(),
) = AboutState(
    elementLegals = elementLegals.toImmutableList(),
)
