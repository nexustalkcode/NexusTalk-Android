/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.api.timeline

import androidx.compose.runtime.Composable
import io.element.android.wysiwyg.utils.HtmlConverter

/**
 * HTML转换器提供器接口
 *
 * 提供富文本编辑器中 HTML 内容与 Markdown 格式之间的转换功能。
 * 用于处理消息内容中的富文本格式转换。
 *
 * @see HtmlConverter HTML转换器实现类
 */
interface HtmlConverterProvider {
    /**
     * 更新转换器状态
     *
     * Compose 重组钩子，用于在 Compose 环境中更新转换器相关状态。
     */
    @Composable
    fun Update()

    /**
     * 提供 HTML 转换器实例
     *
     * @return HtmlConverter 实例，用于执行 HTML 与 Markdown 之间的转换
     */
    fun provide(): HtmlConverter
}
