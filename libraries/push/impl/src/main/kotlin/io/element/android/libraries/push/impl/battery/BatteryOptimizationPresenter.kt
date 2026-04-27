/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.push.impl.battery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.push.api.battery.BatteryOptimizationEvents
import io.element.android.libraries.push.api.battery.BatteryOptimizationState
import io.element.android.libraries.push.impl.push.MutableBatteryOptimizationStore
import io.element.android.libraries.push.impl.store.PushDataStore
import kotlinx.coroutines.launch
import timber.log.Timber

private const val batteryOptimizationDebugTag = "BatteryOptimizationDebug"

@Inject
class BatteryOptimizationPresenter(
    private val pushDataStore: PushDataStore,
    private val mutableBatteryOptimizationStore: MutableBatteryOptimizationStore,
    private val batteryOptimization: BatteryOptimization,
) : Presenter<BatteryOptimizationState> {
    @Composable
    override fun present(): BatteryOptimizationState {
        val coroutineScope = rememberCoroutineScope()
        var isRequestSent by remember { mutableStateOf(false) }
        var localShouldDisplayBanner by remember { mutableStateOf(true) }
        val persistedStoreShouldDisplayBanner by pushDataStore.shouldDisplayBatteryOptimizationBannerFlow.collectAsState(initial = null)
        val hasResolvedStoreShouldDisplayBanner = persistedStoreShouldDisplayBanner != null
        val storeShouldDisplayBanner = persistedStoreShouldDisplayBanner ?: false
        var lastObservedResolvedStoreShouldDisplayBanner by remember { mutableStateOf<Boolean?>(null) }
        var shouldDisplayBannerWithoutPreviousPushFailure by remember {
            mutableStateOf(batteryOptimization.shouldDisplayBannerWithoutPreviousPushFailure())
        }
        var isSystemIgnoringBatteryOptimizations by remember {
            mutableStateOf(batteryOptimization.isIgnoringBatteryOptimizations())
        }

        LifecycleResumeEffect(Unit) {
            shouldDisplayBannerWithoutPreviousPushFailure = batteryOptimization.shouldDisplayBannerWithoutPreviousPushFailure()
            isSystemIgnoringBatteryOptimizations = batteryOptimization.isIgnoringBatteryOptimizations()
            if (isRequestSent) {
                localShouldDisplayBanner = false
            }
            Timber.tag(batteryOptimizationDebugTag).i(
                "BatteryOptimizationPresenter.resume_state localShouldDisplayBanner=%s storeShouldDisplayBanner=%s storeResolved=%s proactive=%s ignoringOptimizations=%s computedShouldDisplay=%s",
                localShouldDisplayBanner,
                storeShouldDisplayBanner,
                hasResolvedStoreShouldDisplayBanner,
                shouldDisplayBannerWithoutPreviousPushFailure,
                isSystemIgnoringBatteryOptimizations,
                localShouldDisplayBanner && (
                    shouldDisplayBannerWithoutPreviousPushFailure ||
                        (storeShouldDisplayBanner && !isSystemIgnoringBatteryOptimizations)
                    ),
            )
            onPauseOrDispose {}
        }

        LaunchedEffect(
            persistedStoreShouldDisplayBanner,
            localShouldDisplayBanner,
            storeShouldDisplayBanner,
            hasResolvedStoreShouldDisplayBanner,
            shouldDisplayBannerWithoutPreviousPushFailure,
            isSystemIgnoringBatteryOptimizations,
        ) {
            Timber.tag(batteryOptimizationDebugTag).i(
                "BatteryOptimizationPresenter.visibility_evaluated localShouldDisplayBanner=%s storeShouldDisplayBanner=%s storeResolved=%s proactive=%s ignoringOptimizations=%s branch=%s computedShouldDisplay=%s",
                localShouldDisplayBanner,
                storeShouldDisplayBanner,
                hasResolvedStoreShouldDisplayBanner,
                shouldDisplayBannerWithoutPreviousPushFailure,
                isSystemIgnoringBatteryOptimizations,
                when {
                    !hasResolvedStoreShouldDisplayBanner -> "awaiting_store"
                    shouldDisplayBannerWithoutPreviousPushFailure -> "proactive"
                    storeShouldDisplayBanner && !isSystemIgnoringBatteryOptimizations -> "push_failure_store"
                    else -> "hidden"
                },
                localShouldDisplayBanner && (
                    shouldDisplayBannerWithoutPreviousPushFailure ||
                        (storeShouldDisplayBanner && !isSystemIgnoringBatteryOptimizations)
                    ),
            )
        }

        LaunchedEffect(persistedStoreShouldDisplayBanner) {
            if (!hasResolvedStoreShouldDisplayBanner) return@LaunchedEffect
            val previousValue = lastObservedResolvedStoreShouldDisplayBanner
            Timber.tag(batteryOptimizationDebugTag).i(
                "BatteryOptimizationPresenter.store_resolution_event previous=%s current=%s source=%s",
                previousValue,
                storeShouldDisplayBanner,
                if (previousValue == null) "first_resolved_from_disk" else "runtime_update",
            )
            lastObservedResolvedStoreShouldDisplayBanner = storeShouldDisplayBanner
        }

        fun handleEvent(event: BatteryOptimizationEvents) {
            when (event) {
                BatteryOptimizationEvents.Dismiss -> coroutineScope.launch {
                    Timber.tag(batteryOptimizationDebugTag).i("BatteryOptimizationPresenter.dismiss_banner")
                    mutableBatteryOptimizationStore.onOptimizationBannerDismissed()
                }
                BatteryOptimizationEvents.RequestDisableOptimizations -> {
                    isRequestSent = true
                    Timber.tag(batteryOptimizationDebugTag).i(
                        "BatteryOptimizationPresenter.request_disable_optimizations storeShouldDisplayBanner=%s storeResolved=%s proactive=%s ignoringOptimizations=%s",
                        storeShouldDisplayBanner,
                        hasResolvedStoreShouldDisplayBanner,
                        shouldDisplayBannerWithoutPreviousPushFailure,
                        isSystemIgnoringBatteryOptimizations,
                    )
                    val launchSucceeded = batteryOptimization.requestDisablingBatteryOptimization()
                    Timber.tag(batteryOptimizationDebugTag).i(
                        "BatteryOptimizationPresenter.request_disable_optimizations_result launchSucceeded=%s",
                        launchSucceeded,
                    )
                    if (launchSucceeded && batteryOptimization.shouldDismissBannerAfterSuccessfulRequest()) {
                        Timber.tag(batteryOptimizationDebugTag).i(
                            "BatteryOptimizationPresenter.dismiss_banner_after_successful_launch"
                        )
                        coroutineScope.launch {
                            mutableBatteryOptimizationStore.onOptimizationBannerDismissed()
                        }
                    } else if (!launchSucceeded) {
                        // If not able to perform the request, ensure that we do not display the banner again
                        coroutineScope.launch {
                            Timber.tag(batteryOptimizationDebugTag).w(
                                "BatteryOptimizationPresenter.dismiss_banner_after_failed_launch"
                            )
                            mutableBatteryOptimizationStore.onOptimizationBannerDismissed()
                        }
                    }
                }
            }
        }

        return BatteryOptimizationState(
            shouldDisplayBanner = localShouldDisplayBanner && (
                shouldDisplayBannerWithoutPreviousPushFailure ||
                    (storeShouldDisplayBanner && !isSystemIgnoringBatteryOptimizations)
                ),
            eventSink = ::handleEvent,
        )
    }
}
