/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.api.internal

/**
 * 静态地图 URL 构建器接口
 *
 * 定义了为第三方服务提供商的静态地图 API 构建 URL 的接口。
 */
interface StaticMapUrlBuilder {
    /**
     * 构建静态地图请求 URL
     *
     * @param lat 地图中心纬度
     * @param lon 地图中心经度
     * @param zoom 缩放级别
     * @param darkMode 是否使用深色模式
     * @param width 请求的图片宽度（像素）
     * @param height 请求的图片高度（像素）
     * @param density 屏幕密度
     * @return String 完整的静态地图 URL
     */
    fun build(
        lat: Double,
        lon: Double,
        zoom: Double,
        darkMode: Boolean,
        width: Int,
        height: Int,
        density: Float,
    ): String

    /**
     * 检查静态地图服务是否可用
     *
     * @return Boolean 如果服务可用返回 true
     */
    fun isServiceAvailable(): Boolean
}

/**
 * 创建默认的静态地图 URL 构建器
 *
 * @return StaticMapUrlBuilder MapTiler 静态地图 URL 构建器实例
 */
fun StaticMapUrlBuilder(): StaticMapUrlBuilder = MapTilerStaticMapUrlBuilder()
