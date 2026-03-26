/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.messagecomposer

import androidx.compose.runtime.Composable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.wysiwyg.compose.RichTextEditorState
import io.element.android.wysiwyg.compose.rememberRichTextEditorState

/**
 * 富文本编辑器状态工厂接口
 *
 * 定义创建富文本编辑器状态的工厂方法，用于在 Compose 中创建和管理富文本编辑器的状态。
 *
 * @see RichTextEditorState 富文本编辑器状态
 */
interface RichTextEditorStateFactory {
    /**
     * 创建并remember富文本编辑器状态
     *
     * @return 富文本编辑器状态实例
     */
    @Composable
    fun remember(): RichTextEditorState
}

/**
 * 默认富文本编辑器状态工厂实现
 *
 * 使用系统提供的 rememberRichTextEditorState() 方法创建富文本编辑器状态。
 *
 * @see RichTextEditorStateFactory 工厂接口
 */
@ContributesBinding(AppScope::class)
class DefaultRichTextEditorStateFactory : RichTextEditorStateFactory {
    /**
     * 创建富文本编辑器状态
     *
     * @return 富文本编辑器状态实例
     */
    @Composable
    override fun remember(): RichTextEditorState {
        return rememberRichTextEditorState()
    }
}
