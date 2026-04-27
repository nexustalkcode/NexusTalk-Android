/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.test.widget

import io.element.android.libraries.matrix.api.widget.CallWidgetMode
import io.element.android.libraries.matrix.api.widget.CallWidgetSettingsProvider
import io.element.android.libraries.matrix.api.widget.MatrixWidgetSettings

class FakeCallWidgetSettingsProvider(
    private val provideFn: (String, String, Boolean, Boolean, Boolean, CallWidgetMode) -> MatrixWidgetSettings = { _, _, _, _, _, _ ->
        MatrixWidgetSettings("id", true, "url")
    }
) : CallWidgetSettingsProvider {
    val providedBaseUrls = mutableListOf<String>()
    val providedCallModes = mutableListOf<CallWidgetMode>()

    override suspend fun provide(
        baseUrl: String,
        widgetId: String,
        encrypted: Boolean,
        direct: Boolean,
        hasActiveCall: Boolean,
        callMode: CallWidgetMode,
    ): MatrixWidgetSettings {
        providedBaseUrls += baseUrl
        providedCallModes += callMode
        return provideFn(baseUrl, widgetId, encrypted, direct, hasActiveCall, callMode)
    }
}
