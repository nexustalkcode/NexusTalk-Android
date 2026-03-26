/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 播放速度按钮原子组件
 *
 * 用于显示和控制媒体播放速度的按钮组件。
 * 显示当前播放速度倍数（如 1×、1.5×、2×），点击可触发速度切换。
 * 常用于音频播放、视频播放等媒体控制场景。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.messageFromMeBackground

/**
 * 播放速度按钮组件
 *
 * 创建一个显示播放速度倍数的可点击按钮。
 * 按钮显示速度文本（如 0.5×、1×、1.5×、2×），点击时触发 [onClick] 回调。
 * 支持预设速度值和任意浮点数速度值。
 *
 * @param speed Float 当前播放速度倍数
 * @param onClick () -> Unit 点击按钮时的回调函数
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 *
 * @return Unit
 *
 * @see [ElementTheme.typography.fontBodyXsMedium] 按钮文本样式
 * @see [ElementTheme.colors.iconSecondary] 按钮文本颜色
 *
 * @example
 * ```kotlin
 * PlaybackSpeedButton(
 *     speed = 1.5f,
 *     onClick = { /* 切换速度 */ }
 * )
 * ```
 */
@Composable
fun PlaybackSpeedButton(
    speed: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val speedText = when (speed) {
        0.5f -> "0.5×"
        1.0f -> "1×"
        1.5f -> "1.5×"
        2.0f -> "2×"
        else -> "$speed×"
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = ElementTheme.colors.bgCanvasDefault,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = speedText,
            color = ElementTheme.colors.iconSecondary,
            style = ElementTheme.typography.fontBodyXsMedium,
        )
    }
}

/**
 * PlaybackSpeedButton 预览组件
 *
 * 用于在设计预览中展示 PlaybackSpeedButton 组件的各种速度状态。
 * 此预览函数支持日夜两种主题模式，展示 0.5×、1×、1.5×、2× 和 3× 速度按钮。
 */
@PreviewsDayNight
@Composable
internal fun PlaybackSpeedButtonPreview() = ElementPreview {
    Row(
        modifier = Modifier
            .background(ElementTheme.colors.messageFromMeBackground)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.0f).forEach { speed ->
            PlaybackSpeedButton(
                speed = speed,
                onClick = {},
            )
        }
    }
}
