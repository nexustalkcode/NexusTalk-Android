/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 文件查看 Presenter
 *
 * 负责处理文件查看的业务逻辑，包括：
 * - 加载文件内容
 * - 处理文件分享操作
 * - 处理文件保存操作
 * - 确定文件的着色模式
 *
 * 使用协程进行异步文件操作，确保主线程流畅响应。
 *
 * @property path 文件路径
 * @property name 文件名称
 * @property fileContentReader 文件内容读取器
 * @property fileShare 文件分享器
 * @property fileSave 文件保存器
 * @see ViewFileState 文件查看状态
 * @see FileContentReader 文件内容读取接口
 * @see FileShare 文件分享接口
 * @see FileSave 文件保存接口
 */
@AssistedInject
class ViewFilePresenter(
    @Assisted("path") val path: String,
    @Assisted("name") val name: String,
    private val fileContentReader: FileContentReader,
    private val fileShare: FileShare,
    private val fileSave: FileSave,
) : Presenter<ViewFileState> {
    /**
     * Presenter 工厂接口
     *
     * 用于通过依赖注入创建 ViewFilePresenter 实例
     */
    @AssistedFactory
    interface Factory {
        /**
         * 创建 ViewFilePresenter 实例
         *
         * @param path 文件路径
         * @param name 文件名称
         * @return ViewFilePresenter 实例
         */
        fun create(
            @Assisted("path") path: String,
            @Assisted("name") name: String,
        ): ViewFilePresenter
    }

    /**
     * 创建视图状态
     *
     * 加载文件内容并确定着色模式，支持日志文件的自动识别。
     *
     * @return ViewFileState 当前文件的状态
     */
    @Composable
    override fun present(): ViewFileState {
        val coroutineScope = rememberCoroutineScope()
        val colorationMode = remember { name.toColorationMode() }

        fun handleEvent(event: ViewFileEvents) {
            when (event) {
                ViewFileEvents.Share -> coroutineScope.share(path)
                ViewFileEvents.SaveOnDisk -> coroutineScope.save(path)
            }
        }

        var lines: AsyncData<List<String>> by remember { mutableStateOf(AsyncData.Loading()) }
        LaunchedEffect(Unit) {
            lines = fileContentReader.getLines(path).fold(
                onSuccess = { AsyncData.Success(it) },
                onFailure = { AsyncData.Failure(it) }
            )
        }
        return ViewFileState(
            name = name,
            lines = lines,
            colorationMode = colorationMode,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 分享文件
     *
     * @param path 要分享的文件路径
     */
    private fun CoroutineScope.share(path: String) = launch {
        fileShare.share(path)
    }

    /**
     * 保存文件到磁盘
     *
     * @param path 要保存的文件路径
     */
    private fun CoroutineScope.save(path: String) = launch {
        fileSave.save(path)
    }
}

/**
 * 将文件名转换为着色模式
 *
 * 根据文件名判断应该使用的着色模式：
 * - logcat.log -> Logcat 模式
 * - logs.* -> RustLogs 模式
 * - 其他 -> None 模式
 *
 * @return 对应的着色模式
 */
private fun String.toColorationMode(): ColorationMode {
    return when {
        equals("logcat.log") -> ColorationMode.Logcat
        startsWith("logs.") -> ColorationMode.RustLogs
        else -> ColorationMode.None
    }
}
