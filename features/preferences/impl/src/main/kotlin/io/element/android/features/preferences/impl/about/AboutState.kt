/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.about

import kotlinx.collections.immutable.ImmutableList

/**
 * 关于页面状态数据类
 *
 * @property elementLegals Element 法律信息列表（包含版权、使用政策、隐私政策等链接）
 */
data class AboutState(
    val elementLegals: ImmutableList<ElementLegal>,
)
