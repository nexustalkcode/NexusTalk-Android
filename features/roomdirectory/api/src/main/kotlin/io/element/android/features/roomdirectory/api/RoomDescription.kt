/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdirectory.api

import android.os.Parcelable
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * 房间描述数据类
 *
 * 表示一个房间的详细信息，包括房间ID、名称、别名、主题、头像等信息。
 * 用于在房间目录中展示和传递房间数据。
 *
 * @property roomId 房间的唯一标识符
 * @property name 房间名称（可选）
 * @property alias 房间别名（可选）
 * @property topic 房间主题描述（可选）
 * @property avatarUrl 房间头像URL（可选）
 * @property joinRule 加入规则，定义用户如何加入该房间
 * @property numberOfMembers 房间成员数量
 */
@Parcelize
data class RoomDescription(
    val roomId: RoomId,
    val name: String?,
    val alias: RoomAlias?,
    val topic: String?,
    val avatarUrl: String?,
    val joinRule: JoinRule,
    val numberOfMembers: Long,
) : Parcelable {
    /**
     * 加入规则枚举
     *
     * 定义了用户加入房间的方式和权限级别。
     */
    enum class JoinRule {
        PUBLIC,
        KNOCK,
        RESTRICTED,
        KNOCK_RESTRICTED,
        INVITE,
        UNKNOWN
    }

    /** 计算得出的显示名称，优先使用名称，其次别名，最后使用房间ID */
    @IgnoredOnParcel
    val computedName = name ?: alias?.value ?: roomId.value

    /** 计算得出的描述文本，用于展示房间的简短描述 */
    @IgnoredOnParcel
    val computedDescription: String
        get() {
            return when {
                topic != null -> topic
                name != null && alias != null -> alias.value
                name == null && alias == null -> ""
                else -> roomId.value
            }
        }

    /** 判断是否可以加入或敲门该房间（仅公共房间和需要敲门的房间可加入） */
    @IgnoredOnParcel
    val canJoinOrKnock = joinRule == JoinRule.PUBLIC || joinRule == JoinRule.KNOCK

    /**
     * 创建头像数据
     *
     * @param size 头像大小
     * @return AvatarData 用于显示房间头像的数据对象
     */
    fun avatarData(size: AvatarSize) = AvatarData(
        id = roomId.value,
        name = name,
        url = avatarUrl,
        size = size,
    )
}
