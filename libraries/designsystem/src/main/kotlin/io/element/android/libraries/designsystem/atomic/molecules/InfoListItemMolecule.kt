/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 信息列表项分子组件
 *
 * 用于显示信息列表中的单个条目。
 * 支持不同的位置样式（顶部、中间、底部、单独），并自动处理圆角效果。
 * 可选包含图标，图标和消息内容水平排列。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * 列表项位置枚举
 *
 * 定义 InfoListItemMolecule 组件在列表中的位置。
 * 不同位置会影响圆角样式的处理。
 */
enum class InfoListItemPosition {
    /** 列表顶部位置 - 显示顶部圆角 */
    Top,
    /** 列表中间位置 - 不显示圆角 */
    Middle,
    /** 列表底部位置 - 显示底部圆角 */
    Bottom,
    /** 单独项位置 - 显示全圆角 */
    Single,
}

/**
 * 信息列表项组件
 *
 * 创建一个信息列表中的单个条目。
 * 条目包含可选图标和消息内容，水平排列。
 * 根据 [position] 参数自动应用相应的圆角样式。
 *
 * @param message @Composable () -> Unit 消息内容区域
 * @param position InfoListItemPosition 列表项位置，决定圆角样式
 * @param backgroundColor Color 背景颜色
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param icon @Composable () -> Unit 图标内容区域，默认为空
 *
 * @return Unit
 *
 * @see [InfoListItemPosition] 列表项位置枚举
 *
 * @example
 * ```kotlin
 * InfoListItemMolecule(
 *     message = { Text("信息内容") },
 *     icon = { Icon(CompoundIcons.InfoSolid()) },
 *     position = InfoListItemPosition.Single,
 *     backgroundColor = ElementTheme.colors.bgSubtleSecondary
 * )
 * ```
 */
@Composable
fun InfoListItemMolecule(
    message: @Composable () -> Unit,
    position: InfoListItemPosition,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {},
) {
    val radius = 14.dp
    val backgroundShape = remember(position) {
        when (position) {
            InfoListItemPosition.Single -> RoundedCornerShape(radius)
            InfoListItemPosition.Top -> RoundedCornerShape(topStart = radius, topEnd = radius)
            InfoListItemPosition.Middle -> RoundedCornerShape(0.dp)
            InfoListItemPosition.Bottom -> RoundedCornerShape(bottomStart = radius, bottomEnd = radius)
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = backgroundShape,
            )
            .padding(vertical = 12.dp, horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        icon()
        message()
    }
}

/**
 * InfoListItemMolecule 预览组件
 *
 * 用于在设计预览中展示 InfoListItemMolecule 组件的各种位置状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun InfoListItemMoleculePreview() {
    ElementPreview {
        val color = ElementTheme.colors.bgSubtleSecondary
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            InfoListItemMolecule(
                message = { Text("A single item") },
                icon = { Icon(imageVector = CompoundIcons.InfoSolid(), contentDescription = null) },
                position = InfoListItemPosition.Single,
                backgroundColor = color,
            )
            InfoListItemMolecule(
                message = { Text("A top item") },
                icon = { Icon(imageVector = CompoundIcons.InfoSolid(), contentDescription = null) },
                position = InfoListItemPosition.Top,
                backgroundColor = color,
            )
            InfoListItemMolecule(
                message = { Text("A middle item") },
                icon = { Icon(imageVector = CompoundIcons.InfoSolid(), contentDescription = null) },
                position = InfoListItemPosition.Middle,
                backgroundColor = color,
            )
            InfoListItemMolecule(
                message = { Text("A bottom item") },
                icon = { Icon(imageVector = CompoundIcons.InfoSolid(), contentDescription = null) },
                position = InfoListItemPosition.Bottom,
                backgroundColor = color,
            )
        }
    }
}
