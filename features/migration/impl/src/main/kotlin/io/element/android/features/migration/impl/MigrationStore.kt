/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.migration.impl

import kotlinx.coroutines.flow.Flow

/**
 * 应用迁移版本存储接口。
 */
interface MigrationStore {
    /**
     * 返回当前应用迁移版本流。
     *
     * 如果流中的版本低于当前应用支持的最新迁移版本，则说明需要继续执行迁移。
     */
    fun applicationMigrationVersion(): Flow<Int>

    /**
     * 设置当前应用迁移版本，通常在某个迁移完成后调用。
     */
    suspend fun setApplicationMigrationVersion(version: Int)
}
