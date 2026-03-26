/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl.configureroom

/**
 * 房间地址密封类
 *
 * 表示房间地址的两种状态：
 * 1. AutoFilled - 系统自动生成的地址（基于房间名称）
 * 2. Edited - 用户手动编辑/修改的地址
 *
 * @property value 地址的字符串值
 */
sealed class RoomAddress(open val value: String) {
    /**
     * 自动填充的房间地址
     *
     * 系统根据房间名称自动生成的地址。
     * 当用户修改房间名称时，此类型会自动更新。
     *
     * @property value 自动生成的地址字符串
     */
    data class AutoFilled(override val value: String) : RoomAddress(value)

    /**
     * 用户编辑的房间地址
     *
     * 用户手动输入/修改的地址。
     * 即使房间名称变化，此地址也不会自动更新。
     *
     * @property value 用户编辑的地址字符串
     */
    data class Edited(override val value: String) : RoomAddress(value)
}
