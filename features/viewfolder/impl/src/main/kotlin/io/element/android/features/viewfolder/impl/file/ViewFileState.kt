/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.file

import io.element.android.libraries.architecture.AsyncData

/**
 * 文件查看状态数据类
 *
 * 表示文件查看界面的当前状态，包含文件名、文件内容行和着色模式。
 * 使用不可变数据结构，确保状态的可预测性和线程安全。
 *
 * @property name 文件名称
 * @property lines 文件内容的异步数据状态
 * @property colorationMode 着色模式，用于日志文件的高亮显示
 * @property eventSink 事件处理函数
 */
data class ViewFileState(
    val name: String,
    val lines: AsyncData<List<String>>,
    val colorationMode: ColorationMode,
    val eventSink: (ViewFileEvents) -> Unit,
)

/**
 * 文件着色模式枚举
 *
 * 定义不同类型日志文件的着色模式，支持根据日志级别显示不同颜色。
 */
enum class ColorationMode {
    /** Logcat 日志格式 */
    Logcat,
    /** Rust 日志格式 */
    RustLogs,
    /** 无着色模式 */
    None,
}
