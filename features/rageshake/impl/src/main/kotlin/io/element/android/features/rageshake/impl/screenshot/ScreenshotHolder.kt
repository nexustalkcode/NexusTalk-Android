/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.screenshot

import android.graphics.Bitmap

/**
 * 截图持有者接口
 *
 * 定义了截图的存储和管理操作。
 */
interface ScreenshotHolder {
    /**
     * 写入位图数据
     *
     * 将截图的位图数据保存到文件中。
     *
     * @param data 位图数据
     */
    fun writeBitmap(data: Bitmap)

    /**
     * 获取文件URI
     *
     * 返回保存的截图文件的URI字符串。
     *
     * @return String? 截图文件的URI，如果不存在则返回null
     */
    fun getFileUri(): String?

    /**
     * 重置截图
     *
     * 删除保存的截图文件。
     */
    fun reset()
}
