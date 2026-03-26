/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 房间预览副标题原子组件
 *
 * 用于显示房间预览页面中的副标题文本。
 * 文本采用居中对齐，使用大号常规字重，
 * 适用于房间标题下方的次要信息展示。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * 房间预览副标题文本组件
 *
 * 创建一个居中显示的副标题文本。
 * 文本使用 Element 主题的次要文字颜色。
 *
 * @param subtitle String 副标题文本内容
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 *
 * @return Unit
 *
 * @see [ElementTheme.typography.fontBodyLgRegular] 副标题默认文本样式
 * @see [ElementTheme.colors.textSecondary] 次要文字颜色
 *
 * @example
 * ```kotlin
 * RoomPreviewSubtitleAtom(subtitle = "Join this room to participate")
 * ```
 */
@Composable
fun RoomPreviewSubtitleAtom(subtitle: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = subtitle,
        style = ElementTheme.typography.fontBodyLgRegular,
        textAlign = TextAlign.Center,
        color = ElementTheme.colors.textSecondary,
    )
}
