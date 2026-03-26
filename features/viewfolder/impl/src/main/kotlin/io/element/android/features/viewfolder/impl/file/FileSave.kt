/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.file

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.androidutils.system.toast
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.core.mimetype.MimeTypes
import io.element.android.libraries.di.annotations.ApplicationContext
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

/**
 * 文件保存接口
 *
 * 定义文件保存到设备存储的功能接口。
 *
 * @see DefaultFileSave 默认实现
 */
interface FileSave {
    /**
     * 保存文件到磁盘
     *
     * @param path 要保存的文件路径
     */
    suspend fun save(
        path: String,
    )
}

/**
 * FileSave 的默认实现
 *
 * 提供实际的文件保存功能，支持 Android 10+ 的 MediaStore API
 * 和旧版本的外部存储 API。
 * 根据设备系统版本自动选择合适的保存方式。
 *
 * @property context 应用上下文
 * @property dispatchers 协程调度器
 * @see FileSave 文件保存接口
 */
@ContributesBinding(AppScope::class)
class DefaultFileSave(
    @ApplicationContext private val context: Context,
    private val dispatchers: CoroutineDispatchers,
) : FileSave {
    /**
     * 保存文件到磁盘
     *
     * 根据系统版本选择保存方式：
     * - Android 10+ 使用 MediaStore API
     * - 旧版本使用外部存储 API
     *
     * @param path 要保存的文件路径
     */
    override suspend fun save(
        path: String,
    ) {
        withContext(dispatchers.io) {
            runCatchingExceptions {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveOnDiskUsingMediaStore(path)
                } else {
                    saveOnDiskUsingExternalStorageApi(path)
                }
            }.onSuccess {
                Timber.v("Save on disk succeed")
                withContext(dispatchers.main) {
                    context.toast("Save on disk succeed")
                }
            }.onFailure {
                Timber.e(it, "Save on disk failed")
            }
        }
    }

    /**
     * 使用 MediaStore API 保存文件（Android 10+）
     *
     * @param path 源文件路径
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveOnDiskUsingMediaStore(path: String) {
        val file = File(path)
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.MIME_TYPE, MimeTypes.OctetStream)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val outputUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (outputUri != null) {
            file.inputStream().use { input ->
                resolver.openOutputStream(outputUri).use { output ->
                    input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                }
            }
        }
    }

    /**
     * 使用外部存储 API 保存文件（Android 10 以下）
     *
     * @param path 源文件路径
     */
    private fun saveOnDiskUsingExternalStorageApi(path: String) {
        val file = File(path)
        val target = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            file.name
        )
        file.inputStream().use { input ->
            FileOutputStream(target).use { output ->
                input.copyTo(output)
            }
        }
    }
}
