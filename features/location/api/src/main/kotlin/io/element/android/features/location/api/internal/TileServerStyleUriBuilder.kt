/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.api.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.element.android.compound.theme.ElementTheme

/**
 * 地图瓦片服务器样式 URI 构建器接口
 *
 * 用于构建 MapLibre 兼容的瓦片服务器样式 URI。
 * 用于渲染动态地图。
 */
interface TileServerStyleUriBuilder {
    /**
     * 构建瓦片服务器样式 URI
     *
     * @param darkMode 是否使用深色模式
     * @return String 瓦片服务器样式 URI
     */
    fun build(
        darkMode: Boolean,
    ): String
}

/**
 * 创建默认的瓦片服务器样式 URI 构建器
 *
 * @return TileServerStyleUriBuilder MapTiler 瓦片服务器样式 URI 构建器实例
 */
fun TileServerStyleUriBuilder(): TileServerStyleUriBuilder = MapTilerTileServerStyleUriBuilder()

/**
 * Provides and remembers a style URI for a MapLibre compatible tile server.
 *
 * Used for rendering dynamic maps.
 */
@Composable
fun rememberTileStyleUrl(): String {
    val darkMode = !ElementTheme.isLightTheme
    return remember(darkMode) {
        TileServerStyleUriBuilder().build(darkMode)
    }
}
