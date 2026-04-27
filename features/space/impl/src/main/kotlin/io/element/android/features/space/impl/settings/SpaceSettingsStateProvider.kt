/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.space.impl.settings

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId

/**
 * 为 Space 设置页预览提供样例状态。
 */
open class SpaceSettingsStateProvider : PreviewParameterProvider<SpaceSettingsState> {
    override val values: Sequence<SpaceSettingsState>
        get() = sequenceOf(
            aSpaceSettingsState(),
            aSpaceSettingsState(alias = null),
            aSpaceSettingsState(showSecurityAndPrivacy = true),
            aSpaceSettingsState(showRolesAndPermissions = true),
        )
}

/**
 * 构造一份 Space 设置页样例状态。
 */
fun aSpaceSettingsState(
    roomId: RoomId = RoomId("!aRoomId:element.io"),
    name: String = "Space name",
    alias: RoomAlias? = RoomAlias("#spacename:element.io"),
    avatarUrl: String? = null,
    memberCount: Long = 100,
    showRolesAndPermissions: Boolean = false,
    showSecurityAndPrivacy: Boolean = false,
    canEditDetails: Boolean = false,
    eventSink: (SpaceSettingsEvents) -> Unit = {},
) = SpaceSettingsState(
    roomId = roomId,
    name = name,
    canonicalAlias = alias,
    avatarUrl = avatarUrl,
    memberCount = memberCount,
    canEditDetails = canEditDetails,
    showRolesAndPermissions = showRolesAndPermissions,
    showSecurityAndPrivacy = showSecurityAndPrivacy,
    eventSink = eventSink,
)
