/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.di

import dev.zacsweers.metro.Qualifier

/**
 * 实时时间线限定符注解
 *
 * 用于标记实时（直播）时间线实例的限定符。
 * 与历史时间线（HistoricalTimeline）区分。
 *
 * @see io.element.android.libraries.matrix.api.room.JoinedRoom.liveTimeline 实时时间线
 */
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Qualifier
annotation class LiveTimeline
