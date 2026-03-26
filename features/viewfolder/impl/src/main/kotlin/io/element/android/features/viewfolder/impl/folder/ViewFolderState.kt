/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.folder

import io.element.android.features.viewfolder.impl.model.Item
import kotlinx.collections.immutable.ImmutableList

/**
 * 文件夹视图状态数据类
 *
 * 表示文件夹浏览界面的当前状态，包含标题和内容列表。
 * 使用不可变数据结构，确保状态的可预测性和线程安全。
 *
 * @property title 当前显示的标题，通常为路径信息
 * @property content 当前文件夹中的项目列表，包括文件和子文件夹
 */
data class ViewFolderState(
    val title: String,
    val content: ImmutableList<Item>,
)
