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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.HomeNavigationBarItem
import io.element.android.features.home.impl.HomeState
import io.element.android.features.home.impl.HomeStateProvider
import io.element.android.features.logout.api.direct.DirectLogoutEvents
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.atomic.atoms.RedIndicatorAtom
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.ListItemStyle
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.components.MatrixUserHeader
import io.element.android.libraries.ui.strings.CommonStrings

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
    LazyColumn(
        state = lazyListState,
        modifier = modifier.background(ElementTheme.colors.bgSubtleSecondary),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                currentUser?.let { matrixUser ->
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
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
                ) {
                    ListItem(
                        headlineContent = { Text(stringResource(id = CommonStrings.screen_notification_settings_title)) },
                        leadingContent = ListItemContent.Icon(
                            IconSource.Vector(CompoundIcons.Notifications()),
                            tintColor = ElementTheme.colors.iconPrimary,
                        ),
                        trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                        onClick = onOpenNotificationSettings,
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                    ListItem(
                        headlineContent = { Text(stringResource(id = CommonStrings.common_screen_lock)) },
                        leadingContent = ListItemContent.Icon(
                            IconSource.Vector(CompoundIcons.Lock()),
                            tintColor = ElementTheme.colors.iconPrimary,
                        ),
                        trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                        onClick = onOpenLockScreenSettings,
                    )
                    if (state.showSecureBackup) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                        ListItem(
                            headlineContent = { Text(stringResource(id = CommonStrings.common_encryption)) },
                            leadingContent = ListItemContent.Icon(
                                IconSource.Vector(CompoundIcons.Key()),
                                tintColor = ElementTheme.colors.iconPrimary,
                            ),
                            trailingContent = ListItemContent.Custom { _ ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    if (state.showSecureBackupBadge) {
                                        Box(
                                            modifier = Modifier.size(24.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            RedIndicatorAtom()
                                        }
                                    }
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = CompoundIcons.ChevronRight(),
                                        contentDescription = null,
                                        tint = LocalContentColor.current,
                                    )
                                }
                            },
                            onClick = onSetUpRecoveryClick,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (state.accountManagementUrl != null || state.devicesManagementUrl != null || state.showLinkNewDevice || state.showBlockedUsersItem) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
                    ) {
                        state.accountManagementUrl?.let { url ->
                            ListItem(
                                headlineContent = { Text(stringResource(id = CommonStrings.action_manage_account)) },
                                leadingContent = ListItemContent.Icon(
                                    IconSource.Vector(CompoundIcons.UserProfile()),
                                    tintColor = ElementTheme.colors.iconPrimary,
                                ),
                                trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                                onClick = { onManageAccountClick(url) },
                            )
                        }
                        if (state.accountManagementUrl != null && (state.devicesManagementUrl != null || state.showLinkNewDevice || state.showBlockedUsersItem)) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                        }
                        state.devicesManagementUrl?.let { url ->
                            ListItem(
                                headlineContent = { Text(stringResource(id = CommonStrings.action_manage_devices)) },
                                leadingContent = ListItemContent.Icon(
                                    IconSource.Vector(CompoundIcons.Devices()),
                                    tintColor = ElementTheme.colors.iconPrimary,
                                ),
                                trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                                onClick = { onManageDevicesClick(url) },
                            )
                        }
                        if (state.devicesManagementUrl != null && (state.showLinkNewDevice || state.showBlockedUsersItem)) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                        }
                        if (state.showLinkNewDevice) {
                            ListItem(
                                headlineContent = { Text(stringResource(id = CommonStrings.common_link_new_device)) },
                                leadingContent = ListItemContent.Icon(
                                    IconSource.Vector(CompoundIcons.Devices()),
                                    tintColor = ElementTheme.colors.iconPrimary,
                                ),
                                trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                                onClick = onLinkNewDeviceClick,
                            )
                        }
                        if (state.showLinkNewDevice && state.showBlockedUsersItem) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                        }
                        if (state.showBlockedUsersItem) {
                            ListItem(
                                headlineContent = { Text(stringResource(id = CommonStrings.common_blocked_users)) },
                                leadingContent = ListItemContent.Icon(
                                    IconSource.Vector(CompoundIcons.Block()),
                                    tintColor = ElementTheme.colors.iconPrimary,
                                ),
                                trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                                onClick = onOpenBlockedUsers,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
                ) {
                    ListItem(
                        headlineContent = { Text(stringResource(id = CommonStrings.common_advanced_settings)) },
                        leadingContent = ListItemContent.Icon(
                            IconSource.Vector(CompoundIcons.Settings()),
                            tintColor = ElementTheme.colors.iconPrimary,
                        ),
                        trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                        onClick = onOpenAdvancedSettings,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
                ) {
                    ListItem(
                        headlineContent = { Text(stringResource(id = CommonStrings.common_about)) },
                        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Info())),
                        trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                        onClick = onOpenAbout,
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
                ) {
                    ListItem(
                        headlineContent = { Text(stringResource(id = CommonStrings.action_signout)) },
                        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.SignOut())),
                        style = ListItemStyle.Destructive,
                        onClick = {
                            if (state.directLogoutState.canDoDirectSignOut) {
                                state.directLogoutState.eventSink(DirectLogoutEvents.Logout(ignoreSdkError = false))
                            } else {
                                onSignOutClick()
                            }
                        },
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
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
