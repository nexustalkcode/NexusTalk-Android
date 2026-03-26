/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.api

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** 地理位置 URI 正则表达式，用于匹配 geo:纬度,经度;u=精度 格式的 URI */
private const val GEO_URI_REGEX = """geo:(?<latitude>-?\d+(?:\.\d+)?),(?<longitude>-?\d+(?:\.\d+)?)(?:;u=(?<uncertainty>\d+(?:\.\d+)?))?"""

/**
 * 地理位置数据类
 *
 * 表示地理位置的坐标信息，包含纬度、经度和精度。
 *
 * @property lat 纬度
 * @property lon 经度
 * @property accuracy 精度（单位：米）
 */
@SuppressLint("NewApi")
@Parcelize
data class Location(
    /** 纬度 */
    val lat: Double,
    /** 经度 */
    val lon: Double,
    /** 精度（单位：米） */
    val accuracy: Float,
) : Parcelable {
    /**
     * 地理位置 URI 正则表达式
     * 格式：geo:纬度,经度;u=精度
     */
    companion object {
        private const val GEO_URI_REGEX = """geo:(?<latitude>-?\d+(?:\.\d+)?),(?<longitude>-?\d+(?:\.\d+)?)(?:;u=(?<uncertainty>\d+(?:\.\d+)?))?"""

        /**
         * 从地理位置 URI 创建 Location 对象
         *
         * @param geoUri 地理位置 URI 字符串
         * @return Location 对象（如果解析失败则返回 null）
         */
        fun fromGeoUri(geoUri: String): Location? {
            val result = Regex(GEO_URI_REGEX).matchEntire(geoUri) ?: return null
            return Location(
                lat = result.groups["latitude"]?.value?.toDoubleOrNull() ?: return null,
                lon = result.groups["longitude"]?.value?.toDoubleOrNull() ?: return null,
                accuracy = result.groups["uncertainty"]?.value?.toFloatOrNull() ?: 0f,
            )
        }
    }

    /**
     * 转换为地理位置 URI 字符串
     *
     * @return 地理位置 URI 字符串
     */
    fun toGeoUri(): String {
        return "geo:$lat,$lon;u=$accuracy"
    }
}
