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

/**
 * 设置恢复密钥横幅
 *
 * 渲染设置恢复密钥提示横幅，当用户尚未设置恢复密钥时显示。
 * 用户可以选择继续设置或关闭横幅。
 *
 * @param onContinueClick 继续点击事件
 * @param onDismissClick 关闭点击事件
 * @param modifier 修饰符
 */
@Composable
internal fun SetUpRecoveryKeyBanner(
    onContinueClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Announcement(
        modifier = modifier.roomListBannerPadding(),
        title = stringResource(R.string.banner_set_up_recovery_title),
        description = stringResource(R.string.banner_set_up_recovery_content),
        type = AnnouncementType.Actionable(
            actionText = stringResource(R.string.banner_set_up_recovery_submit),
            onActionClick = onContinueClick,
            onDismissClick = onDismissClick,
        ),
    )
}

@PreviewsDayNight
@Composable
internal fun SetUpRecoveryKeyBannerPreview() = ElementPreview {
    SetUpRecoveryKeyBanner(
        onContinueClick = {},
        onDismissClick = {},
    )
}
