/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.element.android.features.messages.impl.timeline.di.LiveTimeline
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.timeline.Timeline

/**
 * 消息模块提供模块
 *
 * 定义消息功能模块中各种依赖的提供方法。
 * 使用 @ContributesTo 注解贡献到 RoomScope，
 * 使用 @BindingContainer 注解标记为绑定容器。
 *
 * @see RoomScope 房间作用域
 * @see JoinedRoom 已加入的房间
 * @see Timeline 时间线
 */
@ContributesTo(RoomScope::class)
@BindingContainer
object MessagesProvidesModule {
    /**
     * 提供实时时间线
     *
     * @param joinedRoom 已加入的房间
     * @return 实时时间线实例
     */
    @Provides
    @LiveTimeline
    fun provideLiveTimeline(joinedRoom: JoinedRoom): Timeline = joinedRoom.liveTimeline
}
