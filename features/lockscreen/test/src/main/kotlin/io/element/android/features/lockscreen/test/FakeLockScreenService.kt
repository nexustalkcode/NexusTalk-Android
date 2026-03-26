/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.test

import io.element.android.features.lockscreen.api.LockScreenLockState
import io.element.android.features.lockscreen.api.LockScreenService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

/**
 * 假的锁屏服务实现
 *
 * 用于测试的假实现，可以控制锁状态和 PIN 设置状态。
 */
class FakeLockScreenService : LockScreenService {
    /** PIN 是否已设置的 MutableStateFlow */
    private var isPinSetup = MutableStateFlow(false)
    /** 锁状态的 MutableStateFlow */
    private val _lockState: MutableStateFlow<LockScreenLockState> = MutableStateFlow(LockScreenLockState.Locked)
    override val lockState: StateFlow<LockScreenLockState> = _lockState

    override fun isSetupRequired(): Flow<Boolean> {
        return isPinSetup.map { !it }
    }

    /**
     * 设置 PIN 是否已设置
     *
     * @param isPinSetup PIN 是否已设置
     */
    fun setIsPinSetup(isPinSetup: Boolean) {
        this.isPinSetup.value = isPinSetup
    }

    override fun isPinSetup(): Flow<Boolean> {
        return isPinSetup
    }

    /**
     * 设置锁状态
     *
     * @param lockState 新的锁状态
     */
    fun setLockState(lockState: LockScreenLockState) {
        _lockState.value = lockState
    }
}
