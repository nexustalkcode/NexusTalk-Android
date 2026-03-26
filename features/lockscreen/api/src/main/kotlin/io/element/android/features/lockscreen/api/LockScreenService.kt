/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.api

import android.os.Build
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * 锁屏服务接口
 *
 * 提供锁屏功能的核心接口，包括锁状态查询和 PIN 码设置状态管理。
 */
interface LockScreenService {
    /**
     * 应用当前的锁定状态
     *
     * 返回一个 StateFlow，包含当前应用的锁定状态（已解锁或已锁定）。
     */
    val lockState: StateFlow<LockScreenLockState>

    /**
     * 检查是否需要设置锁屏
     *
     * @return true 如果锁屏是强制的且尚未设置，false 否则
     */
    fun isSetupRequired(): Flow<Boolean>

    /**
     * 检查 PIN 码是否已设置
     *
     * @return true 如果 PIN 码已设置，false 否则
     */
    fun isPinSetup(): Flow<Boolean>
}

/**
 * 处理安全标志的扩展函数
 *
 * 如果已设置 PIN 码，确保在 Activity 上设置安全标志，防止屏幕截图。
 *
 * @param activity 要设置标志的 Activity
 */
fun LockScreenService.handleSecureFlag(activity: ComponentActivity) {
    isPinSetup()
        .onEach { isPinSetup ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activity.setRecentsScreenshotEnabled(!isPinSetup)
            } else {
                if (isPinSetup) {
                    activity.window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }
        }
        .launchIn(activity.lifecycleScope)
}
