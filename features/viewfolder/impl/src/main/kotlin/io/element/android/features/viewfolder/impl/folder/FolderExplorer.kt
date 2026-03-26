/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.folder

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.viewfolder.impl.model.Item
import io.element.android.libraries.androidutils.filesize.FileSizeFormatter
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 文件夹浏览接口
 *
 * 定义获取文件夹内容的功能接口，支持异步加载文件和文件夹列表。
 * 该接口采用协程实现，确保文件操作的异步性和非阻塞性。
 *
 * @see DefaultFolderExplorer 默认实现
 */
interface FolderExplorer {
    /**
     * 获取指定路径下的项目列表
     *
     * @param path 文件夹路径
     * @return 项目列表，包含文件和文件夹
     * @throws IllegalArgumentException 如果路径不是文件夹
     */
    suspend fun getItems(path: String): List<Item>
}

/**
 * FolderExplorer 的默认实现
 *
 * 提供实际的文件系统访问功能，读取指定目录的内容并转换为 Item 列表。
 * 实现了文件夹优先、名称排序的展示逻辑。
 *
 * @property fileSizeFormatter 文件大小格式化器
 * @property dispatchers 协程调度器
 * @see FolderExplorer 文件夹浏览接口
 */
@ContributesBinding(AppScope::class)
class DefaultFolderExplorer(
    private val fileSizeFormatter: FileSizeFormatter,
    private val dispatchers: CoroutineDispatchers,
) : FolderExplorer {
    /**
     * 获取文件夹内容
     *
     * 读取指定路径下的所有文件和文件夹，按类型和名称排序返回。
     * 文件夹显示在前，文件显示在后。
     *
     * @param path 文件夹路径
     * @return 排序后的项目列表
     */
    override suspend fun getItems(path: String): List<Item> = withContext(dispatchers.io) {
        val current = File(path)
        if (current.isFile) {
            error("Not a folder")
        }
        val folderContent = current.listFiles().orEmpty().map { file ->
            if (file.isDirectory) {
                Item.Folder(
                    path = file.path,
                    name = file.name
                )
            } else {
                Item.File(
                    path = file.path,
                    name = file.name,
                    formattedSize = fileSizeFormatter.format(file.length()),
                )
            }
        }
        buildList {
            addAll(folderContent.filterIsInstance<Item.Folder>().sortedBy(Item.Folder::name))
            addAll(folderContent.filterIsInstance<Item.File>().sortedBy(Item.File::name))
        }
    }
}
