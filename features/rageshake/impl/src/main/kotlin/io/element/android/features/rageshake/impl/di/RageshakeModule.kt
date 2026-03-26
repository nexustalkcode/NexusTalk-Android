/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.impl.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import io.element.android.features.rageshake.api.crash.CrashDetectionPresenter
import io.element.android.features.rageshake.api.crash.CrashDetectionState
import io.element.android.features.rageshake.api.detection.RageshakeDetectionPresenter
import io.element.android.features.rageshake.api.detection.RageshakeDetectionState
import io.element.android.features.rageshake.api.preferences.RageshakePreferencesPresenter
import io.element.android.features.rageshake.api.preferences.RageshakePreferencesState
import io.element.android.libraries.architecture.Presenter

/**
 * Rageshake 依赖注入模块
 *
 * 定义 Rageshake 功能中各个 Presenter 的依赖绑定。
 */
@ContributesTo(AppScope::class)
@BindingContainer
interface RageshakeModule {
    /**
     * 绑定摇一摇偏好设置 Presenter
     *
     * @param presenter 摇一摇偏好设置 Presenter 实现
     * @return Presenter<RageshakePreferencesState> 泛型 Presenter
     */
    @Binds
    fun bindRageshakePreferencesPresenter(presenter: RageshakePreferencesPresenter): Presenter<RageshakePreferencesState>

    /**
     * 绑定摇一摇检测 Presenter
     *
     * @param presenter 摇一摇检测 Presenter 实现
     * @return Presenter<RageshakeDetectionState> 泛型 Presenter
     */
    @Binds
    fun bindRageshakeDetectionPresenter(presenter: RageshakeDetectionPresenter): Presenter<RageshakeDetectionState>

    /**
     * 绑定崩溃检测 Presenter
     *
     * @param presenter 崩溃检测 Presenter 实现
     * @return Presenter<CrashDetectionState> 泛型 Presenter
     */
    @Binds
    fun bindCrashDetectionPresenter(presenter: CrashDetectionPresenter): Presenter<CrashDetectionState>
}
