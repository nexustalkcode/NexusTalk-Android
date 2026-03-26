/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.viewfolder.api.TextFileViewer
import io.element.android.features.viewfolder.impl.file.ColorationMode
import io.element.android.features.viewfolder.impl.file.FileContent
import kotlinx.collections.immutable.ImmutableList

/**
 * TextFileViewer 的默认实现
 *
 * 提供文本文件查看器的默认实现，使用 FileContent 组件渲染文本内容。
 * 该实现支持基本的文本显示，不进行语法高亮处理。
 *
 * @see TextFileViewer 文本文件查看器接口
 * @see FileContent 文件内容组件
 */
@ContributesBinding(AppScope::class)
class DefaultTextFileViewer : TextFileViewer {
    /**
     * 渲染文本文件内容
     *
     * @param lines 文本行列表
     * @param modifier 修饰符
     */
    @Composable
    override fun Render(
        lines: ImmutableList<String>,
        modifier: Modifier
    ) {
        FileContent(
            lines = lines,
            colorationMode = ColorationMode.None,
            modifier = modifier
        )
    }
}
