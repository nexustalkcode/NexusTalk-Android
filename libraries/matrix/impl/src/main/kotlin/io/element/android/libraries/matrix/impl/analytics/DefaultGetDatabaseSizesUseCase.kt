/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.analytics

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.matrix.api.MatrixClientProvider
import io.element.android.libraries.matrix.api.analytics.GetDatabaseSizesUseCase
import io.element.android.libraries.matrix.api.analytics.SdkStoreSizes
import io.element.android.libraries.matrix.api.core.SessionId

/**
 * 获取数据库大小的默认实现用例
 *
 * 该类是 [GetDatabaseSizesUseCase] 接口的默认实现，负责获取指定会话（session）
 * 的 Matrix SDK 本地数据库存储大小。
 *
 * 主要用途：
 * - 分析和监控应用的存储使用情况
 * - 帮助用户了解每个账户占用的本地存储空间
 * - 支持存储空间管理和清理功能
 *
 * 使用 @ContributesBinding 注解将此类绑定到 AppScope，使得整个应用可以通过
 * [GetDatabaseSizesUseCase] 接口使用此功能。
 *
 * @property clientProvider Matrix 客户端提供者的延迟加载对象，
 *                          用于按需获取 Matrix 客户端实例
 *
 * @see GetDatabaseSizesUseCase 获取数据库大小的用例接口
 * @see SdkStoreSizes SDK 存储大小数据模型
 * @see MatrixClientProvider Matrix 客户端提供者
 */
@ContributesBinding(AppScope::class)
class DefaultGetDatabaseSizesUseCase(
    private val clientProvider: Lazy<MatrixClientProvider>,
) : GetDatabaseSizesUseCase {

    /**
     * 获取指定会话的数据库存储大小
     *
     * 这是一个挂起函数，需要在协程上下文中调用。
     * 方法会：
     * 1. 根据会话 ID 获取对应的 Matrix 客户端
     * 2. 调用客户端的 getDatabaseSizes() 方法获取存储大小信息
     *
     * @param sessionId 用户会话的唯一标识符
     * @return Result<SdkStoreSizes> 成功时返回包含数据库大小的 SdkStoreSizes 对象，
     *         失败时返回包含错误信息的 Result
     *
     * @throws IllegalArgumentException 当找不到对应会话的 Matrix 客户端时
     */
    override suspend fun invoke(sessionId: SessionId): Result<SdkStoreSizes> {
        // 根据会话 ID 获取 Matrix 客户端
        val client = clientProvider.value.getOrNull(sessionId)
            ?: return Result.failure(IllegalArgumentException("No MatrixClient for session $sessionId"))

        // 调用客户端方法获取数据库大小
        return client.getDatabaseSizes()
    }
}
