/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.file

/**
 * 文件查看事件密封接口
 *
 * 定义文件查看界面中可能发生的用户交互事件。
 * 使用密封接口实现类型安全的事件处理。
 *
 * @see ViewFileEvents.SaveOnDisk 保存到磁盘事件
 * @see ViewFileEvents.Share 分享事件
 */
sealed interface ViewFileEvents {
    /**
     * 保存到磁盘事件
     *
     * 触发将文件保存到设备存储的操作
     */
    data object SaveOnDisk : ViewFileEvents

    /**
     * 分享事件
     *
     * 触发文件分享操作，通过系统分享面板分享文件
     */
    data object Share : ViewFileEvents
}
