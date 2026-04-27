/*
 * Copyright (c) 2026 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import io.element.android.features.call.impl.notifications.CallNotificationData
import io.element.android.features.call.impl.notifications.hasSameRingingIdentityAs
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import kotlinx.collections.immutable.toImmutableList

/**
 * 把原始来电通知集合映射为 overlay 可直接消费的展示状态。
 *
 * @param excludedCall 需要从 overlay 中排除的来电，通常是当前全屏页已展示的那一路。
 * @param onAnswerClick 单条来电点击接听后的回调。
 * @param onDeclineClick 单条来电点击挂断后的回调。
 */
internal fun Iterable<CallNotificationData>.toIncomingCallOverlayState(
    excludedCall: CallNotificationData? = null,
    onAnswerClick: (CallNotificationData) -> Unit,
    onDeclineClick: (CallNotificationData) -> Unit,
): IncomingCallOverlayState {
    return IncomingCallOverlayState(
        calls = filterNot { notificationData ->
            // 全屏来电页已经展示当前这一路来电，overlay 只保留其它并发来电，避免同一条来电信息重复出现。
            excludedCall != null && notificationData.hasSameRingingIdentityAs(excludedCall)
        }.map { notificationData ->
            notificationData.toIncomingCallOverlayCall(
                onAnswerClick = onAnswerClick,
                onDeclineClick = onDeclineClick,
            )
        }.toImmutableList(),
    )
}

private fun CallNotificationData.toIncomingCallOverlayCall(
    onAnswerClick: (CallNotificationData) -> Unit,
    onDeclineClick: (CallNotificationData) -> Unit,
): IncomingCallOverlayCall {
    return IncomingCallOverlayCall(
        id = eventId.value,
        title = incomingCallTitle(),
        subtitle = incomingCallSubtitle(),
        avatarData = AvatarData(
            id = incomingCallAvatarId(),
            name = incomingCallAvatarName(),
            url = avatarUrl,
            size = AvatarSize.RoomDetailsHeader,
        ),
        avatarType = if (isDm) AvatarType.User else AvatarType.Room(),
        onAnswerClick = { onAnswerClick(this) },
        onDeclineClick = { onDeclineClick(this) },
    )
}
