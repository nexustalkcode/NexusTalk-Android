/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl.editroomaddress

import io.element.android.libraries.matrix.api.core.RoomAlias

/**
 * 获取房间别名的本地部分
 *
 * 从完整别名中提取本地名称部分（不含服务器地址）。
 * 例如：从 "#room:example.com" 中提取 "room"
 *
 * @return 房间别名的本地名称部分
 */
fun RoomAlias.addressName(): String {
    return value.drop(1).split(":").first()
}

/**
 * 检查房间别名是否匹配给定的服务器名称
 *
 * @param serverName 服务器名称（如 "example.com"）
 * @return Boolean 是否匹配
 */
fun RoomAlias.matchesServer(serverName: String): Boolean {
    return value.split(":").last() == serverName
}
