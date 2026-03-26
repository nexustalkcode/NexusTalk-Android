/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 消息气泡形状类
 *
 * 自定义 Compose Shape，用于绘制带有小箭头的气泡形状。
 * 根据消息是发送方还是接收方，箭头位置不同：
 * - 接收方：箭头在左下角
 * - 发送方：箭头在右下角
 *
 * @property cornerRadius 圆角半径
 * @property arrowWidth 箭头宽度
 * @property arrowHeight 箭头高度
 * @property isMine 是否为自己的消息（决定箭头位置）
 */
class BubbleShape(
    private val cornerRadius: Dp = 12.dp,
    private val arrowWidth: Dp = 12.dp,
    private val arrowHeight: Dp = 6.dp,
    private val isMine: Boolean,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cornerPx = with(density) { cornerRadius.toPx() }
        val arrowWidthPx = with(density) { arrowWidth.toPx() }
        val arrowHeightPx = with(density) { arrowHeight.toPx() }

        val path = Path().apply {
            if (!isMine) {
                // --- 接收方：三角形在左下角 ---
                // 1. 从左侧垂直线开始（左上圆角下方）
                moveTo(arrowWidthPx, cornerPx)
                // 2. 左上圆角
                arcTo(Rect(arrowWidthPx, 0f, arrowWidthPx + 2 * cornerPx, 2 * cornerPx), 180f, 90f, false)
                // 3. 右上圆角
                arcTo(Rect(size.width - 2 * cornerPx, 0f, size.width, 2 * cornerPx), 270f, 90f, false)
                // 4. 右下圆角
                arcTo(Rect(size.width - 2 * cornerPx, size.height - 2 * cornerPx, size.width, size.height), 0f, 90f, false)
                // 5. 连线到三角形右端点 (注意这里是直角区域)
                lineTo(arrowWidthPx + cornerPx, size.height)
                // 6. 绘制三角形：回到最左下角尖端
                lineTo(0f, size.height)
                // 7. 连回左侧垂直线起点（三角形上方）
                lineTo(arrowWidthPx, size.height - arrowHeightPx)
            } else {
                // --- 发送方：三角形在右下角 ---
                // 1. 左上圆角
                moveTo(0f, cornerPx)
                arcTo(Rect(0f, 0f, 2 * cornerPx, 2 * cornerPx), 180f, 90f, false)
                // 2. 右上圆角 (预留出右侧三角形宽度)
                arcTo(Rect(size.width - arrowWidthPx - 2 * cornerPx, 0f, size.width - arrowWidthPx, 2 * cornerPx), 270f, 90f, false)
                // 3. 右侧垂直线至三角形顶点
                lineTo(size.width - arrowWidthPx, size.height - arrowHeightPx)
                // 4. 绘制三角形：连到最右下角尖端
                lineTo(size.width, size.height)
                // 5. 连回底部水平线
                lineTo(size.width - arrowWidthPx - cornerPx, size.height)
                // 6. 左下圆角
                arcTo(Rect(0f, size.height - 2 * cornerPx, 2 * cornerPx, size.height), 90f, 90f, false)
            }
            close()
        }
        return Outline.Generic(path)
    }
}
