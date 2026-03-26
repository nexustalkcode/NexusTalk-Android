/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl

import io.element.android.libraries.core.log.logger.LoggerTag

/**
 * 安全备份模块日志标签
 *
 * 定义了用于安全备份功能日志记录的标签，用于在日志中标识和过滤安全备份相关的日志信息。
 */
private val loggerTag = LoggerTag("SecureBackup")

/**
 * 安全备份根页面日志标签
 */
val loggerTagRoot = LoggerTag("Root", loggerTag)

/**
 * 安全备份设置页面日志标签
 */
val loggerTagSetup = LoggerTag("Setup", loggerTag)

/**
 * 安全备份禁用页面日志标签
 */
val loggerTagDisable = LoggerTag("Disable", loggerTag)
