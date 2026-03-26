/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 头像颜色提供者
 *
 * 根据标识符为头像提供一致的颜色方案。
 * 通过字符串哈希值映射到预定义的颜色集合，
 * 确保相同标识符始终获得相同的颜色。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.colors

import androidx.compose.runtime.Composable
import io.element.android.compound.theme.AvatarColors
import io.element.android.compound.theme.avatarColors

/**
 * 头像颜色提供者对象
 *
 * 提供根据标识符获取对应头像颜色的功能。
 */
object AvatarColorsProvider {
    /**
     * 提供头像颜色
     *
     * 根据传入的标识符生成哈希值，从预定义的颜色集合中返回对应的颜色方案。
     *
     * @param id String 标识符，用于生成哈希值以选择颜色
     * @return AvatarColors 对应的头像颜色方案
     *
     * @example
     * ```kotlin
     * val colors = AvatarColorsProvider.provide("user123")
     * ```
     */
    @Composable
    fun provide(id: String): AvatarColors {
        return avatarColors().let { colors ->
            colors[id.toHash(colors.size)]
        }
    }
}

/**
 * 字符串转哈希值
 *
 * 将字符串转换为指定范围内的哈希值。
 *
 * @param maxSize Int 哈希值的最大值范围
 * @return Int 计算得到的哈希值
 */
internal fun String.toHash(maxSize: Int): Int {
    return toList().sumOf { it.code } % maxSize
}
