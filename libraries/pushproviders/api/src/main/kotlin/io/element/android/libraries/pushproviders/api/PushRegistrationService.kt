/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.pushproviders.api

import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.core.UserId

/**
 * pushproviders 侧需要回调 push 服务时使用的最小注册接口。
 *
 * UnifiedPush 删除网关后只需要查询当前 provider、重新注册，以及通知“服务已解绑”三件事。
 * 单独抽这层契约可以避免 pushproviders 反向依赖整个 push 模块实现。
 */
interface PushRegistrationService {
    suspend fun getCurrentPushProvider(sessionId: SessionId): PushProvider?

    suspend fun registerWith(
        matrixClient: MatrixClient,
        pushProvider: PushProvider,
        distributor: Distributor,
    ): Result<Unit>

    suspend fun onServiceUnregistered(userId: UserId)
}
