/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 成员数量分子组件
 *
 * 用于显示房间或群组成员数量的组件。
 * 采用胶囊形状设计，包含用户图标和数字。
 * 常用于房间列表项中显示成员数量信息。
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * 成员数量组件
 *
 * 创建一个显示成员数量的胶囊状组件。
 * 组件左侧为用户图标，右侧为成员数量数字。
 *
 * @param memberCount Int 成员数量
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 *
 * @return Unit
 *
 * @see [ElementTheme.colors.bgSubtleSecondary] 默认背景色
 * @see [ElementTheme.colors.iconSecondary] 图标颜色
 * @see [ElementTheme.colors.textSecondary] 文字颜色
 *
 * @example
 * ```kotlin
 * MembersCountMolecule(memberCount = 123)
 * ```
 */
@Composable
fun MembersCountMolecule(
    memberCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(color = ElementTheme.colors.bgSubtleSecondary, shape = CircleShape)
            .padding(start = 2.dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = CompoundIcons.UserProfile(),
            contentDescription = null,
            tint = ElementTheme.colors.iconSecondary,
        )
        Text(
            text = "$memberCount",
            style = ElementTheme.typography.fontBodySmMedium,
            color = ElementTheme.colors.textSecondary,
        )
    }
}

/**
 * MembersCountMolecule 预览组件
 *
 * 用于在设计预览中展示 MembersCountMolecule 组件的不同数量状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun MembersCountMoleculePreview() = ElementPreview {
    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MembersCountMolecule(memberCount = 1)
        MembersCountMolecule(memberCount = 888)
        MembersCountMolecule(memberCount = 123_456)
    }
}
