/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList

/**
 * 文本文件查看器函数式接口
 *
 * 用于渲染文本文件内容的函数式接口，采用 Jetpack Compose 实现。
 * 支持在 Compose UI 中显示文本行列表，可用于日志文件、配置文件等文本内容的展示。
 *
 * @param lines 要显示的文本行列表
 * @param modifier 修饰符，用于自定义布局和样式
 */
fun interface TextFileViewer {
    /**
     * 渲染文本文件内容
     *
     * @param lines 文本行列表
     * @param modifier 布局修饰符
     */
    @Composable
    fun Render(
        lines: ImmutableList<String>,
        modifier: Modifier,
    )
}
