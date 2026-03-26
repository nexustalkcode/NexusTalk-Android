/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.lockscreen.impl.unlock.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import io.element.android.features.lockscreen.impl.unlock.activity.PinUnlockActivity

/**
 * PIN 解锁依赖注入绑定接口
 *
 * 提供 PinUnlockActivity 的依赖注入绑定。
 */
@ContributesTo(AppScope::class)
interface PinUnlockBindings {
    /**
     * 注入 Activity 依赖
     *
     * @param activity 要注入的 Activity
     */
    fun inject(activity: PinUnlockActivity)
}
