/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.api

import kotlinx.coroutines.flow.StateFlow

/**
 * 当前通话服务接口
 *
 * 提供当前通话状态的访问接口，用于查询本地当前的通话状态。
 * 该服务通过 StateFlow 提供通话状态的响应式更新。
 *
 * @see CurrentCall 当前通话状态
 */
interface CurrentCallService {
    /**
     * 当前通话状态流
     *
     * 当活动通话发生变化时，此值会自动更新。
     * 此值反映通话的本地状态。如果用户在另一个会话中接听通话，此值不会更新。
     *
     * @return StateFlow<CurrentCall> 当前通话状态的响应式数据流
     */
    val currentCall: StateFlow<CurrentCall>
}
