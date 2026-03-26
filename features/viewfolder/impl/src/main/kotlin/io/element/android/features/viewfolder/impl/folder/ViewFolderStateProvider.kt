/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.folder

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.viewfolder.impl.model.Item
import kotlinx.collections.immutable.toImmutableList

/**
 * ViewFolderState 预览参数提供者
 *
 * 提供 ViewFolderState 的示例值，用于在 Android Studio 预览中展示 UI 效果。
 * 继承自 PreviewParameterProvider，支持提供多个预览场景。
 *
 * @see ViewFolderState 文件夹视图状态
 */
open class ViewFolderStateProvider : PreviewParameterProvider<ViewFolderState> {
    /**
     * 获取预览状态序列
     *
     * @return 包含不同场景的 ViewFolderState 序列
     */
    override val values: Sequence<ViewFolderState>
        get() = sequenceOf(
            aViewFolderState(),
            aViewFolderState(
                content = listOf(
                    Item.Parent,
                    Item.Folder("aPath", "aFolder"),
                    Item.File("aPath", "aFile", "12kB"),
                )
            )
        )
}

/**
 * 创建示例文件夹状态
 *
 * @param title 标题，默认为 "aPath"
 * @param content 内容列表，默认为空列表
 * @return 示例 ViewFolderState 实例
 */
fun aViewFolderState(
    title: String = "aPath",
    content: List<Item> = emptyList(),
) = ViewFolderState(
    title = title,
    content = content.toImmutableList(),
)
