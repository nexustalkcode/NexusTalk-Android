/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.core.mimetype.MimeTypes
import io.element.android.libraries.di.annotations.ApplicationContext
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

/**
 * 文件分享接口
 *
 * 定义文件分享功能的接口，支持通过系统分享面板分享文件。
 *
 * @see DefaultFileShare 默认实现
 */
interface FileShare {
    /**
     * 分享文件
     *
     * @param path 要分享的文件路径
     */
    suspend fun share(
        path: String
    )
}

/**
 * FileShare 的默认实现
 *
 * 提供实际的文件分享功能，使用 FileProvider 生成内容 URI 并启动分享 Intent。
 * 支持将文件分享给其他应用。
 *
 * @property context 应用上下文
 * @property dispatchers 协程调度器
 * @property buildMeta 构建元信息，用于生成 FileProvider 授权
 * @see FileShare 文件分享接口
 */
@ContributesBinding(AppScope::class)
class DefaultFileShare(
    @ApplicationContext private val context: Context,
    private val dispatchers: CoroutineDispatchers,
    private val buildMeta: BuildMeta,
) : FileShare {
    /**
     * 分享文件
     *
     * 使用系统分享面板分享文件，支持 MIME 类型为 application/octet-stream。
     *
     * @param path 要分享的文件路径
     */
    override suspend fun share(
        path: String,
    ) {
        runCatchingExceptions {
            val file = File(path)
            val shareableUri = file.toShareableUri()
            val shareMediaIntent = Intent(Intent.ACTION_SEND)
                .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(Intent.EXTRA_STREAM, shareableUri)
                .setTypeAndNormalize(MimeTypes.OctetStream)
            withContext(dispatchers.main) {
                val intent = Intent.createChooser(shareMediaIntent, null)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }.onSuccess {
            Timber.v("Share file succeed")
        }.onFailure {
            Timber.e(it, "Share file failed")
        }
    }

    /**
     * 将文件转换为可分享的 URI
     *
     * @return 文件的内容 URI
     */
    private fun File.toShareableUri(): Uri {
        val authority = "${buildMeta.applicationId}.fileprovider"
        return FileProvider.getUriForFile(context, authority, this).normalizeScheme()
    }
}
