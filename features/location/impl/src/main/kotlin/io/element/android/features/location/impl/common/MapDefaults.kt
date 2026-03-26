/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.common

import android.Manifest
import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.maplibre.compose.MapLocationSettings
import io.element.android.libraries.maplibre.compose.MapSymbolManagerSettings
import io.element.android.libraries.maplibre.compose.MapUiSettings
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng

/**
 * 地图默认配置对象
 *
 * 包含地图组件的通用配置值，如 UI 设置、位置设置、相机位置等。
 */
object MapDefaults {
    /**
     * 地图 UI 设置
     *
     * 配置地图的交互行为，包括禁用指南针、旋转手势，启用滚动和缩放手势等。
     *
     * @return MapUiSettings 地图 UI 配置
     */
    val uiSettings: MapUiSettings
        @Composable
        @ReadOnlyComposable
        get() = MapUiSettings(
            compassEnabled = false,
            rotationGesturesEnabled = false,
            scrollGesturesEnabled = true,
            tiltGesturesEnabled = false,
            zoomGesturesEnabled = true,
            logoGravity = Gravity.TOP,
            attributionGravity = Gravity.TOP,
            attributionTintColor = ElementTheme.colors.iconPrimary
        )

    /**
     * 符号管理器设置
     *
     * 配置地图上标记图标的显示行为。
     *
     * @return MapSymbolManagerSettings 符号管理器配置
     */
    val symbolManagerSettings: MapSymbolManagerSettings
        get() = MapSymbolManagerSettings(
            iconAllowOverlap = true
        )

    /**
     * 位置追踪设置
     *
     * 配置用户位置追踪功能的外观和行为。
     *
     * @return MapLocationSettings 位置追踪配置
     */
    val locationSettings: MapLocationSettings
        get() = MapLocationSettings(
            locationEnabled = false,
            backgroundTintColor = Color.White,
            foregroundTintColor = Color.Black,
            backgroundStaleTintColor = Color.White,
            foregroundStaleTintColor = Color.Black,
            accuracyColor = Color.Black,
            pulseEnabled = true,
            pulseColor = Color.Black,
        )

    /**
     * 地图默认中心位置（德国法兰克福附近）
     *
     * 作为地图的初始中心位置，用于在用户未授权位置权限时显示。
     *
     * @return CameraPosition 默认相机位置
     */
    val centerCameraPosition = CameraPosition.Builder()
        .target(LatLng(49.843, 9.902056))
        .zoom(2.7)
        .build()

    /**
     * 默认缩放级别
     *
     * 用于发送位置和显示位置时的默认地图缩放级别。
     */
    const val DEFAULT_ZOOM = 15.0

    /**
     * 位置权限列表
     *
     * 应用需要请求的位置权限列表，包括精确位置和粗略位置。
     */
    val permissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
}
