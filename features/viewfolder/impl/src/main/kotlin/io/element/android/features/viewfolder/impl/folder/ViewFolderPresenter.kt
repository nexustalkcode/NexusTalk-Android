/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.features.viewfolder.impl.model.Item
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.meta.BuildMeta
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * 文件夹视图 Presenter
 *
 * 负责处理文件夹浏览的业务逻辑，包括：
 * - 加载指定路径下的文件和文件夹
 * - 处理导航到父目录的操作
 * - 管理视图状态
 *
 * 使用协程进行异步文件操作，确保主线程流畅响应。
 *
 * @property canGoUp 是否可以返回上一级目录
 * @property path 当前浏览的路径
 * @property folderExplorer 文件夹浏览接口
 * @property buildMeta 构建元信息，用于路径处理
 * @see ViewFolderState 文件夹视图状态
 * @see FolderExplorer 文件夹浏览接口
 */
@AssistedInject
class ViewFolderPresenter(
    @Assisted val canGoUp: Boolean,
    @Assisted val path: String,
    private val folderExplorer: FolderExplorer,
    private val buildMeta: BuildMeta,
) : Presenter<ViewFolderState> {
    /**
     * Presenter 工厂接口
     *
     用于通过依赖注入创建 ViewFolderPresenter 实例
     */
    @AssistedFactory
    interface Factory {
        /**
         * 创建 ViewFolderPresenter 实例
         *
         * @param canGoUp 是否可以返回上一级
         * @param path 当前路径
         * @return ViewFolderPresenter 实例
         */
        fun create(canGoUp: Boolean, path: String): ViewFolderPresenter
    }

    /**
     * 创建视图状态
     *
     * @return ViewFolderState 当前文件夹的状态
     */
    @Composable
    override fun present(): ViewFolderState {
        var content by remember { mutableStateOf<ImmutableList<Item>>(persistentListOf()) }
        val title = remember {
            buildString {
                if (path.contains(buildMeta.applicationId)) {
                    append("…")
                }
                append(path.substringAfter(buildMeta.applicationId))
            }
        }
        LaunchedEffect(Unit) {
            content = buildList {
                if (canGoUp) add(Item.Parent)
                addAll(folderExplorer.getItems(path))
            }.toImmutableList()
        }
        return ViewFolderState(
            title = title,
            content = content,
        )
    }
}
