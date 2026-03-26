/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.reportroom.impl

import io.element.android.libraries.architecture.AsyncAction

/**
 * 举报房间界面的状态数据类
 *
 * 用于存储举报房间页面的所有界面状态信息
 *
 * @property reason 用户输入的举报原因
 * @property leaveRoom 是否在举报后离开房间
 * @property reportAction 举报操作的异步状态
 * @property eventSink 用于处理用户事件的函数引用
 */
data class ReportRoomState(
    /** 用户输入的举报原因文本 */
    val reason: String,

    /** 是否在举报后离开房间的开关状态 */
    val leaveRoom: Boolean,

    /** 举报操作的异步执行状态（未初始化/加载中/成功/失败） */
    val reportAction: AsyncAction<Unit>,

    /** 用于将用户事件发送到 Presenter 进行处理的函数 */
    val eventSink: (ReportRoomEvents) -> Unit
) {
    /**
     * 是否可以提交举报
     *
     * 只有当举报原因不为空时才能提交举报
     *
     * @return 如果举报原因不为空返回 true，否则返回 false
     */
    val canReport: Boolean = reason.isNotBlank()
}
