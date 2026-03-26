/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.analytics

import im.vector.app.features.analytics.plan.JoinedRoom
import io.element.android.libraries.matrix.api.room.BaseRoom
import io.element.android.libraries.matrix.api.room.RoomInfo
import io.element.android.libraries.matrix.api.room.isDm
import kotlinx.coroutines.flow.first

/**
 * 将成员数量转换为分析用的房间规模枚举
 *
 * 在分析用户加入房间的行为时，需要将具体的成员数量归类到预设的规模区间中。
 * 这种分组方式有助于分析不同规模房间的使用情况和性能表现。
 *
 * @receiver 房间的成员数量
 * @return 对应的房间规模枚举值
 *
 * @see JoinedRoom.RoomSize 分析用的房间规模枚举
 */
private fun Long.toAnalyticsRoomSize(): JoinedRoom.RoomSize {
    return when (this) {
        0L,
        1L -> JoinedRoom.RoomSize.One           // 0-1 人：单人房间或空房间
        2L -> JoinedRoom.RoomSize.Two            // 2 人：双人房间
        in 3..10 -> JoinedRoom.RoomSize.ThreeToTen           // 3-10 人：小群组
        in 11..100 -> JoinedRoom.RoomSize.ElevenToOneHundred // 11-100 人：中型群组
        in 101..1000 -> JoinedRoom.RoomSize.OneHundredAndOneToAThousand // 101-1000 人：大型群组
        else -> JoinedRoom.RoomSize.MoreThanAThousand         // 1000+ 人：超大型群组
    }
}

/**
 * 将房间转换为分析用的"已加入房间"事件数据
 *
 * 这是一个挂起函数，需要在协程上下文中调用。
 * 它首先从房间的信息流中获取最新的房间信息，然后转换为分析事件。
 *
 * @param trigger 加入房间的触发原因，用于分析用户行为路径
 * @return 包含房间分析数据的 JoinedRoom 事件对象
 *
 * @see RoomInfo.toAnalyticsJoinedRoom 同步版本的转换函数
 */
suspend fun BaseRoom.toAnalyticsJoinedRoom(trigger: JoinedRoom.Trigger?): JoinedRoom {
    // 从房间信息流中获取最新的房间信息
    val roomInfo = roomInfoFlow.first()
    return roomInfo.toAnalyticsJoinedRoom(trigger)
}

/**
 * 将房间信息转换为分析用的"已加入房间"事件数据
 *
 * 该扩展函数将 RoomInfo 对象转换为 Element 分析系统使用的 JoinedRoom 事件。
 * JoinedRoom 事件记录了用户加入房间的行为，用于分析：
 * - 用户加入房间的类型（DM 还是群组）
 * - 用户加入的是否为 Space（空间）
 * - 房间的成员规模
 * - 触发用户加入房间的原因
 *
 * @param trigger 加入房间的触发原因，例如：通过链接、邀请、搜索等
 * @return 包含房间分析数据的 JoinedRoom 事件对象
 *
 * @see <a href="https://element.io/analytics">Element Analytics</a>
 */
fun RoomInfo.toAnalyticsJoinedRoom(trigger: JoinedRoom.Trigger?): JoinedRoom {
    return JoinedRoom(
        isDM = isDm,                              // 是否为私聊 (Direct Message)
        isSpace = isSpace,                        // 是否为 Space（空间）
        roomSize = joinedMembersCount.toAnalyticsRoomSize(), // 房间成员规模
        trigger = trigger                         // 加入房间的触发原因
    )
}
