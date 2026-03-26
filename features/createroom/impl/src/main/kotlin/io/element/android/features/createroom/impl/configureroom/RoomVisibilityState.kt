/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl.configureroom

import java.util.Optional

/**
 * 房间可见性状态密封接口
 *
 * 定义房间创建时的可见性选项，包括私密房间和公开房间。
 */
sealed interface RoomVisibilityState {
    /** 加入规则项 */
    val joinRuleItem: JoinRuleItem

    /**
     * 私有房间状态
     * @property joinRuleItem 加入规则项
     */
    data class Private(override val joinRuleItem: JoinRuleItem.Private = JoinRuleItem.Private) : RoomVisibilityState

    /**
     * 公开房间状态
     * @property roomAddress 房间地址
     * @property joinRuleItem 加入规则项
     */
    data class Public(
        val roomAddress: RoomAddress,
        override val joinRuleItem: JoinRuleItem.PublicVisibility,
    ) : RoomVisibilityState

    /**
     * 获取房间地址
     *
     * @return Optional<String> 房间地址，如果为私密房间则返回空
     */
    fun roomAddress(): Optional<String> {
        return when (this) {
            is Private -> Optional.empty()
            is Public -> Optional.of(roomAddress.value)
        }
    }
}
