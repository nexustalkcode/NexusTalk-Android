/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.element.android.features.home.impl.R
import io.element.android.libraries.designsystem.components.Announcement
import io.element.android.libraries.designsystem.components.AnnouncementType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsEvents
import io.element.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import io.element.android.libraries.fullscreenintent.api.aFullScreenIntentPermissionsState
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 全屏intent权限横幅
 *
 * 渲染全屏intent权限请求横幅，当应用需要全屏intent权限来显示来电通知时显示。
 * 用户可以选择继续授权或关闭横幅。
 *
 * @param state 全屏intent权限状态
 * @param modifier 修饰符
 */
@Composable
fun FullScreenIntentPermissionBanner(
    state: FullScreenIntentPermissionsState,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Announcement(
        title = stringResource(R.string.full_screen_intent_banner_title),
        description = stringResource(R.string.full_screen_intent_banner_message),
        type = AnnouncementType.Actionable(
            actionText = stringResource(CommonStrings.action_continue),
            onDismissClick = onDismissClick,
            onActionClick = { state.eventSink(FullScreenIntentPermissionsEvents.OpenSettings) },
        ),
        modifier = modifier.roomListBannerPadding(),
    )
}

@PreviewsDayNight
@Composable
internal fun FullScreenIntentPermissionBannerPreview() {
    ElementPreview {
        FullScreenIntentPermissionBanner(
            state = aFullScreenIntentPermissionsState(),
            onDismissClick = {},
        )
    }
}
