/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.common.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.theme.components.FloatingActionButton
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 位置悬浮操作按钮
 *
 * 一个用于定位功能的悬浮按钮，点击后可以将地图中心移动到用户当前位置。
 * 按钮图标会根据当前状态显示不同的图标（已定位/未定位）。
 *
 * 参考设计：https://www.figma.com/design/0MMNu7cTOzLOlWb7ctTkv3/Element-X?node-id=3426-141111
 *
 * @param isMapCenteredOnUser 地图是否已居中于用户位置
 * @param onClick 点击按钮的回调
 * @param modifier 修饰符
 */
@Composable
internal fun LocationFloatingActionButton(
    isMapCenteredOnUser: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        shape = FloatingActionButtonDefaults.smallShape,
        containerColor = ElementTheme.colors.bgCanvasDefault,
        contentColor = ElementTheme.colors.iconPrimary,
        onClick = onClick,
        modifier = modifier
            // Note: design is 40dp, but min is 48 for accessibility.
            .size(48.dp),
    ) {
        val iconImage = if (isMapCenteredOnUser) {
            CompoundIcons.LocationNavigatorCentred()
        } else {
            CompoundIcons.LocationNavigator()
        }
        Icon(
            imageVector = iconImage,
            contentDescription = stringResource(CommonStrings.a11y_move_the_map_to_my_location),
        )
    }
}
