/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.space.impl.leave

import io.element.android.libraries.matrix.api.spaces.SpaceRoom

/**
 * 离开 Space 页面中的可选房间展示模型。
 */
data class SelectableSpaceRoom(
    val spaceRoom: SpaceRoom,
    val isLastAdmin: Boolean,
    val isSelected: Boolean,
)
