/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.androidutils.clipboard

/**
 * 用于预览或测试的剪贴板假实现。
 */
class FakeClipboardHelper : ClipboardHelper {
    var clipboardContents: Any? = null

    /**
     * 把文本保存到内存字段，模拟复制行为。
     */
    override fun copyPlainText(text: String) {
        clipboardContents = text
    }
}
