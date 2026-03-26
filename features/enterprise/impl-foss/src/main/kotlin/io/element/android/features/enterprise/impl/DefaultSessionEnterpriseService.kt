/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.enterprise.impl

import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.enterprise.api.SessionEnterpriseService
import io.element.android.libraries.di.SessionScope

/**
 * SessionEnterpriseService 的默认实现
 *
 * 提供会话级企业服务的默认实现。
 *
 * @see SessionEnterpriseService 会话级企业服务接口
 */
@ContributesBinding(SessionScope::class)
class DefaultSessionEnterpriseService : SessionEnterpriseService {
    /**
     * 初始化会话级企业服务
     *
     * 默认实现为空操作
     */
    override suspend fun init() = Unit

    /**
     * 检查 Element Call 是否可用
     *
     * 默认实现始终返回 true
     *
     * @return Element Call 是否可用，恒为 true
     */
    override suspend fun isElementCallAvailable(): Boolean = true
}
