/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.api

import io.element.android.libraries.architecture.AsyncData

/**
 * 迁移状态数据类
 *
 * 表示应用数据迁移的状态。
 *
 * @property migrationAction 迁移操作的异步状态
 */
data class MigrationState(
    val migrationAction: AsyncData<Unit>,
)
