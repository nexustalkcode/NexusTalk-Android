/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.common.actions

import io.element.android.features.location.api.Location

/**
 * 位置操作接口
 *
 * 定义了位置相关的操作，包括分享位置和打开设置。
 */
interface LocationActions {
    /**
     * 分享位置
     *
     * 打开外部地图应用来显示指定的位置。
     *
     * @param location 要分享的位置信息
     * @param label 位置的标签/描述
     */
    fun share(location: Location, label: String?)

    /**
     * 打开应用设置
     *
     * 打开系统设置页面，让用户可以手动开启位置权限。
     */
    fun openSettings()
}
