/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.enterprise.api

/**
 * 问题报告 URL 密封接口
 *
 * 定义问题报告 URL 的类型，用于配置问题报告功能。
 * 支持默认行为、自定义 URL 和禁用三种状态。
 *
 * @see BugReportUrl.UseDefault 使用默认 URL
 * @see BugReportUrl.Disabled 禁用问题报告
 * @see BugReportUrl.Custom 自定义 URL
 */
sealed interface BugReportUrl {
    /**
     * 使用默认问题报告 URL
     */
    data object UseDefault : BugReportUrl

    /**
     * 禁用问题报告功能
     */
    data object Disabled : BugReportUrl

    /**
     * 使用自定义问题报告 URL
     *
     * @property url 自定义问题报告 URL
     */
    data class Custom(
        val url: String,
    ) : BugReportUrl
}
