/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.services.analytics.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.di.SessionScope
import io.element.android.services.analytics.api.watchers.AnalyticsRoomListStateWatcher
import io.element.android.services.analytics.api.watchers.AnalyticsSendMessageWatcher
import io.element.android.services.analytics.impl.watchers.DefaultAnalyticsRoomListStateWatcher
import io.element.android.services.analytics.impl.watchers.DefaultAnalyticsSendMessageWatcher

/**
 * 这两个 watcher 的实现依赖 matrix 类型。
 * 将 enabled 绑定迁到 matrix impl 后，services:analytics 就不需要再反向依赖 matrix。
 */
@BindingContainer
@ContributesTo(SessionScope::class)
object EnabledMatrixSessionAnalyticsBindings {
    @Provides
    fun provideAnalyticsRoomListStateWatcher(
        defaultAnalyticsRoomListStateWatcher: DefaultAnalyticsRoomListStateWatcher,
    ): AnalyticsRoomListStateWatcher = defaultAnalyticsRoomListStateWatcher
}

@BindingContainer
@ContributesTo(RoomScope::class)
object EnabledMatrixRoomAnalyticsBindings {
    @Provides
    fun provideAnalyticsSendMessageWatcher(
        defaultAnalyticsSendMessageWatcher: DefaultAnalyticsSendMessageWatcher,
    ): AnalyticsSendMessageWatcher = defaultAnalyticsSendMessageWatcher
}
