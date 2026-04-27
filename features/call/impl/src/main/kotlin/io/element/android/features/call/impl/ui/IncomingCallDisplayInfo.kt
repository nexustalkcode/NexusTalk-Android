/*
 * Copyright (c) 2026 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import io.element.android.features.call.impl.notifications.CallNotificationData

/**
 * 生成来电条目的主标题。
 *
 * 私聊优先显示发起人名称；群房间则优先显示房间名，
 * 以避免 room call 被误认为是单人来电。
 */
fun CallNotificationData.incomingCallTitle(): String {
    // 私聊房间展示发起人；群房间展示房间，避免 room call 被误认为个人来电。
    return if (isDm) {
        incomingCallSenderName()
    } else {
        roomName.takeUnless { it.isNullOrBlank() } ?: incomingCallSenderName()
    }
}

/**
 * 生成用于排障和区分来电来源的副标题。
 *
 * 私聊使用发起人的 Matrix ID，群房间使用房间 ID。
 */
fun CallNotificationData.incomingCallSubtitle(): String {
    // Overlay 副标题用于排查和区分来电来源：私聊展示发起人的 Matrix ID，群房间展示房间 ID。
    return if (isDm) senderId.value else roomId.value
}

/**
 * 生成 Avatar 组件使用的稳定标识。
 *
 * 私聊以发起人为主键，群房间以房间为主键，保证头像缓存语义与展示对象一致。
 */
fun CallNotificationData.incomingCallAvatarId(): String {
    return if (isDm) senderId.value else roomId.value
}

/**
 * 生成 Avatar 组件展示的人类可读名称。
 */
fun CallNotificationData.incomingCallAvatarName(): String {
    return incomingCallTitle()
}

private fun CallNotificationData.incomingCallSenderName(): String {
    return senderName.takeUnless { it.isNullOrBlank() } ?: senderId.value
}
