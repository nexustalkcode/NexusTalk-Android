/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.rageshake.api.RageshakeFeatureAvailability
import io.element.android.features.rageshake.impl.reporter.BugReporterUrlProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 默认摇一摇功能可用性实现
 *
 * 通过检查问题报告URL是否配置来确定功能是否可用。
 *
 * @property bugReporterUrlProvider 问题报告URL提供者
 */
@ContributesBinding(AppScope::class)
class DefaultRageshakeFeatureAvailability(
    private val bugReporterUrlProvider: BugReporterUrlProvider,
) : RageshakeFeatureAvailability {
    /**
     * 检查功能是否可用
     *
     * 根据问题报告URL是否配置来返回功能可用性。
     *
     * @return Flow<Boolean> 功能可用性的布尔值流
     */
    override fun isAvailable(): Flow<Boolean> {
        return bugReporterUrlProvider.provide()
            .map { it != null }
    }
}
