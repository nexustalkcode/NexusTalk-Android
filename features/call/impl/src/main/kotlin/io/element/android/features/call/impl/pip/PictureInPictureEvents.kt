/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.pip

import io.element.android.features.call.impl.utils.PipController

/**
 * 画中画事件密封接口
 *
 * 定义了画中画模式相关的各种事件，用于在 UI 和 Presenter 之间传递事件。
 *
 * @see SetPipController 设置画中画控制器事件
 * @see EnterPictureInPicture 进入画中画模式事件
 * @see OnPictureInPictureModeChanged 画中画模式变更事件
 */
sealed interface PictureInPictureEvents {
    /**
     * 设置画中画控制器事件
     *
     * 用于传递画中画控制器实例，以便在需要时执行画中画操作。
     *
     * @property pipController 画中画控制器实例
     */
    data class SetPipController(val pipController: PipController) : PictureInPictureEvents

    /**
     * 进入画中画模式事件
     *
     * 触发进入画中画模式的操作。
     */
    data object EnterPictureInPicture : PictureInPictureEvents

    /**
     * 画中画模式变更事件
     *
     * 当系统画中画模式状态发生变化时触发。
     *
     * @property isInPip 是否处于画中画模式
     */
    data class OnPictureInPictureModeChanged(val isInPip: Boolean) : PictureInPictureEvents
}
