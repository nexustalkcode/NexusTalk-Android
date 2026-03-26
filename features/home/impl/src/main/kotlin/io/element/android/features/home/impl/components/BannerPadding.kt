/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 房间列表横幅通用内边距
 *
 * 为房间列表中的横幅组件提供统一的内边距样式。
 *
 * @return 带有内边距的修饰符
 */
internal fun Modifier.roomListBannerPadding() = padding(horizontal = 16.dp, vertical = 8.dp)
