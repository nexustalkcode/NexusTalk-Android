/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.enterprise.api

/**
 * 会话级企业服务接口
 *
 * 定义与会话相关的企业级功能接口。
 * 该接口在会话范围内提供，用于处理特定于用户会话的企业功能。
 *
 * @see DefaultSessionEnterpriseService 默认实现
 */
interface SessionEnterpriseService {
    /**
     * 检查 Element Call 是否可用
     *
     * @return Element Call 是否可用
     */
    suspend fun isElementCallAvailable(): Boolean

    /**
     * 初始化会话级企业服务
     *
     * 在会话开始时调用，用于初始化必要的资源
     */
    suspend fun init()
}
