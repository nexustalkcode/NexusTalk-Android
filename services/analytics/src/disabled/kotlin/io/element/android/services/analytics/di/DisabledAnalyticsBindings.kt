/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.services.analytics.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.di.identifiers.SentrySdkDsn
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analytics.api.ScreenTracker
import io.element.android.services.analytics.api.watchers.AnalyticsColdStartWatcher
import io.element.android.services.analytics.api.watchers.AnalyticsRoomListStateWatcher
import io.element.android.services.analytics.api.watchers.AnalyticsSendMessageWatcher
import io.element.android.services.analytics.noop.NoopAnalyticsService
import io.element.android.services.analytics.noop.NoopScreenTracker
import io.element.android.services.analytics.noop.watchers.NoopAnalyticsColdStartWatcher
import io.element.android.services.analytics.noop.watchers.NoopAnalyticsRoomListStateWatcher
import io.element.android.services.analytics.noop.watchers.NoopAnalyticsSendMessageWatcher

/**
 * 单模块模式下的禁用态绑定入口。
 *
 * analytics 关闭时，应用层仍然只依赖 :services:analytics，
 * 但 Metro 最终会在这里统一切到 noop 实现，并补上禁用场景下需要的空 Sentry 配置。
 */
@BindingContainer
@ContributesTo(AppScope::class)
object DisabledAppAnalyticsBindings {
    @Provides
    fun provideAnalyticsService(noopAnalyticsService: NoopAnalyticsService): AnalyticsService = noopAnalyticsService

    @Provides
    fun provideScreenTracker(noopScreenTracker: NoopScreenTracker): ScreenTracker = noopScreenTracker

    @Provides
    fun provideAnalyticsColdStartWatcher(
        noopAnalyticsColdStartWatcher: NoopAnalyticsColdStartWatcher,
    ): AnalyticsColdStartWatcher = noopAnalyticsColdStartWatcher

    @Provides
    fun provideSentrySdkDsn(): SentrySdkDsn? = null
}

@BindingContainer
@ContributesTo(SessionScope::class)
object DisabledSessionAnalyticsBindings {
    @Provides
    fun provideAnalyticsRoomListStateWatcher(
        noopAnalyticsRoomListStateWatcher: NoopAnalyticsRoomListStateWatcher,
    ): AnalyticsRoomListStateWatcher = noopAnalyticsRoomListStateWatcher
}

@BindingContainer
@ContributesTo(RoomScope::class)
object DisabledRoomAnalyticsBindings {
    @Provides
    fun provideAnalyticsSendMessageWatcher(
        noopAnalyticsSendMessageWatcher: NoopAnalyticsSendMessageWatcher,
    ): AnalyticsSendMessageWatcher = noopAnalyticsSendMessageWatcher
}
