/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 计数器原子组件
 *
 * 用于显示数字的圆形计数器组件，支持正常和关键两种颜色样式。
 * 常用于显示未读消息数量、通知计数等场景。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.ext.angledGradient
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.text.toDp
import io.element.android.libraries.designsystem.theme.components.Text

private const val MAX_COUNT = 99
private const val MAX_COUNT_STRING = "+$MAX_COUNT"

/**
 * A counter atom that displays a number in a circle.
 * Figma link : https://www.figma.com/design/G1xy0HDZKJf5TCRFmKb5d5/Compound-Android-Components?node-id=2805-2649&m=dev
 *
 * @param count The number to display. If the number is greater than [MAX_COUNT], the counter will display [MAX_COUNT_STRING].
 * If the number is less than 1, the counter will not be displayed.
 * @param modifier The modifier to apply to this layout.
 * @param textStyle The style to apply to the text inside the counter.
 * @param isCritical If true, the counter will use a critical color scheme, otherwise it will use an accent color scheme.
 */
@Composable
fun CounterAtom(
    count: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = CounterAtomDefaults.textStyle,
    isCritical: Boolean = false,
) {
    if (count < 1) return
    val countAsText = when (count) {
        in 0..MAX_COUNT -> count.toString()
        else -> MAX_COUNT_STRING
    }
    val textMeasurer = rememberTextMeasurer()
    // Measure the maximum count string size
    val textLayoutResult = textMeasurer.measure(
        text = MAX_COUNT_STRING,
        style = textStyle
    )
    val textSize = textLayoutResult.size
    val squareSize = maxOf(textSize.width, textSize.height)
    Box(
        modifier = modifier
            .size(squareSize.toDp() + 1.dp)
            .clip(CircleShape)
            .angledGradient(
                colorStops = if (isCritical) {
                    arrayOf(
                        0f to ElementTheme.colors.iconCriticalPrimary,
                        1f to ElementTheme.colors.iconCriticalPrimary
                    )
                } else {
                    arrayOf(
                        0f to ElementTheme.colors.gradientBrandStop1,
                        1f to ElementTheme.colors.gradientBrandStop2
                    )
                },
                degrees = 89f
            )
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = countAsText,
            style = textStyle,
            color = ElementTheme.colors.textOnSolidPrimary,
        )
    }
}

/**
 * CounterAtom 样式默认值
 *
 * 提供 CounterAtom 组件的默认样式配置。
 */
object CounterAtomDefaults {
    /**
     * 默认文本样式
     *
     * 获取计数器组件文本的默认样式，使用 Element 主题的中等字体大小和中等字重。
     *
     * @return TextStyle 默认的文本样式配置
     */
    val textStyle: TextStyle
        @Composable get() = ElementTheme.typography.fontBodyMdMedium
}

@PreviewsDayNight
@Composable
internal fun CounterAtomPreview() = ElementPreview {
    Column(verticalArrangement = spacedBy(2.dp)) {
        CounterAtom(count = 0)
        CounterAtom(count = 4)
        CounterAtom(count = 99)
        CounterAtom(count = 100)
        CounterAtom(count = 4, isCritical = true)
    }
}
