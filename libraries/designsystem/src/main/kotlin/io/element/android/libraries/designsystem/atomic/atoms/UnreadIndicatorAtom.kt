/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 未读指示器原子组件
 *
 * 用于显示未读消息或内容指示的小圆点。
 * 采用圆形设计，可自定义大小、颜色和可见性。
 * 常用于消息列表中标识未读会话或消息。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.ext.angledGradient
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.unreadIndicator

/**
 * 未读指示器组件
 *
 * 创建一个圆形未读状态指示器。
 * 根据 [isVisible] 参数控制显示或隐藏，支持自定义大小和颜色。
 * 当 [count] 有值时，显示带数字的胶囊形状徽章。
 *
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param size Dp 指示器的直径大小，默认为 12.dp
 * @param color Color 指示器的背景颜色，默认为 [ElementTheme.colors.unreadIndicator]
 * @param isVisible Boolean 是否可见，默认为 true，false 时显示透明背景
 * @param contentDescription String? 无障碍描述文本，默认为 null
 * @param count Int? 未读数量，当有值时显示带数字的徽章（数量 > 99 时显示 "99+"）
 *
 * @return Unit
 *
 * @see [ElementTheme.colors.unreadIndicator] 默认未读指示器颜色
 *
 * @example
 * ```kotlin
 * UnreadIndicatorAtom(
 *     size = 8.dp,
 *     color = Color.Red,
 *     isVisible = true
 * )
 * ```
 *
 * @example
 * ```kotlin
 * UnreadIndicatorAtom(
 *     count = 150  // 显示 "99+"
 * )
 * ```
 */
@Composable
fun UnreadIndicatorAtom(
    modifier: Modifier = Modifier,
    size: Dp = 12.dp,
    color: Color = ElementTheme.colors.unreadIndicator,
    isVisible: Boolean = true,
    contentDescription: String? = null,
    count: Int? = null,
) {
    if (!isVisible) {
        return
    }

    val displayText = count?.let {
        if (it > 99) "99+" else it.toString()
    }

    if (displayText != null) {
        // 胶囊形状徽章样式
        Text(
            modifier = modifier
                .semantics {
                    contentDescription?.let { this.contentDescription = it }
                }
                .height(size)
                .defaultMinSize(minWidth = size)
                .clip(RoundedCornerShape(size))
                .angledGradient(
                    colorStops = arrayOf(
                        0f to ElementTheme.colors.gradientBrandStop1,
                        1f to ElementTheme.colors.gradientBrandStop2
                    ),
                    degrees = 89f
                )
                .padding(horizontal = 3.dp),
            text = displayText,
            style = ElementTheme.typography.fontBodyMdRegular.copy(lineHeight = 12.sp),
            color = Color.White,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    } else {
        // 原有圆点样式
        Box(
            modifier = modifier
                .semantics {
                    contentDescription?.let { this.contentDescription = it }
                }
                .size(size)
                .clip(CircleShape)
                .angledGradient(
                    colorStops = arrayOf(
                        0f to ElementTheme.colors.gradientBrandStop1,
                        1f to ElementTheme.colors.gradientBrandStop2
                    ),
                    degrees = 89f
                )
        )
    }
}

/**
 * UnreadIndicatorAtom 预览组件
 *
 * 用于在设计预览中展示 UnreadIndicatorAtom 组件的默认状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun UnreadIndicatorAtomPreview() = ElementPreview {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        // 圆点样式
        UnreadIndicatorAtom()
        // 带数量的徽章
        UnreadIndicatorAtom(count = 5)
        // 数量超过99
        UnreadIndicatorAtom(count = 150)
    }
}
