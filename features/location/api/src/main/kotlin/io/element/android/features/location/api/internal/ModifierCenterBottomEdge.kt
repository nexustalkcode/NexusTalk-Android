/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.api.internal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset

/**
 * 将内容水平居中对齐，并将内容底部边缘垂直对齐到中心
 *
 * 该修饰符用于将内容在水平方向上居中，并在垂直方向上将内容的底部边缘对齐到容器的中心位置。
 * 常用于地图上的图钉标记定位，使其尖端对准地图上的特定位置。
 *
 * @param scope 盒子作用域，用于获取容器尺寸信息
 * @return Modifier 修改后的修饰符
 */
fun Modifier.centerBottomEdge(scope: BoxScope): Modifier = this.then(
    with(scope) {
        Modifier.align { size, space, _ ->
            IntOffset(
                x = (space.width - size.width) / 2,
                y = space.height / 2 - size.height,
            )
        }
    }
)
