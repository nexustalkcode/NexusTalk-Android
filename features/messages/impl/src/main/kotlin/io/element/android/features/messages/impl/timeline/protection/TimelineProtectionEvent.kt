/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.protection

import io.element.android.libraries.matrix.api.core.EventId

/**
 * 时间线媒体保护层可能触发的事件。
 */
sealed interface TimelineProtectionEvent {
    /** 请求显示指定事件的受保护内容。 */
    data class ShowContent(val eventId: EventId?) : TimelineProtectionEvent
}
