/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl.configureroom

import androidx.compose.runtime.Immutable
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.join.AllowRule
import io.element.android.libraries.matrix.api.room.join.JoinRule
import kotlinx.collections.immutable.persistentListOf

/**
 * 表示房间加入规则的选项，用于在创建房间 UI 中展示。
 *
 * 此密封接口定义了用户在创建房间时可以选择的多种加入规则类型，
 * 包括私有房间、公开房间、需要申请的公开房间等。
 *
 * @see JoinRule 底层 Matrix 协议的加入规则枚举
 * @see io.element.android.features.createroom.impl.configureroom.ConfigureRoomState 用于存储用户选择的加入规则
 */
@Immutable
sealed interface JoinRuleItem {
    /**
     * 私有房间 - 只有受邀用户才能加入。
     * 这是最严格的加入规则，适合私密对话或敏感内容的房间。
     *
     * 对应的 Matrix [JoinRule] 为 [JoinRule.Private]
     */
    data object Private : JoinRuleItem

    /**
     * 公开可见性相关的加入规则集合。
     *
     * 此密封接口包含所有需要公开可见性的加入规则选项，
     * 这些选项决定了房间在房间列表中的可见性以及用户如何加入房间。
     */
    @Immutable
    sealed interface PublicVisibility : JoinRuleItem {
        /**
         * 完全公开的房间 - 任何人都可以自由加入，无需任何审批。
         * 房间会出现在公开房间列表中，所有人都可以搜索并加入。
         *
         * 对应的 Matrix [JoinRule] 为 [JoinRule.Public]
         */
        data object Public : PublicVisibility

        /**
         * 需要敲门的公开房间 - 任何人都可以申请加入，但需要房间管理员批准。
         * 用户需要发送加入请求（敲门），管理员可以批准或拒绝。
         *
         * 对应的 Matrix [JoinRule] 为 [JoinRule.Knock]
         */
        data object AskToJoin : PublicVisibility

        /**
         * 受限的公开房间 - 只有特定空间的成员才能加入。
         *
         * @param parentSpaceId 父空间的 ID，用户的房间成员资格必须与此空间关联才能加入
         * 对应的 Matrix [JoinRule] 为 [JoinRule.Restricted]
         */
        data class Restricted(val parentSpaceId: RoomId) : PublicVisibility

        /**
         * 受限且需要敲门的公开房间 - 只有特定空间的成员可以申请加入。
         *
         * 结合了 [Restricted] 和 [AskToJoin] 的特性：
         * - 用户必须是指定空间的成员才能申请加入
         * - 申请需要房间管理员批准
         *
         * @param parentSpaceId 父空间的 ID，用户的房间成员资格必须与此空间关联才能申请加入
         * 对应的 Matrix [JoinRule] 为 [JoinRule.KnockRestricted]
         */
        data class AskToJoinRestricted(val parentSpaceId: RoomId) : PublicVisibility
    }

    /**
     * 将 [JoinRuleItem] 选项转换为 Matrix 协议层的 [JoinRule]。
     *
     * 此方法用于将 UI 层的数据模型转换为 Matrix SDK 所需的加入规则，
     * 以便在创建房间或更新房间设置时使用。
     *
     * @return 对应的 [JoinRule] 枚举值，包含必要的参数（如受限规则的空间 ID）
     * @see JoinRule Matrix 协议中的加入规则定义
     * @see AllowRule 用于定义受限规则中的房间成员资格条件
     */
    fun toJoinRule(): JoinRule = when (this) {
        Private -> JoinRule.Private
        PublicVisibility.Public -> JoinRule.Public
        PublicVisibility.AskToJoin -> JoinRule.Knock
        is PublicVisibility.Restricted -> JoinRule.Restricted(persistentListOf(AllowRule.RoomMembership(parentSpaceId)))
        is PublicVisibility.AskToJoinRestricted -> JoinRule.KnockRestricted(persistentListOf(AllowRule.RoomMembership(parentSpaceId)))
    }
}
