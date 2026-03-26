/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.pip

/**
 * 创建画中画状态工具函数
 *
 * 用于在测试或预览时快速创建画中画状态对象。
 *
 * @param supportPip 是否支持画中画模式（默认 false）
 * @param isInPictureInPicture 是否处于画中画模式（默认 false）
 * @param eventSink 事件处理函数（默认空函数）
 * @return PictureInPictureState 画中画状态对象
 *
 * @see PictureInPictureState 画中画状态数据类
 */
fun aPictureInPictureState(
    supportPip: Boolean = false,
    isInPictureInPicture: Boolean = false,
    eventSink: (PictureInPictureEvents) -> Unit = {},
): PictureInPictureState {
    return PictureInPictureState(
        supportPip = supportPip,
        isInPictureInPicture = isInPictureInPicture,
        eventSink = eventSink,
    )
}
