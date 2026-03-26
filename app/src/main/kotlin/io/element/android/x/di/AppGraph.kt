/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x.di

import android.content.Context
import androidx.work.ListenableWorker
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import io.element.android.libraries.architecture.NodeFactoriesBindings
import io.element.android.libraries.di.annotations.ApplicationContext
import io.element.android.libraries.workmanager.api.di.MetroWorkerFactory
import kotlin.reflect.KClass

/**
 * 应用依赖注入图接口。
 *
 * 定义了应用级别的依赖注入关系。
 * 使用 Metro 依赖注入框架的 @DependencyGraph 注解标记。
 * 继承自 NodeFactoriesBindings，提供节点工厂绑定。
 *
 * 包含以下依赖关系：
 * - sessionGraphFactory：会话图的工厂接口，用于创建 SessionGraph
 * - workerProviders：WorkManager 工作者提供者映射
 *
 * Factory 接口定义了创建 AppGraph 实例的工厂方法，
 * 接收 ApplicationContext 作为参数。
 */
@DependencyGraph(AppScope::class)
interface AppGraph : NodeFactoriesBindings {
    val sessionGraphFactory: SessionGraph.Factory

    @Multibinds
    val workerProviders:
        Map<KClass<out ListenableWorker>, MetroWorkerFactory.WorkerInstanceFactory<*>>

    @DependencyGraph.Factory
    interface Factory {
        fun create(
            @ApplicationContext @Provides
            context: Context
        ): AppGraph
    }
}
