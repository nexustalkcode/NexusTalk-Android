/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.historyvisible

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * 历史可见性状态预览参数提供者
 *
 * 继承自 [PreviewParameterProvider]，用于在预览环境中提供 [HistoryVisibleState] 的示例数据。
 * 主要用于 Android Studio 的 Compose 预览功能，帮助开发者快速查看 UI 在不同状态下的渲染效果。
 *
 * @see HistoryVisibleState 历史可见性状态数据类
 * @see HistoryVisibleStateView 历史可见性状态视图
 */
class HistoryVisibleStateProvider : PreviewParameterProvider<HistoryVisibleState> {
    /**
     * 提供预览状态序列
     *
     * 此属性返回一个 [Sequence]，包含用于预览的状态对象。
     * 当前只提供一个显示警告的状态作为示例。
     *
     * @return 包含示例 [HistoryVisibleState] 对象的序列
     */
    override val values: Sequence<HistoryVisibleState>
    get() = sequenceOf(
            aHistoryVisibleState(showAlert = true),
        )
}

/**
 * 创建历史可见性状态测试辅助函数
 *
 * 此函数是内部辅助函数，用于在测试和预览中快速创建 [HistoryVisibleState] 实例。
 *
 * @param showAlert 是否显示警告，默认为 false
 * @param eventSink 事件处理函数，默认为空函数
 * @return 新创建的 [HistoryVisibleState] 实例
 *
 * @see HistoryVisibleState 历史可见性状态
 * @see HistoryVisibleEvent 历史可见性事件
 */
internal fun aHistoryVisibleState(
    showAlert: Boolean = false,
    eventSink: (HistoryVisibleEvent) -> Unit = {},
) = HistoryVisibleState(
    showAlert,
    eventSink = eventSink,
)
