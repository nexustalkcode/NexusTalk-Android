/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.androidutils.filesize

/**
 * 用于测试或预览的文件大小格式化假实现。
 */
class FakeFileSizeFormatter : FileSizeFormatter {
    /**
     * 直接返回字节数字符串。
     */
    override fun format(fileSize: Long, useShortFormat: Boolean): String {
        return "$fileSize Bytes"
    }
}
