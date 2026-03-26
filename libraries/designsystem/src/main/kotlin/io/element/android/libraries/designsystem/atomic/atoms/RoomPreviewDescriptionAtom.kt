/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 房间预览描述原子组件
 *
 * 用于显示房间预览页面中的描述文本。
 * 文本采用居中对齐，使用中等常规字重，
 * 适用于房间名称下方的描述信息展示。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * 房间预览描述文本组件
 *
 * 创建一个居中显示的房间描述文本。
 * 文本使用 Element 主题的默认字体样式，自动处理文本溢出。
 *
 * @param description String 描述文本内容
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param maxLines Int 文本最大显示行数，默认为 Int.MAX_VALUE（不限制）
 *
 * @return Unit
 *
 * @see [ElementTheme.typography.fontBodyMdRegular] 默认文本样式
 * @see [TextOverflow.Ellipsis] 文本溢出处理方式
 *
 * @example
 * ```kotlin
 * RoomPreviewDescriptionAtom(
 *     description = "This is a room description",
 *     maxLines = 2
 * )
 * ```
 */
@Composable
fun RoomPreviewDescriptionAtom(
    description: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        modifier = modifier,
        text = description,
        style = ElementTheme.typography.fontBodyMdRegular,
        textAlign = TextAlign.Center,
        color = ElementTheme.colors.textPrimary,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}
