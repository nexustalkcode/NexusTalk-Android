/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.reportroom.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction

/**
 * 举报房间状态的预览参数提供者
 *
 * 用于在预览和测试环境中提供不同的状态样本
 * 继承自 Compose 的 PreviewParameterProvider，用于支持多个预览场景
 */
open class ReportRoomStateProvider : PreviewParameterProvider<ReportRoomState> {
    /**
     * 伴生对象，包含预览用的常量
     */
    companion object {
        /** 预览用的示例举报原因文本 */
        private const val A_REPORT_ROOM_REASON = "Inappropriate content"
    }

    /**
     * 提供状态样本的序列
     *
     * 包含多种不同的状态组合，用于展示不同的界面状态：
     * 1. 默认状态（空原因）
     * 2. 已填写原因
     * 3. 开启离开房间开关
     * 4. 举报进行中（加载中）
     * 5. 举报失败
     */
    override val values: Sequence<ReportRoomState>
        get() = sequenceOf(
            aReportRoomState(),
            aReportRoomState(reason = A_REPORT_ROOM_REASON),
            aReportRoomState(leaveRoom = true),
            aReportRoomState(reason = A_REPORT_ROOM_REASON, reportAction = AsyncAction.Loading),
            aReportRoomState(reason = A_REPORT_ROOM_REASON, reportAction = AsyncAction.Failure(Exception("Failed to report"))),
        )
}

/**
 * 创建举报房间状态的辅助函数
 *
 * 用于在测试和预览中快速创建 [ReportRoomState] 实例
 *
 * @param reason 举报原因文本，默认为空字符串
 * @param leaveRoom 是否离开房间，默认为 false
 * @param reportAction 举报操作状态，默认为未初始化
 * @param eventSink 事件处理函数，默认为空函数
 * @return 返回构建的 [ReportRoomState] 实例
 */
fun aReportRoomState(
    reason: String = "",
    leaveRoom: Boolean = false,
    reportAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (ReportRoomEvents) -> Unit = {}
) = ReportRoomState(
    reason = reason,
    leaveRoom = leaveRoom,
    reportAction = reportAction,
    eventSink = eventSink,
)
