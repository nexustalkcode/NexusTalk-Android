/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.ui.common.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.atomic.atoms.RedIndicatorAtom
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.ListItemStyle
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 复用设置首页中重复的内容区块。
 *
 * 这个组件只负责渲染设置项本身，不负责页面壳层的标题栏、系统返回或宿主导航语义。
 * 调用方可以把它放在 Home 的嵌入式页面里，也可以放在独立设置流里，
 * 从而达到“共享内容、保留不同宿主行为”的目的。
 */
@Composable
fun SettingsContent(
    onOpenNotificationSettings: () -> Unit,
    onOpenLockScreenSettings: () -> Unit,
    onOpenAdvancedSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier,
    headerContent: (@Composable () -> Unit)? = null,
    showSecureBackup: Boolean = false,
    showSecureBackupBadge: Boolean = false,
    onSetUpRecoveryClick: (() -> Unit)? = null,
    onManageAccountClick: (() -> Unit)? = null,
    onManageDevicesClick: (() -> Unit)? = null,
    onLinkNewDeviceClick: (() -> Unit)? = null,
    onOpenBlockedUsers: (() -> Unit)? = null,
) {
    val showAccountSection = onManageAccountClick != null ||
        onManageDevicesClick != null ||
        onLinkNewDeviceClick != null ||
        onOpenBlockedUsers != null

    Column(modifier = modifier) {
        headerContent?.invoke()
        if (headerContent != null) {
            Spacer(modifier = Modifier.height(16.dp))
        }

        SettingsCard {
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
            if (showSecureBackup && onSetUpRecoveryClick != null) {
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
                            if (showSecureBackupBadge) {
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

        if (showAccountSection) {
            Spacer(modifier = Modifier.height(16.dp))
            SettingsCard {
                onManageAccountClick?.let { onClick ->
                    ListItem(
                        headlineContent = { Text(stringResource(id = CommonStrings.action_manage_account)) },
                        leadingContent = ListItemContent.Icon(
                            IconSource.Vector(CompoundIcons.UserProfile()),
                            tintColor = ElementTheme.colors.iconPrimary,
                        ),
                        trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                        onClick = onClick,
                    )
                }
                if (onManageAccountClick != null && (onManageDevicesClick != null || onLinkNewDeviceClick != null || onOpenBlockedUsers != null)) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                }
                onManageDevicesClick?.let { onClick ->
                    ListItem(
                        headlineContent = { Text(stringResource(id = CommonStrings.action_manage_devices)) },
                        leadingContent = ListItemContent.Icon(
                            IconSource.Vector(CompoundIcons.Devices()),
                            tintColor = ElementTheme.colors.iconPrimary,
                        ),
                        trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                        onClick = onClick,
                    )
                }
                if (onManageDevicesClick != null && (onLinkNewDeviceClick != null || onOpenBlockedUsers != null)) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                }
                onLinkNewDeviceClick?.let { onClick ->
                    ListItem(
                        headlineContent = { Text(stringResource(id = CommonStrings.common_link_new_device)) },
                        leadingContent = ListItemContent.Icon(
                            IconSource.Vector(CompoundIcons.Devices()),
                            tintColor = ElementTheme.colors.iconPrimary,
                        ),
                        trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                        onClick = onClick,
                    )
                }
                if (onLinkNewDeviceClick != null && onOpenBlockedUsers != null) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
                }
                onOpenBlockedUsers?.let { onClick ->
                    ListItem(
                        headlineContent = { Text(stringResource(id = CommonStrings.common_blocked_users)) },
                        leadingContent = ListItemContent.Icon(
                            IconSource.Vector(CompoundIcons.Block()),
                            tintColor = ElementTheme.colors.iconPrimary,
                        ),
                        trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                        onClick = onClick,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        SettingsCard {
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
        SettingsCard {
            ListItem(
                headlineContent = { Text(stringResource(id = CommonStrings.common_about)) },
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Info())),
                trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
                onClick = onOpenAbout,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        SettingsCard {
            ListItem(
                headlineContent = { Text(stringResource(id = CommonStrings.action_signout)) },
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.SignOut())),
                style = ListItemStyle.Destructive,
                onClick = onSignOutClick,
            )
        }
    }
}

@Composable
private fun SettingsCard(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(ElementTheme.colors.bgCanvasDefault, RoundedCornerShape(10.dp))
    ) {
        content()
    }
}
