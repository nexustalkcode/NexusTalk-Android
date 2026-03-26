/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.pip

/**
 * 画中画状态数据类
 *
 * 表示通话画中画模式的当前状态，用于在 UI 层展示和交互。
 *
 * @property supportPip 是否支持画中画模式（取决于 Android 版本和设备功能）
 * @property isInPictureInPicture 是否处于画中画模式
 * @property eventSink 事件处理函数，用于向 Presenter 发送事件
 *
 * @see PictureInPictureEvents 画中画事件
 * @see PictureInPicturePresenter 画中画 Presenter
 */
data class PictureInPictureState(
    /** 是否支持画中画模式（取决于 Android 版本和设备功能） */
    val supportPip: Boolean,
    /** 是否处于画中画模式 */
    val isInPictureInPicture: Boolean,
    /** 事件处理函数，用于向 Presenter 发送事件 */
    val eventSink: (PictureInPictureEvents) -> Unit,
)
