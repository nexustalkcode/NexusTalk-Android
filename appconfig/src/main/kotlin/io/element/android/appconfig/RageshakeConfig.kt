/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

/**
 * 崩溃报告配置 (Rageshake Configuration)
 *
 * 此对象包含崩溃报告（又称Rageshake）功能相关的配置项。
 * Rageshake是Matrix社区开发的崩溃报告工具，用于收集和提交应用日志帮助调试问题。
 */
object RageshakeConfig {
    /** 提交崩溃报告的目标URL地址。用户的崩溃报告将被发送到此URL进行处理 */
    const val BUG_REPORT_URL = BuildConfig.BUG_REPORT_URL

    /**
     * 应用程序标识符。用于标识提交崩溃报告的应用类型。
     * 应与GitHub问题跟踪器配置中的映射相对应，以确保崩溃报告能正确分配和处理。
     * 例如：'riot-web'、'element-android'等。
     * 参考：https://github.com/matrix-org/rageshake
     */
    const val BUG_REPORT_APP_NAME = BuildConfig.BUG_REPORT_APP_NAME

    /** 崩溃报告上传请求的最大大小（字节）。默认值为50MB，略低于CloudFlare的最大请求大小限制 */
    const val MAX_LOG_UPLOAD_SIZE = 50 * 1024 * 1024L

    /** 单个日志文件的最大大小（字节）。用于限制收集的日志内容大小，防止生成过大的报告 */
    const val MAX_LOG_CONTENT_SIZE = 100 * 1024 * 1024L

    /** 崩溃报告可以包含的最大日志行数。用于限制日志条目的数量，确保报告的可读性和处理效率 */
    const val MAX_LOG_LINES_SIZE = 1_000_000
}
