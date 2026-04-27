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
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analytics.api.ScreenTracker
import io.element.android.services.analytics.api.watchers.AnalyticsColdStartWatcher
import io.element.android.services.analytics.impl.DefaultAnalyticsService
import io.element.android.services.analytics.impl.DefaultScreenTracker
import io.element.android.services.analytics.impl.watchers.DefaultAnalyticsColdStartWatcher

/**
 * 单模块模式下的启用态绑定入口。
 *
 * 这里单独放在 enabled 源码目录里，是为了让 Gradle 只编译一套接口绑定，
 * 从而避免 impl/noop 在同一个构建里同时向 Metro 注册重复绑定。
 */
@BindingContainer
@ContributesTo(AppScope::class)
object EnabledAppAnalyticsBindings {
    @Provides
    fun provideAnalyticsService(defaultAnalyticsService: DefaultAnalyticsService): AnalyticsService = defaultAnalyticsService

    @Provides
    fun provideScreenTracker(defaultScreenTracker: DefaultScreenTracker): ScreenTracker = defaultScreenTracker

    @Provides
    fun provideAnalyticsColdStartWatcher(
        defaultAnalyticsColdStartWatcher: DefaultAnalyticsColdStartWatcher,
    ): AnalyticsColdStartWatcher = defaultAnalyticsColdStartWatcher
}
