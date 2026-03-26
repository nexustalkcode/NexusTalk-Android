/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import io.element.android.features.call.impl.receivers.DeclineCallBroadcastReceiver
import io.element.android.features.call.impl.ui.ElementCallActivity
import io.element.android.features.call.impl.ui.IncomingCallActivity

/**
 * 通话模块依赖注入绑定接口
 *
 * 用于依赖注入的绑定接口，提供通话相关组件的注入方法。
 * 该接口将依赖注入到各个 Activity 和 BroadcastReceiver 中。
 *
 * @see ElementCallActivity 通话主界面 Activity
 * @see IncomingCallActivity 来电通知 Activity
 * @see DeclineCallBroadcastReceiver 拒绝通话广播接收器
 */
@ContributesTo(AppScope::class)
interface CallBindings {
    /**
     * 注入通话主界面 Activity 的依赖
     *
     * @param callActivity 通话主界面 Activity 实例
     */
    fun inject(callActivity: ElementCallActivity)

    /**
     * 注入来电通知 Activity 的依赖
     *
     * @param callActivity 来电通知 Activity 实例
     */
    fun inject(callActivity: IncomingCallActivity)

    /**
     * 注入拒绝通话广播接收器的依赖
     *
     * @param declineCallBroadcastReceiver 拒绝通话广播接收器实例
     */
    fun inject(declineCallBroadcastReceiver: DeclineCallBroadcastReceiver)
}
