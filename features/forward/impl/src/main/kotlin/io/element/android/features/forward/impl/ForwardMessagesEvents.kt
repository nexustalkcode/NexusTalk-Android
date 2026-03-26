/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.forward.impl

/**
 * 转发消息事件 sealed 接口
 *
 * 定义了转发消息功能中所有可能发生的用户事件。
 */
sealed interface ForwardMessagesEvents {
    /**
     * 清除错误事件
     *
     * 当转发操作发生错误时，用户点击关闭错误提示时触发此事件。
     * 会将转发状态重置为未初始化状态。
     */
    data object ClearError : ForwardMessagesEvents
}
