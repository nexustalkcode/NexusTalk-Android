/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.api

import io.element.android.libraries.matrix.api.room.StateEventType
import io.element.android.libraries.matrix.api.room.join.JoinRule
import io.element.android.libraries.matrix.api.room.powerlevels.RoomPermissions

/**
 * 安全与隐私权限数据类
 *
 * 用于描述用户在房间中可以修改的安全与隐私相关权限。
 *
 * @property canChangeRoomAccess 是否可以修改房间访问权限（加入规则）
 * @property canChangeHistoryVisibility 是否可以修改历史可见性
 * @property canChangeEncryption 是否可以修改加密设置
 * @property canChangeRoomVisibility 是否可以修改房间在目录中的可见性
 */
data class SecurityAndPrivacyPermissions(
    /** 是否可以修改房间访问权限（加入规则） */
    val canChangeRoomAccess: Boolean,
    /** 是否可以修改历史可见性 */
    val canChangeHistoryVisibility: Boolean,
    /** 是否可以修改加密设置 */
    val canChangeEncryption: Boolean,
    /** 是否可以修改房间在目录中的可见性 */
    val canChangeRoomVisibility: Boolean,
) {
    /**
     * 检查是否具有任何安全与隐私相关的修改权限
     *
     * @param isSpace 是否为空间
     * @param joinRule 当前的加入规则
     * @return Boolean 是否具有任何修改权限
     */
    fun hasAny(isSpace: Boolean, joinRule: JoinRule?): Boolean {
        val canChangeRoomVisibility = when (joinRule) {
            is JoinRule.Public,
            is JoinRule.Knock,
            is JoinRule.KnockRestricted -> canChangeRoomVisibility
            else -> false
        }
        return if (isSpace) {
            canChangeRoomAccess || canChangeRoomVisibility
        } else {
            canChangeRoomAccess || canChangeRoomVisibility || canChangeHistoryVisibility || canChangeEncryption
        }
    }

    companion object {
        /**
         * 默认的权限配置，所有权限均为 false（用户没有修改权限）
         */
        val DEFAULT = SecurityAndPrivacyPermissions(
            canChangeRoomAccess = false,
            canChangeHistoryVisibility = false,
            canChangeEncryption = false,
            canChangeRoomVisibility = false,
        )
    }
}

/**
 * 将房间权限（RoomPermissions）转换为安全与隐私权限（SecurityAndPrivacyPermissions）
 *
 * 根据用户在各状态事件类型上的发送权限，判断其是否可以修改对应的安全与隐私设置。
 *
 * @return SecurityAndPrivacyPermissions 包含各项权限的实例
 */
fun RoomPermissions.securityAndPrivacyPermissions(): SecurityAndPrivacyPermissions {
    return SecurityAndPrivacyPermissions(
        canChangeRoomAccess = canOwnUserSendState(StateEventType.RoomJoinRules),
        canChangeHistoryVisibility = canOwnUserSendState(StateEventType.RoomHistoryVisibility),
        canChangeEncryption = canOwnUserSendState(StateEventType.RoomEncryption),
        canChangeRoomVisibility = canOwnUserSendState(StateEventType.RoomCanonicalAlias),
    )
}
