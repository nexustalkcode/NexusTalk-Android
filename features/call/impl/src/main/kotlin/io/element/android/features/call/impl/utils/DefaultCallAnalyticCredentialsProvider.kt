/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.utils

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.call.impl.BuildConfig
import io.element.android.libraries.matrix.api.widget.CallAnalyticCredentialsProvider

/**
 * 通话分析凭证提供者默认实现
 *
 * 提供 Element Call 所需的分析服务凭证，包括 PostHog 和 Sentry。
 * 凭证从 BuildConfig 中读取，如果为空则返回 null。
 *
 * @see CallAnalyticCredentialsProvider 分析凭证提供者接口
 */
@ContributesBinding(AppScope::class)
class DefaultCallAnalyticCredentialsProvider : CallAnalyticCredentialsProvider {
    /** PostHog 用户 ID */
    override val posthogUserId: String? = BuildConfig.POSTHOG_USER_ID.takeIf { it.isNotBlank() }
    /** PostHog API 主机地址 */
    override val posthogApiHost: String? = BuildConfig.POSTHOG_API_HOST.takeIf { it.isNotBlank() }
    /** PostHog API 密钥 */
    override val posthogApiKey: String? = BuildConfig.POSTHOG_API_KEY.takeIf { it.isNotBlank() }
    /** Rageshake 提交 URL */
    override val rageshakeSubmitUrl: String? = BuildConfig.RAGESHAKE_URL.takeIf { it.isNotBlank() }
    /** Sentry DSN */
    override val sentryDsn: String? = BuildConfig.SENTRY_DSN.takeIf { it.isNotBlank() }
}
