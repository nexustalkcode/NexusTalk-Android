/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.attachments.video

import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.mediaviewer.api.local.LocalMedia

/**
 * 媒体优化选择器Presenter接口
 *
 * 定义媒体优化选择器的业务逻辑接口。
 * 负责处理图片和视频的优化选项选择。
 *
 * 功能：
 * - 提供图片优化选项
 * - 提供视频压缩预设选择
 * - 计算视频压缩后的估算大小
 *
 * @see DefaultMediaOptimizationSelectorPresenter 默认实现
 * @see MediaOptimizationSelectorState 选择器状态
 */
fun interface MediaOptimizationSelectorPresenter : Presenter<MediaOptimizationSelectorState> {
    /**
     * Presenter工厂接口
     *
     * 用于创建MediaOptimizationSelectorPresenter实例
     */
    interface Factory {
        /**
         * 创建媒体优化选择器Presenter
         *
         * @param localMedia 要优化的本地媒体
         * @return MediaOptimizationSelectorPresenter实例
         */
        fun create(
            localMedia: LocalMedia,
        ): MediaOptimizationSelectorPresenter
    }
}
