/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.di

import dev.zacsweers.metro.MapKey
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContent
import kotlin.reflect.KClass

/**
 * 时间线项事件内容键注解
 *
 * 用于将TimelineItemPresenterFactory添加到依赖注入的多绑定Map中，
 * 以TimelineItemEventContent子类作为键。
 *
 * 使用此注解可以将特定类型的Presenter工厂绑定到对应的内容类型。
 *
 * @param value TimelineItemEventContent的子类，用于标识内容类型
 *
 * @see TimelineItemPresenterFactory 时间线项Presenter工厂
 * @see TimelineItemEventContent 时间线项事件内容
 */
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class TimelineItemEventContentKey(val value: KClass<out TimelineItemEventContent>)
