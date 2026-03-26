/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.tasks

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.matrix.api.MatrixClient
import timber.log.Timber

/**
 * 清理数据库存储用例接口
 */
fun interface VacuumStoresUseCase {
    /**
     * 执行数据库清理操作
     */
    suspend operator fun invoke()
}

/**
 * 默认清理数据库存储用例实现
 *
 * 负责对 Matrix 数据库执行 VACUUM 操作，以回收未使用的空间并优化数据库性能。
 *
 * @property matrixClient Matrix 客户端
 */
@ContributesBinding(AppScope::class)
class DefaultVacuumStoresUseCase(
    private val matrixClient: MatrixClient,
) : VacuumStoresUseCase {
    override suspend fun invoke() {
        matrixClient.performDatabaseVacuum()
            .onFailure { Timber.e(it, "Failed to vacuum stores") }
    }
}
