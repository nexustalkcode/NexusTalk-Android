/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.messagecomposer.suggestions

import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.roomlist.RoomListService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 房间别名建议数据类
 *
 * 表示一个可以被建议给用户的房间别名信息，包含房间别名、房间ID、房间名称和房间头像URL。
 *
 * @property roomAlias 房间的规范别名
 * @property roomId 房间的唯一标识符
 * @property roomName 房间的名称（可选）
 * @property roomAvatarUrl 房间的头像URL（可选）
 */
data class RoomAliasSuggestion(
    val roomAlias: RoomAlias,
    val roomId: RoomId,
    val roomName: String?,
    val roomAvatarUrl: String?,
)

/**
 * 房间别名建议数据源接口
 *
 * 定义获取房间别名建议的方法，用于在消息编辑器中提供房间别名自动补全功能。
 *
 * @see RoomAliasSuggestion 房间别名建议数据类
 * @see Flow 数据流
 */
interface RoomAliasSuggestionsDataSource {
    /**
     * 获取所有房间别名建议
     *
     * @return 包含房间别名建议的数据流
     */
    fun getAllRoomAliasSuggestions(): Flow<List<RoomAliasSuggestion>>
}

/**
 * 默认房间别名建议数据源实现
 *
 * 从房间列表服务中获取所有房间，并过滤出具有规范别名的房间作为建议列表。
 *
 * @param roomListService 房间列表服务
 * @see RoomAliasSuggestionsDataSource 数据源接口
 */
@ContributesBinding(SessionScope::class)
class DefaultRoomAliasSuggestionsDataSource(
    private val roomListService: RoomListService,
) : RoomAliasSuggestionsDataSource {
    /**
     * 获取所有房间别名建议
     *
     * 从所有房间摘要中筛选出具有规范别名的房间，并提取相关信息作为建议返回。
     *
     * @return 房间别名建议列表的数据流
     */
    override fun getAllRoomAliasSuggestions(): Flow<List<RoomAliasSuggestion>> {
        return roomListService
            .allRooms
            .summaries
            .map { roomSummaries ->
                roomSummaries
                    .mapNotNull { roomSummary ->
                        roomSummary.info.canonicalAlias?.let { roomAlias ->
                            RoomAliasSuggestion(
                                roomAlias = roomAlias,
                                roomId = roomSummary.roomId,
                                roomName = roomSummary.info.name,
                                roomAvatarUrl = roomSummary.info.avatarUrl,
                            )
                        }
                    }
            }
    }
}
