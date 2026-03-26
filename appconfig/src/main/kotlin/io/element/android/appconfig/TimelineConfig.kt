/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

import io.element.android.libraries.matrix.api.room.StateEventType

/**
 * 时间线配置 (Timeline Configuration)
 *
 * 此对象包含消息时间线（聊天记录）相关的配置项。
 * 控制消息显示数量上限、过滤不显示的事件类型等。
 */
object TimelineConfig {
    /** 在消息气泡上最多显示的已读回执用户头像数量。超过此数量的用户将以"+N"形式显示 */
    const val MAX_READ_RECEIPT_TO_DISPLAY = 3

    /**
     * 需要从时间线中过滤掉的事件类型列表。这些事件将不会在聊天界面中显示，
     * 因为它们通常是技术性的内部状态事件，对普通用户没有实际意义。
     *
     * 过滤的事件类型包括：
     * - CallMember: 通话成员状态变化
     * - RoomAliases: 房间别名更改
     * - RoomCanonicalAlias: 房间主要别名设置
     * - RoomGuestAccess: 访客访问权限
     * - RoomHistoryVisibility: 历史消息可见性
     * - RoomJoinRules: 加入规则
     * - RoomPowerLevels: 权限级别
     * - RoomServerAcl: 服务器访问控制列表
     * - RoomTombstone: 房间迁移通知
     * - SpaceChild/SpaceParent: 空间层级关系
     * - PolicyRuleRoom/PolicyRuleServer/PolicyRuleUser: 策略规则
     */
    val excludedEvents = listOf(
        StateEventType.CallMember,
        StateEventType.RoomAliases,
        StateEventType.RoomCanonicalAlias,
        StateEventType.RoomGuestAccess,
        StateEventType.RoomHistoryVisibility,
        StateEventType.RoomJoinRules,
        StateEventType.RoomPowerLevels,
        StateEventType.RoomServerAcl,
        StateEventType.RoomTombstone,
        StateEventType.SpaceChild,
        StateEventType.SpaceParent,
        StateEventType.PolicyRuleRoom,
        StateEventType.PolicyRuleServer,
        StateEventType.PolicyRuleUser,
    )
}
