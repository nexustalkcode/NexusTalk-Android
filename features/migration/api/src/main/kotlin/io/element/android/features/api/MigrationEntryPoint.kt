/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 迁移功能入口点接口
 *
 * 定义了应用数据迁移功能的入口接口，负责呈现和管理迁移流程。
 */
interface MigrationEntryPoint {
    /**
     * 呈现迁移状态
     *
     * @return MigrationState 迁移状态
     */
    @Composable
    fun present(): MigrationState

    /**
     * 渲染迁移界面
     *
     * @param state 迁移状态
     * @param modifier 修饰符
     */
    @Composable
    fun Render(
        state: MigrationState,
        modifier: Modifier,
    )
}
