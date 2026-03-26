/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.root

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.preferences.impl.R
import io.element.android.features.preferences.impl.user.UserPreferences
import io.element.android.libraries.architecture.coverage.ExcludeFromCoverage
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.components.preferences.PreferencePage
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.ElementPreviewDark
import io.element.android.libraries.designsystem.preview.ElementPreviewLight
import io.element.android.libraries.designsystem.preview.PreviewWithLargeHeight
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import androidx.compose.material3.LocalContentColor
import io.element.android.libraries.designsystem.atomic.atoms.RedIndicatorAtom
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.ListItemStyle
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarHost
import io.element.android.libraries.designsystem.utils.snackbar.rememberSnackbarHostState
import io.element.android.libraries.matrix.api.core.DeviceId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.components.MatrixUserProvider
import io.element.android.libraries.matrix.ui.components.MatrixUserRow
import io.element.android.libraries.matrix.ui.components.aMatrixUserList
import io.element.android.libraries.ui.strings.CommonStrings

@Composable
fun PreferencesRootView(
    state: PreferencesRootState,
    onBackClick: () -> Unit,
    onAddAccountClick: () -> Unit,
    onSecureBackupClick: () -> Unit,
    onManageAccountClick: (url: String) -> Unit,
    onLinkNewDeviceClick: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenRageShake: () -> Unit,
    onOpenLockScreenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenDeveloperSettings: () -> Unit,
    onOpenAdvancedSettings: () -> Unit,
    onOpenLabs: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenUserProfile: (MatrixUser) -> Unit,
    onOpenUserQrCode: (MatrixUser) -> Unit,
    onOpenBlockedUsers: () -> Unit,
    onSignOutClick: () -> Unit,
    onDeactivateClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = rememberSnackbarHostState(snackbarMessage = state.snackbarMessage)

    // Include pref from other modules
    PreferencePage(
        modifier = modifier,
        onBackClick = onBackClick,
        title = stringResource(id = CommonStrings.common_settings),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBarBackgroundColor = ElementTheme.colors.bgSubtleSecondary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = ElementTheme.colors.bgSubtleSecondary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
            ) {
                UserPreferences(
                    modifier = Modifier.clickable {
                        onOpenUserProfile(state.myUser)
                    },
                    user = state.myUser,
                    onQrCodeClick = if (state.myUser != null) {
                        { onOpenUserQrCode(state.myUser) }
                    } else null,
                    showChevron = true,
                    avatarSize = 60.dp,
                )
            }

            if (state.isMultiAccountEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                MultiAccountSection(
                    state = state,
                    onAddAccountClick = onAddAccountClick,
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
                    headlineContent = { Text(stringResource(id = R.string.screen_notification_settings_title)) },
                    leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Notifications()), tintColor = ElementTheme.colors.iconPrimary),
                    trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                    onClick = onOpenNotificationSettings,
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                ListItem(
                    headlineContent = { Text(stringResource(id = CommonStrings.common_screen_lock)) },
                    leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Lock()), tintColor = ElementTheme.colors.iconPrimary),
                    trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                    onClick = onOpenLockScreenSettings,
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                if (state.showSecureBackup) {
                    ListItem(
                        headlineContent = { Text(stringResource(id = CommonStrings.common_encryption)) },
                        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Key()), tintColor = ElementTheme.colors.iconPrimary),
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
                        onClick = onSecureBackupClick,
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.accountManagementUrl.isNullOrEmpty().not() || state.devicesManagementUrl.isNullOrEmpty() || state.showLinkNewDevice || state.showBlockedUsersItem) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
                ) {
                    state.accountManagementUrl?.let { url ->
                        ListItem(
                            headlineContent = { Text(stringResource(id = CommonStrings.action_manage_account)) },
                            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.UserProfile()), tintColor = ElementTheme.colors.iconPrimary),
                            trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                            onClick = { onManageAccountClick(url) },
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                    }

                    state.devicesManagementUrl?.let { url ->
                        ListItem(
                            headlineContent = { Text(stringResource(id = CommonStrings.action_manage_devices)) },
                            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Devices()), tintColor = ElementTheme.colors.iconPrimary),
                            trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                            onClick = { onManageAccountClick(url) },
                        )
                    }

                    if (state.showLinkNewDevice) {
                        ListItem(
                            headlineContent = { Text(stringResource(id = CommonStrings.common_link_new_device)) },
                            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Devices()), tintColor = ElementTheme.colors.iconPrimary),
                            trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                            onClick = onLinkNewDeviceClick,
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                    }

                    if (state.showBlockedUsersItem) {
                        ListItem(
                            headlineContent = { Text(stringResource(id = CommonStrings.common_blocked_users)) },
                            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Block()), tintColor = ElementTheme.colors.iconPrimary),
                            trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                            onClick = onOpenBlockedUsers,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
            ) {
                ListItem(
                    headlineContent = { Text(stringResource(id = CommonStrings.common_advanced_settings)) },
                    leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Settings()), tintColor = ElementTheme.colors.iconPrimary),
                    trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                    onClick = onOpenAdvancedSettings,
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
                    onClick = onSignOutClick,
                )
            }
            if (state.showDeveloperSettings) {
                Spacer(modifier = Modifier.height(16.dp))
                DeveloperPreferencesView(onOpenDeveloperSettings)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ColumnScope.MultiAccountSection(
    state: PreferencesRootState,
    onAddAccountClick: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
    ) {
        state.otherSessions.forEach { matrixUser ->
            MatrixUserRow(
                modifier = Modifier.clickable {
                    state.eventSink(PreferencesRootEvents.SwitchToSession(matrixUser.userId))
                },
                matrixUser = matrixUser,
                avatarSize = AvatarSize.AccountItem,
            )
            HorizontalDivider()
        }
        ListItem(
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Plus()), tintColor = ElementTheme.colors.iconPrimary),
            headlineContent = {
                Text(stringResource(CommonStrings.common_add_another_account))
            },
            trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
            onClick = onAddAccountClick,
        )
    }
}

@Composable
private fun ColumnScope.Footer(
    version: String,
    deviceId: DeviceId?,
    onClick: (() -> Unit)?,
) {
    val text = remember(version, deviceId) {
        buildString {
            append(version)
            if (deviceId != null) {
                append("\n")
                append(deviceId)
            }
        }
    }
    Text(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 16.dp)
            .clickable(enabled = onClick != null, onClick = onClick ?: {})
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
        textAlign = TextAlign.Center,
        text = text,
        style = ElementTheme.typography.fontBodySmRegular,
        color = ElementTheme.colors.textSecondary,
    )
}

@Composable
private fun DeveloperPreferencesView(onOpenDeveloperSettings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
    ) {
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.common_developer_options)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Code())),
            trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
            onClick = onOpenDeveloperSettings
        )
    }
}

@PreviewWithLargeHeight
@Composable
internal fun PreferencesRootViewLightPreview(@PreviewParameter(MatrixUserProvider::class) matrixUser: MatrixUser) =
    ElementPreviewLight { ContentToPreview(matrixUser) }

@PreviewWithLargeHeight
@Composable
internal fun PreferencesRootViewDarkPreview(@PreviewParameter(MatrixUserProvider::class) matrixUser: MatrixUser) =
    ElementPreviewDark { ContentToPreview(matrixUser) }

@ExcludeFromCoverage
@Composable
private fun ContentToPreview(matrixUser: MatrixUser) {
    PreferencesRootView(
        state = aPreferencesRootState(myUser = matrixUser),
        onBackClick = {},
        onAddAccountClick = {},
        onOpenAnalytics = {},
        onOpenRageShake = {},
        onOpenDeveloperSettings = {},
        onOpenAdvancedSettings = {},
        onOpenLabs = {},
        onOpenAbout = {},
        onSecureBackupClick = {},
        onManageAccountClick = {},
        onLinkNewDeviceClick = {},
        onOpenNotificationSettings = {},
        onOpenLockScreenSettings = {},
        onOpenUserProfile = {},
        onOpenUserQrCode = {},
        onOpenBlockedUsers = {},
        onSignOutClick = {},
        onDeactivateClick = {},
    )
}

@PreviewsDayNight
@Composable
internal fun MultiAccountSectionPreview() = ElementPreview {
    Column {
        MultiAccountSection(
            state = aPreferencesRootState(
                otherSessions = aMatrixUserList(),
            ),
            onAddAccountClick = {},
        )
    }
}
