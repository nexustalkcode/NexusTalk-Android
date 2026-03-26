/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 选择指示器原子组件
 *
 * 用于显示选中状态的指示图标。
 * 当选中时显示实心勾选圆圈图标，未选中时显示空内容。
 * 常用于列表项选择、checkbox 状态显示等场景。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon

/**
 * 选择指示器组件
 *
 * 根据选中状态显示勾选图标或空内容。
 * 选中时显示实心勾选圆圈，颜色根据启用状态切换。
 *
 * @param checked Boolean 是否选中状态
 * @param enabled Boolean 是否启用状态，禁用时显示灰色
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 *
 * @return Unit
 *
 * @see [ElementTheme.colors.iconAccentPrimary] 选中状态图标颜色
 * @see [ElementTheme.colors.iconDisabled] 禁用状态图标颜色
 *
 * @example
 * ```kotlin
 * SelectedIndicatorAtom(
 *     checked = true,
 *     enabled = true
 * )
 * ```
 */
@Composable
fun SelectedIndicatorAtom(
    checked: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    if (checked) {
        Icon(
            modifier = modifier.toggleable(
                value = true,
                role = Role.Checkbox,
                enabled = enabled,
                onValueChange = {},
            ),
            imageVector = CompoundIcons.CheckCircleSolid(),
            contentDescription = null,
            tint = if (enabled) {
                ElementTheme.colors.iconAccentPrimary
            } else {
                ElementTheme.colors.iconDisabled
            },
        )
    } else {
        Box(modifier)
    }
}

/**
 * SelectedIndicatorAtom 预览组件
 *
 * 用于在设计预览中展示 SelectedIndicatorAtom 组件的各种状态组合。
 * 此预览函数支持日夜两种主题模式，展示选中/未选中和启用/禁用状态的组合。
 */
@Composable
@PreviewsDayNight
internal fun SelectedIndicatorAtomPreview() = ElementPreview {
    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SelectedIndicatorAtom(
            modifier = Modifier.size(24.dp),
            checked = false,
            enabled = false,
        )
        SelectedIndicatorAtom(
            modifier = Modifier.size(24.dp),
            checked = true,
            enabled = false,
        )
        SelectedIndicatorAtom(
            modifier = Modifier.size(24.dp),
            checked = false,
            enabled = true,
        )
        SelectedIndicatorAtom(
            modifier = Modifier.size(24.dp),
            checked = true,
            enabled = true,
        )
    }
}
