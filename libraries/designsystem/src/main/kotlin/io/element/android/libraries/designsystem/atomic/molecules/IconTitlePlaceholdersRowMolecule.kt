/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 图标标题占位符行分子组件
 *
 * 用于在加载状态显示图标和标题占位符的组件。
 * 包含一个圆形图标占位符和多个文本占位符。
 * 常用于列表项加载骨架屏中的图标和标题区域。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.atomic.atoms.PlaceholderAtom
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.placeholderBackground

/**
 * 图标标题占位符行组件
 *
 * 创建一个包含图标占位符和多个标题占位符的行布局。
 * 适用于显示列表项在加载过程中的占位效果。
 *
 * @param iconSize Dp 图标占位符的尺寸大小
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param horizontalArrangement Arrangement.Horizontal 水平排列方式，默认为 Arrangement.Start
 * @param verticalAlignment Alignment.Vertical 垂直对齐方式，默认为 Alignment.CenterVertically
 *
 * @return Unit
 *
 * @see [PlaceholderAtom] 占位符组件
 * @see [AvatarSize] 头像尺寸规格
 *
 * @example
 * ```kotlin
 * IconTitlePlaceholdersRowMolecule(
 *     iconSize = AvatarSize.TimelineRoom.dp
 * )
 * ```
 */
@Composable
fun IconTitlePlaceholdersRowMolecule(
    iconSize: Dp,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .align(Alignment.CenterVertically)
                .background(color = ElementTheme.colors.placeholderBackground, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        PlaceholderAtom(width = 20.dp, height = 7.dp)
        Spacer(modifier = Modifier.width(7.dp))
        PlaceholderAtom(width = 45.dp, height = 7.dp)
        Spacer(modifier = Modifier.width(8.dp))
    }
}

/**
 * IconTitlePlaceholdersRowMolecule 预览组件
 *
 * 用于在设计预览中展示 IconTitlePlaceholdersRowMolecule 组件的默认状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun IconTitlePlaceholdersRowMoleculePreview() = ElementPreview {
    IconTitlePlaceholdersRowMolecule(
        iconSize = AvatarSize.TimelineRoom.dp,
    )
}
