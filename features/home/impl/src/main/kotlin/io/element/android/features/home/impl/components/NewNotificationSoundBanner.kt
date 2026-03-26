/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
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
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 新通知声音横幅
 *
 * 渲染新通知声音功能介绍横幅，向用户介绍新上线的通知声音功能。
 * 用户可以点击关闭横幅。
 *
 * @param onDismissClick 关闭点击事件
 * @param modifier 修饰符
 */
@Composable
internal fun NewNotificationSoundBanner(
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Announcement(
        modifier = modifier.roomListBannerPadding(),
        title = stringResource(R.string.banner_new_sound_title),
        description = stringResource(R.string.banner_new_sound_message),
        type = AnnouncementType.Actionable(
            actionText = stringResource(CommonStrings.action_ok),
            onActionClick = onDismissClick,
            onDismissClick = onDismissClick,
        ),
    )
}

@PreviewsDayNight
@Composable
internal fun NewNotificationSoundBannerPreview() = ElementPreview {
    NewNotificationSoundBanner(
        onDismissClick = {},
    )
}
