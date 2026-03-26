/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.utils

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.element.android.features.call.api.CurrentCall
import io.element.android.features.call.api.CurrentCallService
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 当前通话服务默认实现
 *
 * 实现 CurrentCallService 接口，提供本地当前通话状态的追踪。
 * 使用单例模式确保整个应用只有一个实例。
 *
 * @see CurrentCallService 当前通话服务接口
 * @see CurrentCall 当前通话状态
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultCurrentCallService : CurrentCallService {
    /** 当前通话状态流 */
    override val currentCall = MutableStateFlow<CurrentCall>(CurrentCall.None)

    /**
     * 通话开始回调
     *
     * 当通话开始时更新当前通话状态。
     *
     * @param call 通话状态
     */
    fun onCallStarted(call: CurrentCall) {
        currentCall.value = call
    }

    /**
     * 通话结束回调
     *
     * 当通话结束时将状态重置为无通话。
     */
    fun onCallEnded() {
        currentCall.value = CurrentCall.None
    }
}
