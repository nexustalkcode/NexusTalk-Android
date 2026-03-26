/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 信息列表有机体组件
 *
 * 用于垂直排列多个信息项的复合组件。
 * 每个信息项包含图标和消息内容，自动处理圆角样式。
 * 适用于显示房间信息、用户资料详情等列表场景。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.organisms

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.atomic.molecules.InfoListItemMolecule
import io.element.android.libraries.designsystem.atomic.molecules.InfoListItemPosition
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 信息列表项数据类
 *
 * 定义信息列表中的单个项目。
 *
 * @property message CharSequence 消息文本内容，支持 AnnotatedString
 * @property iconId Int? 可选的资源图标 ID
 * @property iconVector ImageVector? 可选的向量图标
 * @property iconComposable @Composable () -> Unit 可选的自定义图标组件
 */
data class InfoListItem(
    val message: CharSequence,
    @DrawableRes val iconId: Int? = null,
    val iconVector: ImageVector? = null,
    val iconComposable: @Composable () -> Unit = {},
)

/**
 * 信息列表组件
 *
 * 创建一个垂直排列的信息列表。
 * 每个列表项自动根据位置设置圆角样式。
 *
 * @param items ImmutableList<InfoListItem> 信息项列表
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param backgroundColor Color 背景颜色，默认为 [ElementTheme.colors.bgSubtleSecondary]
 * @param iconTint Color 图标着色，默认为当前内容颜色
 * @param iconSize Dp 图标尺寸，默认为 20.dp
 * @param textStyle TextStyle 文本样式，默认为当前文本样式
 * @param textColor Color 文字颜色，默认为 [ElementTheme.colors.textPrimary]
 * @param verticalArrangement Arrangement.Vertical 垂直排列方式，默认为 Arrangement.spacedBy(4.dp)
 *
 * @return Unit
 *
 * @see [InfoListItem] 信息列表项数据类
 * @see [InfoListItemMolecule] 信息列表项组件
 *
 * @example
 * ```kotlin
 * InfoListOrganism(
 *     items = persistentListOf(
 *         InfoListItem(message = "信息1"),
 *         InfoListItem(message = "信息2")
 *     )
 * )
 * ```
 */
@Composable
fun InfoListOrganism(
    items: ImmutableList<InfoListItem>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ElementTheme.colors.bgSubtleSecondary,
    iconTint: Color = LocalContentColor.current,
    iconSize: Dp = 20.dp,
    textStyle: TextStyle = LocalTextStyle.current,
    textColor: Color = ElementTheme.colors.textPrimary,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(4.dp),
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
    ) {
        for ((index, item) in items.withIndex()) {
            val position = when {
                items.size == 1 -> InfoListItemPosition.Single
                index == 0 -> InfoListItemPosition.Top
                index == items.size - 1 -> InfoListItemPosition.Bottom
                else -> InfoListItemPosition.Middle
            }
            InfoListItemMolecule(
                message = {
                    if (item.message is AnnotatedString) {
                        Text(
                            text = item.message,
                            style = textStyle,
                            color = textColor,
                        )
                    } else {
                        Text(
                            text = item.message.toString(),
                            style = textStyle,
                            color = textColor,
                        )
                    }
                },
                icon = {
                    if (item.iconId != null) {
                        Icon(
                            modifier = Modifier.size(iconSize),
                            resourceId = item.iconId,
                            contentDescription = null,
                            tint = iconTint,
                        )
                    } else if (item.iconVector != null) {
                        Icon(
                            modifier = Modifier.size(iconSize),
                            imageVector = item.iconVector,
                            contentDescription = null,
                            tint = iconTint,
                        )
                    } else {
                        item.iconComposable()
                    }
                },
                position = position,
                backgroundColor = backgroundColor,
            )
        }
    }
}


/**
 * InfoListOrganism 预览组件
 *
 * 用于在设计预览中展示 InfoListOrganism 组件的默认状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun InfoListOrganismPreview() = ElementPreview {
    val items = persistentListOf(
        InfoListItem(message = "A top item"),
        InfoListItem(message = "A middle item"),
        InfoListItem(message = "A bottom item"),
    )
    InfoListOrganism(
        items = items,
    )
}
