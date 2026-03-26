/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.reporter

import java.io.File

/**
 * 问题报告器接口
 *
 * 定义了问题报告的核心功能接口，用于收集和提交问题报告。
 */
interface BugReporter {
    /**
     * 发送问题报告
     *
     * 将用户的问题报告发送到服务器，包含日志、截图等信息。
     *
     * @param withDevicesLogs 是否包含设备日志
     * @param withCrashLogs 是否包含崩溃日志
     * @param withScreenshot 是否包含截图
     * @param problemDescription 问题描述
     * @param canContact 用户是否同意被直接联系
     * @param sendPushRules 是否包含推送规则
     * @param listener 上传进度监听器
     */
    suspend fun sendBugReport(
        withDevicesLogs: Boolean,
        withCrashLogs: Boolean,
        withScreenshot: Boolean,
        problemDescription: String,
        canContact: Boolean = false,
        sendPushRules: Boolean = false,
        listener: BugReporterListener
    )

    /**
     * 获取日志目录
     *
     * 返回存储日志文件的目录路径。
     *
     * @return File 日志目录
     */
    fun logDirectory(): File

    /**
     * 设置当前追踪日志级别
     *
     * 配置日志追踪的详细程度。
     *
     * @param logLevel 日志级别
     */
    fun setCurrentTracingLogLevel(logLevel: String)

    /**
     * 保存日志猫日志
     *
     * 将当前的logcat输出保存到文件中。
     *
     * @return File? 保存的文件，如果失败则返回null
     */
    fun saveLogCat(): File?
}
