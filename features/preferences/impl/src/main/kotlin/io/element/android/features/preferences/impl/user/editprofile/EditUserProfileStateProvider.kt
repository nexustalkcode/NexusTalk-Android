/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.user.editprofile

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.ui.media.AvatarAction
import io.element.android.libraries.permissions.api.PermissionsState
import io.element.android.libraries.permissions.api.aPermissionsState
import kotlinx.collections.immutable.toImmutableList

/**
 * 为编辑用户资料页面预览提供样例状态。
 */
open class EditUserProfileStateProvider : PreviewParameterProvider<EditUserProfileState> {
    override val values: Sequence<EditUserProfileState>
        get() = sequenceOf(
            aEditUserProfileState(),
            aEditUserProfileState(userAvatarUrl = "example://uri"),
            aEditUserProfileState(saveAction = AsyncAction.ConfirmingCancellation),
        )
}

/**
 * 构造一份编辑用户资料页面样例状态。
 */
fun aEditUserProfileState(
    userId: UserId = UserId("@john.doe:matrix.org"),
    displayName: String = "John Doe",
    userAvatarUrl: String? = null,
    avatarActions: List<AvatarAction> = emptyList(),
    saveButtonEnabled: Boolean = true,
    saveAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    cameraPermissionState: PermissionsState = aPermissionsState(showDialog = false),
    eventSink: (EditUserProfileEvent) -> Unit = {},
) = EditUserProfileState(
    userId = userId,
    displayName = displayName,
    userAvatarUrl = userAvatarUrl,
    avatarActions = avatarActions.toImmutableList(),
    saveButtonEnabled = saveButtonEnabled,
    saveAction = saveAction,
    cameraPermissionState = cameraPermissionState,
    eventSink = eventSink,
)
