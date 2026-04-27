/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.push.impl.push

import io.element.android.tests.testutils.lambda.lambdaError

class FakeMutableBatteryOptimizationStore(
    private val showBatteryOptimizationBannerResult: (String) -> Unit = { lambdaError() },
    private val onOptimizationBannerDismissedResult: () -> Unit = { lambdaError() },
    private val resetResult: () -> Unit = { lambdaError() },
) : MutableBatteryOptimizationStore {
    override suspend fun showBatteryOptimizationBanner(reason: String) {
        showBatteryOptimizationBannerResult(reason)
    }

    override suspend fun onOptimizationBannerDismissed() {
        onOptimizationBannerDismissedResult()
    }

    override suspend fun reset() {
        resetResult()
    }
}
