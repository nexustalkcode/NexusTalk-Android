/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.tracing

enum class TraceLogPack(val key: String) {
    EVENT_CACHE("event_cache"),
    SEND_QUEUE("send_queue"),
    TIMELINE("timeline"),
    NOTIFICATION_CLIENT("notification_client"),
    SYNC_PROFILING("sync_profiling"),
    LATEST_EVENTS("latest_events"),
}
