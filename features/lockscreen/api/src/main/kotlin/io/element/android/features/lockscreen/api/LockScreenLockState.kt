/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.api

/**
 * 锁屏锁定状态密封接口
 *
 * 定义了锁屏应用的锁定状态，包括已解锁和已锁定两种状态。
 */
sealed interface LockScreenLockState {
    /** 已解锁状态，表示用户可以访问应用内容 */
    data object Unlocked : LockScreenLockState
    /** 已锁定状态，表示用户需要验证才能访问应用内容 */
    data object Locked : LockScreenLockState
}
