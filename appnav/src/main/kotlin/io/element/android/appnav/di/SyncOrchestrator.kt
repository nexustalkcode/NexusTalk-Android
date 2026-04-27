/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appnav.di

import androidx.annotation.VisibleForTesting
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.features.networkmonitor.api.NetworkMonitor
import io.element.android.features.networkmonitor.api.NetworkStatus
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.core.coroutine.childScope
import io.element.android.libraries.matrix.api.sync.SyncService
import io.element.android.libraries.matrix.api.sync.SyncState
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analytics.api.recordTransaction
import io.element.android.services.analyticsproviders.api.AnalyticsUserData
import io.element.android.services.appnavstate.api.AppForegroundStateService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * 同步服务编排器。
 *
 * 根据应用前后台、通话、通知同步和网络状态决定是否启动/停止 SyncService。
 */
@AssistedInject
class SyncOrchestrator(
    @Assisted private val syncService: SyncService,
    @Assisted sessionCoroutineScope: CoroutineScope,
    private val appForegroundStateService: AppForegroundStateService,
    private val networkMonitor: NetworkMonitor,
    dispatchers: CoroutineDispatchers,
    private val analyticsService: AnalyticsService,
) {
    /**
     * 创建 SyncOrchestrator 的 Assisted 工厂。
     */
    @AssistedFactory
    interface Factory {
        fun create(
            syncService: SyncService,
            sessionCoroutineScope: CoroutineScope,
        ): SyncOrchestrator
    }

    private val tag = "SyncOrchestrator"

    private val coroutineScope = sessionCoroutineScope.childScope(dispatchers.io, tag)

    private val started = AtomicBoolean(false)

    /**
     * 开始观察应用状态和网络状态，以控制同步服务启停。
     *
     * 在开始观察前，会先尝试启动一次同步服务，以便尽早暴露服务器可达性状态。
     */
    fun start() {
        if (!started.compareAndSet(false, true)) {
            Timber.tag(tag).d("already started, exiting early")
            return
        }

        coroutineScope.launch {
            // Perform an initial sync if the sync service is not running, to check whether the homeserver is accessible
            // Otherwise, if the device is offline the sync service will never start and the SyncState will be Idle, not Offline
            Timber.tag(tag).d("performing initial sync attempt")
            analyticsService.recordTransaction("First sync", "syncService.startSync()") { transaction ->
                syncService.startSync()

                // Wait until the sync service is not idle, either it will be running or in error/offline state
                val firstState = syncService.syncState.first { it != SyncState.Idle }
                transaction.putIndexableData(AnalyticsUserData.FIRST_SYNC_STATE, firstState.name)
            }

            observeStates()
        }
    }

    @OptIn(FlowPreview::class)
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    /**
     * 观察应用活跃状态、网络状态和同步状态，并执行启停动作。
     */
    internal fun observeStates() = coroutineScope.launch {
        Timber.tag(tag).d("start observing the app and network state")

        val isAppActiveFlow = combine(
            appForegroundStateService.isInForeground,
            appForegroundStateService.isInCall,
            appForegroundStateService.isSyncingNotificationEvent,
            appForegroundStateService.hasRingingCall,
        ) { isInForeground, isInCall, isSyncingNotificationEvent, hasRingingCall ->
            isInForeground || isInCall || isSyncingNotificationEvent || hasRingingCall
        }

        combine(
            // small debounce to avoid spamming startSync when the state is changing quickly in case of error.
            syncService.syncState.debounce(100.milliseconds),
            networkMonitor.connectivity,
            isAppActiveFlow,
        ) { syncState, networkState, isAppActive ->
            val isNetworkAvailable = networkState == NetworkStatus.Connected

            Timber.tag(tag).d("isAppActive=$isAppActive, isNetworkAvailable=$isNetworkAvailable")
            if (syncState == SyncState.Running && !isAppActive) {
                SyncStateAction.StopSync
            } else if (syncState == SyncState.Idle && isAppActive && isNetworkAvailable) {
                SyncStateAction.StartSync
            } else {
                SyncStateAction.NoOp
            }
        }
            .distinctUntilChanged()
            .debounce { action ->
                // Don't stop the sync immediately, wait a bit to avoid starting/stopping the sync too often
                if (action == SyncStateAction.StopSync) 3.seconds else 0.seconds
            }
            .onCompletion {
                Timber.tag(tag).d("has been stopped")
            }
            .collect { action ->
                when (action) {
                    SyncStateAction.StartSync -> {
                        syncService.startSync()
                    }
                    SyncStateAction.StopSync -> {
                        syncService.stopSync()
                    }
                    SyncStateAction.NoOp -> Unit
                }
            }
    }
}

/**
 * 对同步服务执行的动作。
 */
private enum class SyncStateAction {
    StartSync,
    StopSync,
    NoOp,
}
