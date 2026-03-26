/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:JvmName("TileServerStyleUriBuilderKt")

package io.element.android.features.location.api.internal

import io.element.android.features.location.api.BuildConfig

/**
 * MapTiler 瓦片服务器样式 URI 构建器
 *
 * 用于构建 MapLibre 兼容的地图瓦片服务器样式 URI。
 *
 * @property baseUrl MapTiler API 基础 URL
 * @property apiKey MapTiler API 密钥
 * @property lightMapId 浅色地图样式 ID
 * @property darkMapId 深色地图样式 ID
 */
internal class MapTilerTileServerStyleUriBuilder(
    /** MapTiler API 基础 URL */
    private val baseUrl: String,
    /** MapTiler API 密钥 */
    private val apiKey: String,
    /** 浅色地图样式 ID */
    private val lightMapId: String,
    /** 深色地图样式 ID */
    private val darkMapId: String,
) : TileServerStyleUriBuilder {
    constructor() : this(
        baseUrl = BuildConfig.MAPTILER_BASE_URL.removeSuffix("/"),
        apiKey = BuildConfig.MAPTILER_API_KEY,
        lightMapId = BuildConfig.MAPTILER_LIGHT_MAP_ID,
        darkMapId = BuildConfig.MAPTILER_DARK_MAP_ID,
    )

    override fun build(darkMode: Boolean): String {
        val mapId = if (darkMode) darkMapId else lightMapId
        return "$baseUrl/$mapId/style.json?key=$apiKey"
    }
}
