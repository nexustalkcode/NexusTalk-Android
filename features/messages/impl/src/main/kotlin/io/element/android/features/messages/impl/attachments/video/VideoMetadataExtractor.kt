/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments.video

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Size
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.di.annotations.ApplicationContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * 视频元数据提取器接口
 *
 * 用于从视频文件中提取元数据信息。
 * 实现 AutoCloseable 接口以释放资源。
 *
 * 主要提取信息：
 * - 视频尺寸（宽度和高度）
 * - 视频时长
 *
 * @see DefaultVideoMetadataExtractor 默认实现
 */
interface VideoMetadataExtractor : AutoCloseable {
    /**
     * 获取视频尺寸
     *
     * @return Result 包含视频宽度和高度，如果失败则返回错误
     */
    fun getSize(): Result<Size>

    /**
     * 获取视频时长
     *
     * @return Result 包含视频时长，如果失败则返回错误
     */
    fun getDuration(): Result<Duration>

    /**
     * 工厂接口
     *
     * 用于创建 VideoMetadataExtractor 实例
     */
    interface Factory {
        /**
         * 创建视频元数据提取器
         *
         * @param uri 视频文件的URI
         * @return VideoMetadataExtractor 实例
         */
        fun create(uri: Uri): VideoMetadataExtractor
    }
}

/**
 * 默认视频元数据提取器实现
 *
 * 使用 Android 的 MediaMetadataRetriever 来提取视频元数据。
 * 支持获取视频的分辨率和时长信息。
 *
 * @property context Android应用上下文
 * @property uri 视频文件的URI
 */
@ContributesBinding(AppScope::class)
@AssistedInject
class DefaultVideoMetadataExtractor(
    @ApplicationContext private val context: Context,
    @Assisted private val uri: Uri,
) : VideoMetadataExtractor {
    /**
     * 工厂接口
     *
     * 用于创建 DefaultVideoMetadataExtractor 实例
     */
    @ContributesBinding(AppScope::class)
    @AssistedFactory
    interface Factory : VideoMetadataExtractor.Factory {
        override fun create(uri: Uri): DefaultVideoMetadataExtractor
    }

    // 不使用 lazy 代理，以便捕获初始化期间的异常
    /**
     * 媒体元数据检索器
     *
     * 用于访问视频文件的元数据，使用懒加载初始化
     */
    private val mediaMetadataRetriever = lazy {
        MediaMetadataRetriever().apply {
            setDataSource(context, uri)
        }
    }

    /**
     * 获取视频尺寸
     *
     * 从视频元数据中提取宽度和高度信息
     *
     * @return Result 包含 Size 对象，失败时返回错误
     */
    override fun getSize(): Result<Size> = runCatchingExceptions {
        val width = mediaMetadataRetriever.value.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt()
        val height = mediaMetadataRetriever.value.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt()

        @Suppress("ComplexCondition")
        if (width != null && width > 0 && height != null && height > 0) {
           Size(width, height)
        } else {
            error("Could not retrieve video size from metadata for $uri")
        }
    }

    /**
     * 获取视频时长
     *
     * 从视频元数据中提取时长信息（毫秒）
     *
     * @return Result 包含 Duration 对象，失败时返回错误
     */
    override fun getDuration(): Result<Duration> = runCatchingExceptions {
        mediaMetadataRetriever.value.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
            ?.takeIf { it > 0L }
            ?.milliseconds
            ?: error("Could not retrieve video duration from metadata")
    }

    /**
     * 释放资源
     *
     * 关闭并释放 MediaMetadataRetriever
     */
    override fun close() {
        if (mediaMetadataRetriever.isInitialized()) {
            mediaMetadataRetriever.value.release()
        }
    }
}
