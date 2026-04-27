/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.services.analytics.impl.watchers

import dev.zacsweers.metro.Inject
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.core.coroutine.childScope
import io.element.android.libraries.core.coroutine.withPreviousValue
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.roomlist.RoomListService
import io.element.android.services.analytics.api.AnalyticsLongRunningTransaction
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analytics.api.finishLongRunningTransaction
import io.element.android.services.analytics.api.watchers.AnalyticsRoomListStateWatcher
import io.element.android.services.appnavstate.api.AppForegroundStateService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class DefaultAnalyticsRoomListStateWatcher @Inject constructor(
    private val appForegroundStateService: AppForegroundStateService,
    private val roomListService: RoomListService,
    private val analyticsService: AnalyticsService,
    @SessionCoroutineScope sessionCoroutineScope: CoroutineScope,
    dispatchers: CoroutineDispatchers,
) : AnalyticsRoomListStateWatcher {
    private val coroutineScope: CoroutineScope = sessionCoroutineScope.childScope(dispatchers.computation, "AnalyticsRoomListStateWatcher")
    private val isStarted = AtomicBoolean(false)
    // 这里只在“后台 -> 前台”切换后标记 warm state，避免首次订阅时就把冷启动误判成一次 catch-up。
    private val isWarmState = AtomicBoolean(false)

    override fun start() {
        if (isStarted.getAndSet(true)) {
            Timber.w("Can't start RoomListStateWatcher, it's already running.")
            return
        }

        val longRunningTransaction = AnalyticsLongRunningTransaction.CatchUp

        appForegroundStateService.isInForeground
            .withPreviousValue()
            .onEach { (wasInForeground, isInForeground) ->
                if (isInForeground && roomListService.state.value != RoomListService.State.Running) {
                    analyticsService.startLongRunningTransaction(longRunningTransaction)
                } else if (!isInForeground) {
                    analyticsService.removeLongRunningTransaction(longRunningTransaction)
                }

                if (wasInForeground == false && isInForeground) {
                    // 只有真实经历过回前台后，后续 room list 进入 Running 才算一次可结束的 catch-up。
                    isWarmState.set(true)
                }
            }
            .launchIn(coroutineScope)

        roomListService.state
            .onEach { state ->
                if (state == RoomListService.State.Running && isWarmState.get()) {
                    analyticsService.finishLongRunningTransaction(longRunningTransaction)
                }
            }
            .launchIn(coroutineScope)
    }

    override fun stop() {
        if (isStarted.getAndSet(false)) {
            coroutineScope.coroutineContext.cancelChildren()
        }
    }
}
