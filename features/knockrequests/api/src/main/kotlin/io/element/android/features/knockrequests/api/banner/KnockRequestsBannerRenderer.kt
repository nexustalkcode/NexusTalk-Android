/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.api.banner

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 渲染房间敲门请求横幅的入口接口。
 *
 * 由上层页面按需注入，实现侧只负责把当前房间的敲门请求状态展示成可交互的 banner。
 */
interface KnockRequestsBannerRenderer {
    /**
     * 渲染敲门请求横幅。
     *
     * @param modifier 应用于横幅根节点的修饰符。
     * @param onViewRequestsClick 点击“查看请求”后的回调。
     */
    @Composable
    fun View(modifier: Modifier, onViewRequestsClick: () -> Unit)
}
