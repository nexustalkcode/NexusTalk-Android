/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.root

import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.matrix.api.core.DeviceId
import io.element.android.libraries.matrix.api.user.MatrixUser
import kotlinx.collections.immutable.ImmutableList

/**
 * 首选项根页面状态数据类
 *
 * @property myUser 当前登录用户信息
 * @property version 应用版本号
 * @property deviceId 设备 ID
 * @property isMultiAccountEnabled 是否启用多账户
 * @property otherSessions 其他会话列表
 * @property showSecureBackup 是否显示安全备份选项
 * @property showSecureBackupBadge 是否显示安全备份徽章
 * @property accountManagementUrl 账户管理 URL
 * @property devicesManagementUrl 设备管理 URL
 * @property canReportBug 是否可以报告问题
 * @property showLinkNewDevice 是否显示链接新设备选项
 * @property showAnalyticsSettings 是否显示分析设置
 * @property showDeveloperSettings 是否显示开发者设置
 * @property canDeactivateAccount 是否可以停用账户
 * @property showBlockedUsersItem 是否显示被屏蔽用户选项
 * @property showLabsItem 是否显示实验室功能选项
 * @property directLogoutState 直接退出登录状态
 * @property snackbarMessage Snackbar 消息
 * @property eventSink 事件处理函数
 */
data class PreferencesRootState(
    val myUser: MatrixUser,
    val version: String,
    val deviceId: DeviceId?,
    val isMultiAccountEnabled: Boolean,
    val otherSessions: ImmutableList<MatrixUser>,
    val showSecureBackup: Boolean,
    val showSecureBackupBadge: Boolean,
    val accountManagementUrl: String?,
    val devicesManagementUrl: String?,
    val canReportBug: Boolean,
    val showLinkNewDevice: Boolean,
    val showAnalyticsSettings: Boolean,
    val showDeveloperSettings: Boolean,
    val canDeactivateAccount: Boolean,
    val showBlockedUsersItem: Boolean,
    val showLabsItem: Boolean,
    val directLogoutState: DirectLogoutState,
    val snackbarMessage: SnackbarMessage?,
    val eventSink: (PreferencesRootEvents) -> Unit,
)
