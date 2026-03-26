/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.reporter

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.appconfig.RageshakeConfig
import io.element.android.features.enterprise.api.BugReportUrl
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.sessionstorage.api.SessionStore
import io.element.android.libraries.sessionstorage.api.sessionIdFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

/**
 * 默认问题报告URL提供者
 *
 * BugReporterUrlProvider 接口的实现，根据企业配置和用户会话提供问题报告URL。
 *
 * @property bugReportAppNameProvider 应用名称提供者
 * @property enterpriseService 企业服务
 * @property sessionStore 会话存储
 */
@ContributesBinding(AppScope::class)
class DefaultBugReporterUrlProvider(
    private val bugReportAppNameProvider: BugReportAppNameProvider,
    private val enterpriseService: EnterpriseService,
    private val sessionStore: SessionStore,
) : BugReporterUrlProvider {
    /**
     * 提供问题报告URL
     *
     * 根据企业配置和当前会话返回问题报告URL。
     * 支持自定义URL、默认URL和禁用三种状态。
     *
     * @return Flow<HttpUrl?> 问题报告URL的可空流
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun provide(): Flow<HttpUrl?> {
        if (bugReportAppNameProvider.provide().isEmpty()) return flowOf(null)
        return sessionStore.sessionIdFlow().flatMapLatest { sessionId ->
            enterpriseService.bugReportUrlFlow(sessionId?.let(::SessionId))
                .map { bugReportUrl ->
                    when (bugReportUrl) {
                        is BugReportUrl.Custom -> bugReportUrl.url
                        BugReportUrl.Disabled -> null
                        BugReportUrl.UseDefault -> RageshakeConfig.BUG_REPORT_URL.takeIf { it.isNotEmpty() }
                    }
                }
                .map { it?.toHttpUrl() }
        }
    }
}
