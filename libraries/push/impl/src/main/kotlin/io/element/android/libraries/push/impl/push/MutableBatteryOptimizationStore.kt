/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.push.impl.push

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.push.impl.store.DefaultPushDataStore
import timber.log.Timber

private const val batteryOptimizationDebugTag = "BatteryOptimizationDebug"

interface MutableBatteryOptimizationStore {
    suspend fun showBatteryOptimizationBanner(reason: String)
    suspend fun onOptimizationBannerDismissed()
    suspend fun reset()
}

@ContributesBinding(AppScope::class)
class DefaultMutableBatteryOptimizationStore(
    private val defaultPushDataStore: DefaultPushDataStore,
) : MutableBatteryOptimizationStore {
    override suspend fun showBatteryOptimizationBanner(reason: String) {
        Timber.tag(batteryOptimizationDebugTag).w("BatteryOptimizationStore.show_banner reason=%s", reason)
        defaultPushDataStore.setBatteryOptimizationBannerState(
            newState = DefaultPushDataStore.BATTERY_OPTIMIZATION_BANNER_STATE_SHOW,
            reason = reason,
        )
    }

    override suspend fun onOptimizationBannerDismissed() {
        Timber.tag(batteryOptimizationDebugTag).i("BatteryOptimizationStore.dismiss_banner")
        defaultPushDataStore.setBatteryOptimizationBannerState(
            newState = DefaultPushDataStore.BATTERY_OPTIMIZATION_BANNER_STATE_DISMISSED,
        )
    }

    override suspend fun reset() {
        Timber.tag(batteryOptimizationDebugTag).i("BatteryOptimizationStore.reset_banner_state")
        defaultPushDataStore.setBatteryOptimizationBannerState(
            newState = DefaultPushDataStore.BATTERY_OPTIMIZATION_BANNER_STATE_INIT,
        )
    }
}
