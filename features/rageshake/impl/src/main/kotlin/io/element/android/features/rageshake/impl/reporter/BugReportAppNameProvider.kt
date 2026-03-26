/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.reporter

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.appconfig.RageshakeConfig

/**
 * 问题报告应用名称提供者接口
 *
 * 定义获取问题报告时使用的应用名称的接口。
 */
fun interface BugReportAppNameProvider {
    /**
     * 提供应用名称
     *
     * @return String 应用名称
     */
    fun provide(): String
}

/**
 * 默认问题报告应用名称提供者
 *
 * 使用配置中的应用名称。
 */
@ContributesBinding(AppScope::class)
class DefaultBugReportAppNameProvider : BugReportAppNameProvider {
    /**
     * 提供应用名称
     *
     * @return String 从配置中获取的应用名称
     */
    override fun provide(): String = RageshakeConfig.BUG_REPORT_APP_NAME
}
