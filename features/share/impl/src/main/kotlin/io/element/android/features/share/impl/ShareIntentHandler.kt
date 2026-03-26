/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.share.impl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import androidx.core.content.IntentCompat
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.androidutils.compat.queryIntentActivitiesCompat
import io.element.android.libraries.core.mimetype.MimeTypes
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeAny
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeApplication
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeAudio
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeFile
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeImage
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeText
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeVideo
import io.element.android.libraries.di.annotations.ApplicationContext
import timber.log.Timber

/**
 * 分享意图处理器接口
 *
 * 定义处理传入分享意图的功能接口，支持处理文件分享和纯文本分享。
 *
 * @see DefaultShareIntentHandler 默认实现
 */
/**
 * Interface for handling incoming share intents.
 *
 * Defines the functionality for processing incoming share intents, supporting file sharing and plain text sharing.
 *
 * @see DefaultShareIntentHandler Default implementation
 */
interface ShareIntentHandler {
    /**
     * 要分享的 URI 数据类
     *
     * @property uri 文件 URI
     * @property mimeType MIME 类型
     */
    /**
     * Data class representing a URI to be shared.
     *
     * @property uri The URI of the file
     * @property mimeType The MIME type of the content
     */
    data class UriToShare(
        val uri: Uri,
        val mimeType: String,
    )

    /**
     * 处理传入的分享意图
     *
     * 该方法处理来自其他应用的分享意图，支持以下场景：
     * - 分享图片、视频、音频、文件
     * - 分享纯文本
     * - 分享单个文件或多个文件
     *
     * @param intent 分享意图
     * @param onUris 处理文件分享的回调，返回是否成功
     * @param onPlainText 处理纯文本分享的回调，返回是否成功
     * @return 如果能处理意图数据返回 true，否则返回 false
     */
    suspend fun handleIncomingShareIntent(
        intent: Intent,
        onUris: suspend (List<UriToShare>) -> Boolean,
        onPlainText: suspend (String) -> Boolean,
    ): Boolean
}

/**
 * 分享意图处理器的默认实现
 *
 * 提供实际的分享意图处理功能，支持：
 * - 解析 Intent 中的 URI 列表
     * 处理不同类型的分享意图
     * 管理 URI 权限
     *
     * @property context 应用上下文
     * @see ShareIntentHandler 分享意图处理器接口
     */
/**
 * Default implementation of ShareIntentHandler.
 *
 * Provides actual share intent handling functionality, supporting:
 * - Parsing URI list from Intent
 * - Handling different types of share intents
 * - Managing URI permissions
 *
 * @property context Application context
 * @see ShareIntentHandler Share intent handler interface
 */
@ContributesBinding(AppScope::class)
class DefaultShareIntentHandler(
    @ApplicationContext private val context: Context,
) : ShareIntentHandler {
    /**
     * 处理传入的分享意图
     *
     * 根据意图类型和 MIME 类型分发到相应的处理逻辑。
     *
     * @param intent 分享意图
     * @param onUris 处理文件分享的回调
     * @param onPlainText 处理纯文本分享的回调
     * @return 是否成功处理
     */
    override suspend fun handleIncomingShareIntent(
        intent: Intent,
        onUris: suspend (List<ShareIntentHandler.UriToShare>) -> Boolean,
        onPlainText: suspend (String) -> Boolean,
    ): Boolean {
        val type = intent.resolveType(context) ?: return false
        val uris = getIncomingUris(intent, type)
        return when {
            uris.isEmpty() && type == MimeTypes.PlainText -> handlePlainText(intent, onPlainText)
            type.isMimeTypeImage() ||
                type.isMimeTypeVideo() ||
                type.isMimeTypeAudio() ||
                type.isMimeTypeApplication() ||
                type.isMimeTypeFile() ||
                type.isMimeTypeText() ||
                type.isMimeTypeAny() -> {
                val result = onUris(uris)
                revokeUriPermissions(uris.map { it.uri })
                result
            }
            else -> false
        }
    }

    /**
     * 处理纯文本分享
     *
     * @param intent 分享意图
     * @param onPlainText 处理纯文本的回调
     * @return 是否成功处理
     */
    private suspend fun handlePlainText(intent: Intent, onPlainText: suspend (String) -> Boolean): Boolean {
        val content = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
        return if (content?.isNotEmpty() == true) {
            onPlainText(content)
        } else {
            false
        }
    }

    /**
     * 获取传入分享的 URI 列表
     *
     * 该方法用于检索从其他应用或通过内部使用
     * android.intent.action.SEND 或 android.intent.action.SEND_MULTIPLE 操作分享的文件。
     *
     * @param intent 分享意图
     * @param fallbackMimeType 备用 MIME 类型
     * @return 要分享的 URI 列表
     */
    private fun getIncomingUris(intent: Intent, fallbackMimeType: String): List<ShareIntentHandler.UriToShare> {
        val uriList = mutableListOf<Uri>()
        if (intent.action == Intent.ACTION_SEND) {
            IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
                ?.let { uriList.add(it) }
        } else if (intent.action == Intent.ACTION_SEND_MULTIPLE) {
            IntentCompat.getParcelableArrayListExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
                ?.let { uriList.addAll(it) }
        }
        val resInfoList: List<ResolveInfo> = context.packageManager.queryIntentActivitiesCompat(intent, PackageManager.MATCH_DEFAULT_ONLY)
        uriList.forEach { uri ->
            resInfoList.forEach resolve@{ resolveInfo ->
                val packageName: String = resolveInfo.activityInfo.packageName
                // Replace implicit intent by an explicit to fix crash on some devices like Xiaomi.
                // see https://juejin.cn/post/7031736325422186510
                try {
                    context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (e: Exception) {
                    Timber.w(e, "Unable to grant Uri permission")
                    return@resolve
                }
                intent.action = null
                intent.component = ComponentName(packageName, resolveInfo.activityInfo.name)
            }
        }
        return uriList.map { uri ->
            // The value in fallbackMimeType can be wrong, especially if several uris were received
            // in the same intent (i.e. 'image/*'). We need to check the mime type of each uri.
            val mimeType = context.contentResolver.getType(uri) ?: fallbackMimeType
            ShareIntentHandler.UriToShare(
                uri = uri,
                mimeType = mimeType,
            )
        }
    }

    /**
     * 撤销 URI 权限
     *
     * 在分享完成后撤销授予的 URI 读取权限。
     *
     * @param uris 要撤销权限的 URI 列表
     */
    private fun revokeUriPermissions(uris: List<Uri>) {
        uris.forEach { uri ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.revokeUriPermission(context.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    context.revokeUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } catch (e: Exception) {
                Timber.w(e, "Unable to revoke Uri permission")
            }
        }
    }
}
