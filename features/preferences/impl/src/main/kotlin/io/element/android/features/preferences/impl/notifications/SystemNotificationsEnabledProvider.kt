/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.notifications

import androidx.core.app.NotificationManagerCompat
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

/**
 * 系统通知启用状态提供者接口
 */
interface SystemNotificationsEnabledProvider {
    /**
     * 检查系统通知是否启用
     *
     * @return 如果系统通知已启用则返回 true
     */
    fun notificationsEnabled(): Boolean
}

/**
 * 默认系统通知启用状态提供者实现
 *
 * 使用系统 NotificationManager 来检查通知是否已启用。
 *
 * @property notificationManager 系统通知管理器
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultSystemNotificationsEnabledProvider(
    private val notificationManager: NotificationManagerCompat,
) : SystemNotificationsEnabledProvider {
    override fun notificationsEnabled(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }
}
