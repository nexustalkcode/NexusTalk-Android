/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.startup.AppInitializer
import androidx.work.Configuration
import dev.zacsweers.metro.createGraphFactory
import io.element.android.libraries.di.DependencyInjectionGraphOwner
import io.element.android.libraries.push.api.badge.BadgeManager
import io.element.android.libraries.workmanager.api.di.MetroWorkerFactory
import io.element.android.x.di.AppGraph
import io.element.android.x.info.logApplicationInfo
import io.element.android.x.initializer.CacheCleanerInitializer
import io.element.android.x.initializer.CrashInitializer
import io.element.android.x.initializer.PlatformInitializer

/**
 * ElementX 应用程序的主 Application 类。
 *
 * 负责初始化应用级别的依赖注入图和工作管理器配置。
 * 在应用启动时，会通过 AppInitializer 依次初始化 CrashInitializer、PlatformInitializer 和 CacheCleanerInitializer。
 * 继承自 Application、DependencyInjectionGraphOwner 和 Configuration.Provider 接口，
 * 分别用于提供应用上下文、访问全局依赖注入图和配置 WorkManager。
 */
class ElementXApplication : Application(), DependencyInjectionGraphOwner, Configuration.Provider {
    override val graph: AppGraph = createGraphFactory<AppGraph.Factory>().create(this)

    override val workManagerConfiguration: Configuration = Configuration.Builder()
        .setWorkerFactory(MetroWorkerFactory(graph.workerProviders))
        .build()

    private val badgeLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            BadgeManager.scheduleRefreshFromLastKnownCount(this@ElementXApplication)
        }
    }

    override fun onCreate() {
        super.onCreate()
        AppInitializer.getInstance(this).apply {
            initializeComponent(CrashInitializer::class.java)
            initializeComponent(PlatformInitializer::class.java)
            initializeComponent(CacheCleanerInitializer::class.java)
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(badgeLifecycleObserver)

        logApplicationInfo(this)
    }
}
