/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.reporter

import kotlinx.coroutines.flow.Flow
import okhttp3.HttpUrl

/**
 * 问题报告URL提供者接口
 *
 * 定义获取问题报告提交URL的接口。
 */
fun interface BugReporterUrlProvider {
    /**
     * 提供问题报告URL
     *
     * 返回一个问题报告提交的URL，可能为null（如果功能不可用）。
     *
     * @return Flow<HttpUrl?> 问题报告URL的可空流
     */
    fun provide(): Flow<HttpUrl?>
}
