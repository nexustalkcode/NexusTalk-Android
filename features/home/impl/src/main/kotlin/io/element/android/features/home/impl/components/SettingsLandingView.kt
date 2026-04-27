/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.home.impl.HomeNavigationBarItem
import io.element.android.features.home.impl.HomeState
import io.element.android.features.home.impl.HomeStateProvider
import io.element.android.features.logout.api.direct.DirectLogoutEvents
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.components.MatrixUserHeader
import io.element.android.libraries.ui.common.settings.SettingsContent

/**
 * 渲染首页设置落地页。
 */
@Composable
fun SettingsLandingView(
    state: HomeState,
    lazyListState: LazyListState,
    onOpenUserProfile: (MatrixUser) -> Unit,
    onOpenUserQrCode: (MatrixUser) -> Unit,
    onManageAccountClick: (String) -> Unit,
    onManageDevicesClick: (String) -> Unit,
    onLinkNewDeviceClick: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenLockScreenSettings: () -> Unit,
    onOpenAdvancedSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenBlockedUsers: () -> Unit,
    onSignOutClick: () -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentUser = state.currentUserAndNeighbors.getOrNull(state.currentUserAndNeighbors.size / 2)
        ?: state.currentUserAndNeighbors.firstOrNull()
    val headerContent: (@Composable () -> Unit)? = currentUser?.let { matrixUser ->
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
            ) {
                MatrixUserHeader(
                    matrixUser = matrixUser,
                    modifier = Modifier.clickable { onOpenUserProfile(matrixUser) },
                    onQrCodeClick = { onOpenUserQrCode(matrixUser) },
                    showChevron = true,
                    avatarSize = 60.dp,
                )
            }
        }
    }
    LazyColumn(
        state = lazyListState,
        modifier = modifier.background(ElementTheme.colors.bgSubtleSecondary),
        contentPadding = PaddingValues(bottom = 80.dp),
    ) {
        item {
            SettingsContent(
                onOpenNotificationSettings = onOpenNotificationSettings,
                onOpenLockScreenSettings = onOpenLockScreenSettings,
                onOpenAdvancedSettings = onOpenAdvancedSettings,
                onOpenAbout = onOpenAbout,
                onSignOutClick = {
                    if (state.directLogoutState.canDoDirectSignOut) {
                        state.directLogoutState.eventSink(DirectLogoutEvents.Logout(ignoreSdkError = false))
                    } else {
                        onSignOutClick()
                    }
                },
                showSecureBackup = state.showSecureBackup,
                showSecureBackupBadge = state.showSecureBackupBadge,
                onSetUpRecoveryClick = onSetUpRecoveryClick,
                onManageAccountClick = state.accountManagementUrl?.let { url -> { onManageAccountClick(url) } },
                onManageDevicesClick = state.devicesManagementUrl?.let { url -> { onManageDevicesClick(url) } },
                onLinkNewDeviceClick = onLinkNewDeviceClick.takeIf { state.showLinkNewDevice },
                onOpenBlockedUsers = onOpenBlockedUsers.takeIf { state.showBlockedUsersItem },
                headerContent = headerContent,
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun SettingsLandingViewPreview(@PreviewParameter(HomeStateProvider::class) state: HomeState) = ElementPreview {
    SettingsLandingView(
        state = state.copy(currentHomeNavigationBarItem = HomeNavigationBarItem.Settings),
        lazyListState = rememberLazyListState(),
        onOpenUserProfile = {},
        onOpenUserQrCode = {},
        onManageAccountClick = {},
        onManageDevicesClick = {},
        onLinkNewDeviceClick = {},
        onOpenNotificationSettings = {},
        onOpenLockScreenSettings = {},
        onOpenAdvancedSettings = {},
        onOpenAbout = {},
        onOpenBlockedUsers = {},
        onSignOutClick = {},
        onSetUpRecoveryClick = {},
    )
}
