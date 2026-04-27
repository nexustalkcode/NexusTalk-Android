/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.services.appnavstate.impl

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import io.element.android.services.appnavstate.api.AppForegroundStateService
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

private const val incomingCallTraceTag = "IncomingCallTrace"

class DefaultAppForegroundStateService : AppForegroundStateService {
    override val isInForeground = MutableStateFlow(false)
    override val isInCall = MutableStateFlow(false)
    override val isSyncingNotificationEvent = MutableStateFlow(false)
    override val hasRingingCall = MutableStateFlow(false)

    private val appLifecycle: Lifecycle by lazy { ProcessLifecycleOwner.get().lifecycle }

    override fun startObservingForeground() {
        Timber.tag(incomingCallTraceTag).i(
            "AppForegroundStateService.startObservingForeground currentLifecycle=%s currentForeground=%s",
            appLifecycle.currentState,
            getCurrentState(),
        )
        appLifecycle.addObserver(lifecycleObserver)
    }

    override fun updateIsInCallState(isInCall: Boolean) {
        Timber.tag(incomingCallTraceTag).i("AppForegroundStateService.updateIsInCallState %s -> %s", this.isInCall.value, isInCall)
        this.isInCall.value = isInCall
    }

    override fun updateHasRingingCall(hasRingingCall: Boolean) {
        Timber.tag(incomingCallTraceTag).i("AppForegroundStateService.updateHasRingingCall %s -> %s", this.hasRingingCall.value, hasRingingCall)
        this.hasRingingCall.value = hasRingingCall
    }

    override fun updateIsSyncingNotificationEvent(isSyncingNotificationEvent: Boolean) {
        Timber.tag(incomingCallTraceTag).i(
            "AppForegroundStateService.updateIsSyncingNotificationEvent %s -> %s",
            this.isSyncingNotificationEvent.value,
            isSyncingNotificationEvent,
        )
        this.isSyncingNotificationEvent.value = isSyncingNotificationEvent
    }

    private val lifecycleObserver = LifecycleEventObserver { _, event ->
        val newValue = getCurrentState()
        Timber.tag(incomingCallTraceTag).i(
            "AppForegroundStateService.lifecycle event=%s foreground %s -> %s lifecycle=%s",
            event,
            isInForeground.value,
            newValue,
            appLifecycle.currentState,
        )
        isInForeground.value = newValue
    }

    private fun getCurrentState(): Boolean = appLifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
}
