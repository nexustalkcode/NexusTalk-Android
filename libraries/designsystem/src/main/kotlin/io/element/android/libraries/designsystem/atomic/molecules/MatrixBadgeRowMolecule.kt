/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * Matrix 徽章行分子组件
 *
 * 用于水平排列多个 Matrix 徽章的组件。
 * 徽章之间保持固定间距，自动换行显示。
 * 常用于显示房间状态标签列表，如信任度、加密状态等。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.atomic.atoms.MatrixBadgeAtom
import kotlinx.collections.immutable.ImmutableList

/**
 * Matrix 徽章行组件
 *
 * 创建一个水平排列多个 Matrix 徽章的行布局。
 * 徽章之间保持 8dp 间距，超出宽度自动换行。
 *
 * @param data ImmutableList<MatrixBadgeAtom.MatrixBadgeData> 徽章数据列表
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 *
 * @return Unit
 *
 * @see [MatrixBadgeAtom] Matrix 徽章组件
 * @see [MatrixBadgeAtom.MatrixBadgeData] 徽章数据类
 *
 * @example
 * ```kotlin
 * MatrixBadgeRowMolecule(
 *     data = listOf(
 *         MatrixBadgeAtom.MatrixBadgeData(
 *             text = "信任",
 *             icon = CompoundIcons.Verified(),
 *             type = MatrixBadgeAtom.Type.Positive
 *         )
 *     )
 * )
 * ```
 */
@Composable
fun MatrixBadgeRowMolecule(
    data: ImmutableList<MatrixBadgeAtom.MatrixBadgeData>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (badge in data) {
            MatrixBadgeAtom.View(badge)
        }
    }
}
