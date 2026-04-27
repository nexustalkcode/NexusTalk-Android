/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.migration.impl.migrations

/**
 * 单个应用迁移步骤接口。
 */
interface AppMigration {
    /** 迁移顺序号，数值越小越先执行。 */
    val order: Int
    /** 执行当前迁移。 */
    suspend fun migrate(isFreshInstall: Boolean)
}
