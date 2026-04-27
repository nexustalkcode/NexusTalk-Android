/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.services.analytics.impl.store

import kotlinx.coroutines.flow.Flow

/**
 * analytics 本地存储抽象。
 *
 * 这里只保留接口，具体基于 PreferenceDataStore 的实现下沉到 preferences 模块，
 * 用来打断 `services:analytics -> libraries:preferences` 的主依赖边。
 */
interface AnalyticsStore {
    val userConsentFlow: Flow<Boolean>
    val didAskUserConsentFlow: Flow<Boolean>
    val analyticsIdFlow: Flow<String>
    suspend fun setUserConsent(newUserConsent: Boolean)
    suspend fun setDidAskUserConsent(newValue: Boolean = true)
    suspend fun setAnalyticsId(newAnalyticsId: String)
    suspend fun reset()
}
