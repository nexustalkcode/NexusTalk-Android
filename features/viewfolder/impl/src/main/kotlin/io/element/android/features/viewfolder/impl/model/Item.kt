/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.model

import androidx.compose.runtime.Immutable

/**
 * 文件夹项目密封接口
 *
 * 表示文件夹浏览中的项目类型，包括父目录、文件夹和文件。
 * 使用密封接口实现类型安全的数据建模，支持 exhaustive when 表达式。
 *
 * @see Item.Parent 父目录项，用于返回上一级
 * @see Item.Folder 文件夹项
 * @see Item.File 文件项
 */
@Immutable
sealed interface Item {
    /**
     * 父目录项
     *
     * 表示返回上一级目录的虚拟项目，在根目录时不可见
     */
    data object Parent : Item

    /**
     * 文件夹项目
     *
     * @property path 文件夹的完整路径
     * @property name 文件夹名称
     */
    data class Folder(
        val path: String,
        val name: String,
    ) : Item

    /**
     * 文件项目
     *
     * @property path 文件的完整路径
     * @property name 文件名称
     * @property formattedSize 格式化的文件大小字符串
     */
    data class File(
        val path: String,
        val name: String,
        val formattedSize: String,
    ) : Item
}
