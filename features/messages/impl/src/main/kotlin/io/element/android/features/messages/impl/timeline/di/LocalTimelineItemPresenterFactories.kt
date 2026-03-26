/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.di

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 本地时间线项Presenter工厂组合局部变量
 *
 * 这是一个Compose组合局部变量，用于在Compose上下文中提供TimelineItemPresenterFactories实例。
 * 允许在运行时动态设置时间线项的Presenter工厂集合。
 *
 * @see TimelineItemPresenterFactories 时间线项Presenter工厂集合
 */
val LocalTimelineItemPresenterFactories = staticCompositionLocalOf {
    TimelineItemPresenterFactories(emptyMap())
}
