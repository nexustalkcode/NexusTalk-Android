/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.core.mimetype.MimeTypes.isMimeTypeVideo
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.mediaupload.api.MaxUploadSizeProvider
import io.element.android.libraries.mediaupload.api.MediaOptimizationConfigProvider
import io.element.android.libraries.mediaupload.api.compressorHelper
import io.element.android.libraries.mediaviewer.api.local.LocalMedia
import io.element.android.libraries.preferences.api.store.VideoCompressionPreset
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import timber.log.Timber
import kotlin.math.roundToLong

/**
 * 默认媒体优化选择器Presenter
 *
 * [MediaOptimizationSelectorPresenter] 的默认实现。
 * 负责管理媒体优化选项的状态和逻辑。
 *
 * 主要功能：
 * - 根据功能开关控制是否显示优化选项
 * - 获取服务器允许的最大上传大小
 * - 计算视频压缩后的估算文件大小
 * - 管理用户的选择状态
 *
 * @property localMedia 要优化的本地媒体
 * @property maxUploadSizeProvider 最大上传大小提供者
 * @property featureFlagService 功能开关服务
 * @property mediaOptimizationConfigProvider 媒体优化配置提供者
 * @property mediaExtractorFactory 视频元数据提取器工厂
 */
@AssistedInject
class DefaultMediaOptimizationSelectorPresenter(
    @Assisted private val localMedia: LocalMedia,
    private val maxUploadSizeProvider: MaxUploadSizeProvider,
    private val featureFlagService: FeatureFlagService,
    private val mediaOptimizationConfigProvider: MediaOptimizationConfigProvider,
    mediaExtractorFactory: VideoMetadataExtractor.Factory,
) : MediaOptimizationSelectorPresenter {
    /**
     * 工厂接口
     *
     * 用于创建 DefaultMediaOptimizationSelectorPresenter 实例
     */
    @ContributesBinding(SessionScope::class)
    @AssistedFactory
    interface Factory : MediaOptimizationSelectorPresenter.Factory {
        override fun create(
            localMedia: LocalMedia,
        ): DefaultMediaOptimizationSelectorPresenter
    }

    /**
     * 视频元数据提取器
     *
     * 用于提取视频的尺寸和时长信息
     */
    private val mediaExtractor = mediaExtractorFactory.create(localMedia.uri)

    /**
     * 生成并返回媒体优化选择器状态
     *
     * Compose Composable函数，作为Presenter的主入口。
     * 负责初始化状态、获取服务器配置、计算视频估算大小。
     *
     * @return MediaOptimizationSelectorState 包含所有视图所需数据的不可变状态对象
     */
    @Composable
    override fun present(): MediaOptimizationSelectorState {
        // 检查功能开关，确定是否显示媒体优化选择器
        val displayMediaSelectorViews by produceState<Boolean?>(null) {
            value = featureFlagService.isFeatureEnabled(FeatureFlags.SelectableMediaQuality)
        }

        // 视频预设选择对话框显示状态
        var displayVideoPresetSelectorDialog by remember { mutableStateOf(false) }

        // 获取服务器允许的最大上传大小
        val maxUploadSize by produceState(AsyncData.Loading()) {
            maxUploadSizeProvider.getMaxUploadSize().fold(
                onSuccess = { value = AsyncData.Success(it) },
                onFailure = {
                    Timber.e(it, "Failed to retrieve max upload size for video optimization selector")
                    // 如果无法获取最大上传大小，默认为100MB
                    value = AsyncData.Success((100 * 1024 * 1024).toLong())
                }
            )
        }

        val mediaMimeType = localMedia.info.mimeType

        // 计算各种视频压缩预设的估算文件大小
        val videoSizeEstimations by produceState<AsyncData<ImmutableList<VideoUploadEstimation>>>(
            initialValue = AsyncData.Loading(),
            key1 = maxUploadSize,
        ) {
            if (maxUploadSize !is AsyncData.Success) {
                return@produceState
            }

            // 如果不是视频类型，则不计算
            if (!mediaMimeType.isMimeTypeVideo()) {
                value = AsyncData.Uninitialized
                return@produceState
            }

            // 提取视频元数据（尺寸和时长）
            val (videoDimensions, duration) = mediaExtractor.use {
                val size = it.getSize()
                    .getOrElse { exception ->
                        value = AsyncData.Failure(exception)
                        return@produceState
                    }

                val duration = it.getDuration()
                    .getOrElse { exception ->
                        value = AsyncData.Failure(exception)
                        return@produceState
                    }
                size to duration
            }

            // 计算每种预设的估算文件大小
            val sizeEstimations = VideoCompressionPreset.entries
                .map { preset ->
                    val bitRateAsBytes = preset.compressorHelper().calculateOptimalBitrate(videoDimensions, 30) / 8f
                    val durationInSeconds = duration.inWholeSeconds.toFloat()
                    // 添加10%的安全余量
                    val calculatedSize = (bitRateAsBytes * durationInSeconds * 1.1f).roundToLong()
                    VideoUploadEstimation(
                        preset = preset,
                        sizeInBytes = calculatedSize,
                        canUpload = calculatedSize <= (maxUploadSize as AsyncData.Success).data
                    )
                }
                .toImmutableList()
                .also { sizes ->
                    Timber.d(sizes.joinToString("\n") { "Calculated size for ${it.preset}: ${it.sizeInBytes} MB. Max upload size: $maxUploadSize" })
                }

            value = AsyncData.Success(sizeEstimations)
        }

        // 用户选择的图片优化状态
        var selectedImageOptimization by remember { mutableStateOf<AsyncData<Boolean>>(AsyncData.Loading()) }
        // 用户选择的视频预设状态
        var selectedVideoOptimizationPreset by remember { mutableStateOf<AsyncData<VideoCompressionPreset>>(AsyncData.Loading()) }

        // 初始化默认选项
        LaunchedEffect(videoSizeEstimations.dataOrNull()) {
            val mediaOptimizationConfig = mediaOptimizationConfigProvider.get()
            selectedImageOptimization = AsyncData.Success(mediaOptimizationConfig.compressImages)
            // 根据默认预设和视频大小估算找到最佳视频预设
            // 由于当前预设的估算可能太大无法上传，我们检查提供较小文件大小的选项
            selectedVideoOptimizationPreset = findBestVideoPreset(
                defaultVideoPreset = mediaOptimizationConfig.videoCompressionPreset,
                videoSizeEstimations = videoSizeEstimations,
            )
        }

        /**
         * 处理用户事件
         *
         * 根据不同的事件类型执行相应的业务逻辑
         *
         * @param event 用户交互事件
         */
        fun handleEvent(event: MediaOptimizationSelectorEvent) {
            when (event) {
                is MediaOptimizationSelectorEvent.SelectImageOptimization -> {
                    selectedImageOptimization = AsyncData.Success(event.enabled)
                }
                is MediaOptimizationSelectorEvent.SelectVideoPreset -> {
                    val estimations = videoSizeEstimations.dataOrNull()
                    if (estimations != null) {
                        val preset = estimations.find { it.preset == event.preset }
                        if (preset == null) {
                            Timber.e("Selected video preset ${event.preset} is not available in the estimations")
                            return
                        }
                        if (!preset.canUpload) {
                            Timber.w("Selected video preset ${event.preset} exceeds max upload size")
                            return
                        }
                    } else {
                        Timber.e("Video size estimations are not available")
                        return
                    }
                    selectedVideoOptimizationPreset = AsyncData.Success(event.preset)
                    displayVideoPresetSelectorDialog = false
                }
                is MediaOptimizationSelectorEvent.OpenVideoPresetSelectorDialog -> {
                    displayVideoPresetSelectorDialog = true
                }
                is MediaOptimizationSelectorEvent.DismissVideoPresetSelectorDialog -> {
                    displayVideoPresetSelectorDialog = false
                }
            }
        }

        return MediaOptimizationSelectorState(
            maxUploadSize = maxUploadSize,
            videoSizeEstimations = videoSizeEstimations,
            isImageOptimizationEnabled = selectedImageOptimization.dataOrNull(),
            selectedVideoPreset = selectedVideoOptimizationPreset.dataOrNull(),
            displayMediaSelectorViews = displayMediaSelectorViews,
            displayVideoPresetSelectorDialog = displayVideoPresetSelectorDialog,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 查找最佳视频预设
     *
     * 根据默认预设和可用估算找到可以上传的最佳视频预设。
     * 会尝试找到不低于默认预设质量且可以上传的预设。
     *
     * @param defaultVideoPreset 默认视频压缩预设
     * @param videoSizeEstimations 视频大小估算列表
     * @return 最佳视频预设的AsyncData
     */
    private fun findBestVideoPreset(
        defaultVideoPreset: VideoCompressionPreset,
        videoSizeEstimations: AsyncData<ImmutableList<VideoUploadEstimation>>,
    ): AsyncData<VideoCompressionPreset> {
        val estimations = videoSizeEstimations.dataOrNull() ?: return AsyncData.Loading()
        // 这将找到可用于生成可上传视频的最佳视频预设
        val bestEstimation = estimations.find { it.preset.ordinal >= defaultVideoPreset.ordinal && it.canUpload }?.preset
        return if (bestEstimation != null) {
            AsyncData.Success(bestEstimation)
        } else {
            AsyncData.Failure(
                IllegalStateException("No suitable video preset found for default preset: $defaultVideoPreset")
            )
        }
    }
}
