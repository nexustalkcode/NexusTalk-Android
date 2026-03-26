/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.element.android.features.messages.impl.timeline.aTimelineItemEvent
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemPollContent
import io.element.android.libraries.designsystem.preview.ElementPreviewLight

/**
 * 时间线事件时间戳在下方预览
 *
 * 预览时间戳显示在内容下方的样式（用于投票等场景）。
 * 注意：此预览不需要浅色/深色变体，因为我们只关注时间戳位置。
 */
@Preview
@Composable
internal fun TimelineItemEventTimestampBelowPreview() = ElementPreviewLight {
    ATimelineItemEventRow(
        event = aTimelineItemEvent(content = aTimelineItemPollContent()),
    )
}
