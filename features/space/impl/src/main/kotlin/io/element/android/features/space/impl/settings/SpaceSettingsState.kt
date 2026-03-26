/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.space.impl.settings

import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 空间设置状态数据类
 *
 * @property roomId 房间 ID
 * @property name 空间名称
 * @property canonicalAlias 标准别名
 * @property avatarUrl 头像 URL
 * @property memberCount 成员数量
 * @property canEditDetails 是否可以编辑详情
 * @property showRolesAndPermissions 是否显示角色和权限
 * @property showSecurityAndPrivacy 是否显示安全和隐私
 * @property eventSink 事件处理函数
 */
data class SpaceSettingsState(
    val roomId: RoomId,
    val name: String,
    val canonicalAlias: RoomAlias?,
    val avatarUrl: String?,
    val memberCount: Long,
    val canEditDetails: Boolean,
    val showRolesAndPermissions: Boolean,
    val showSecurityAndPrivacy: Boolean,
    val eventSink: (SpaceSettingsEvents) -> Unit
)
