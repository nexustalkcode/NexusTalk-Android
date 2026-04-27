/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.services.analytics.noop.watchers

import dev.zacsweers.metro.Inject
import io.element.android.services.analytics.api.watchers.AnalyticsRoomListStateWatcher

class NoopAnalyticsRoomListStateWatcher @Inject constructor() : AnalyticsRoomListStateWatcher {
    override fun start() {}
    override fun stop() {}
}
