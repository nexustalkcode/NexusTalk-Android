/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appnav.room

import android.os.Parcelable
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.room.JoinedRoom
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * 房间流程的初始导航目标。
 */
sealed interface RoomNavigationTarget : Parcelable {
    /**
     * 房间主页目标，可携带聚焦事件或已加载房间实例。
     */
    @Parcelize
    data class Root(
        val eventId: EventId? = null,
        @IgnoredOnParcel val joinedRoom: JoinedRoom? = null,
    ) : RoomNavigationTarget

    /** 房间详情页目标。 */
    @Parcelize
    data object Details : RoomNavigationTarget

    /** 房间通知设置页目标。 */
    @Parcelize
    data object NotificationSettings : RoomNavigationTarget
}
