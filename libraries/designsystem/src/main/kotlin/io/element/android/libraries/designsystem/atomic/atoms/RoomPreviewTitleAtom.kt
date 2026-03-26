/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 房间预览标题原子组件
 *
 * 用于显示房间预览页面中的主标题文本。
 * 文本采用居中对齐，使用大号加粗字重，
 * 适用于房间名称或主要标题的展示。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * 房间预览标题文本组件
 *
 * 创建一个居中显示的标题文本。
 * 文本使用 Element 主题的主要文字颜色和加粗样式。
 *
 * @param title String 标题文本内容
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param fontStyle FontStyle? 字体样式，可选设置斜体等样式，默认为 null
 *
 * @return Unit
 *
 * @see [ElementTheme.typography.fontHeadingLgBold] 标题默认文本样式
 * @see [ElementTheme.colors.textPrimary] 主要文字颜色
 *
 * @example
 * ```kotlin
 * RoomPreviewTitleAtom(title = "Element Room")
 * RoomPreviewTitleAtom(title = "Italic Title", fontStyle = FontStyle.Italic)
 * ```
 */
@Composable
fun RoomPreviewTitleAtom(
    title: String,
    modifier: Modifier = Modifier,
    fontStyle: FontStyle? = null,
) {
    Text(
        modifier = modifier,
        text = title,
        style = ElementTheme.typography.fontHeadingLgBold,
        textAlign = TextAlign.Center,
        fontStyle = fontStyle,
        color = ElementTheme.colors.textPrimary,
    )
}
