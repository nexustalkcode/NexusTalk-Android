/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 编号列表有机体组件
 *
 * 用于在可滚动列表中显示多个编号列表项的复合组件。
 * 每个列表项包含编号和富文本内容，自动排列。
 * 适用于条款说明、操作步骤等需要编号的场景。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.atomic.molecules.NumberedListMolecule
import kotlinx.collections.immutable.ImmutableList

/**
 * 编号列表组件
 *
 * 创建一个可滚动的编号列表。
 * 使用 LazyColumn 实现懒加载，支持大量列表项。
 *
 * @param items ImmutableList<AnnotatedString> 列表项文本内容列表
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 *
 * @return Unit
 *
 * @see [NumberedListMolecule] 编号列表项组件
 *
 * @example
 * ```kotlin
 * NumberedListOrganism(
 *     items = persistentListOf(
 *         "第一步操作".toAnnotatedString(),
 *         "第二步操作".toAnnotatedString()
 *     )
 * )
 * ```
 */
@Composable
fun NumberedListOrganism(
    items: ImmutableList<AnnotatedString>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        itemsIndexed(items) { index, item ->
            NumberedListMolecule(index = index + 1, text = item)
        }
    }
}
