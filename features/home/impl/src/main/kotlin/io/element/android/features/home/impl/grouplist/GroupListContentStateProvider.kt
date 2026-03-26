/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.grouplist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * 社区列表内容状态提供者
 *
 * 为预览和测试提供 GroupListContentState 示例数据。
 *
 * @see GroupListContentState 社区列表内容状态
 */
open class GroupListContentStateProvider : PreviewParameterProvider<GroupListContentState> {
    /**
     * 提供预览状态序列
     */
    override val values: Sequence<GroupListContentState>
        get() = sequenceOf(
            aRoomsContentState(),
            aRoomsContentState(summaries = aRoomListRoomSummaryList()),
            aSkeletonContentState(),
            anEmptyContentState(),
        )
}
