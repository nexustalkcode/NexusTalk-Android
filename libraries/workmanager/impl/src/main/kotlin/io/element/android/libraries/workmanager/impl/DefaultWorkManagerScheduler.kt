/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.workmanager.impl

import androidx.work.WorkInfo
import androidx.work.WorkManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import io.element.android.libraries.sessionstorage.api.observer.SessionListener
import io.element.android.libraries.sessionstorage.api.observer.SessionObserver
import io.element.android.libraries.workmanager.api.WorkManagerRequest
import io.element.android.libraries.workmanager.api.WorkManagerRequestType
import io.element.android.libraries.workmanager.api.WorkManagerScheduler
import io.element.android.libraries.workmanager.api.workManagerTag
import timber.log.Timber

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class DefaultWorkManagerScheduler(
    lazyWorkManager: Lazy<WorkManager>,
    sessionObserver: SessionObserver,
) : WorkManagerScheduler {
    private val workManager by lazyWorkManager

    init {
        // 这里只关心“删除了哪个会话”的字符串标识，用来清理对应的 WorkManager tag。
        sessionObserver.addListener(object : SessionListener {
            override suspend fun onSessionDeleted(userId: String, wasLastSession: Boolean) {
                Timber.d("Session deleted for userId: $userId, cancelling associated workmanager requests")
                cancel(userId)
            }
        })
    }

    override fun submit(workManagerRequest: WorkManagerRequest) {
        workManagerRequest.build().fold(
            onSuccess = { workRequests ->
                workManager.enqueue(workRequests)
            },
            onFailure = {
                Timber.e(it, "Failed to build WorkManager request $workManagerRequest")
            }
        )
    }

    override fun hasPendingWork(sessionId: String, requestType: WorkManagerRequestType): Boolean {
        val workInfos = workManager.getWorkInfosByTag(workManagerTag(sessionId, requestType)).get().orEmpty()
        return workInfos.any { info ->
            val isPeriodic = info.periodicityInfo != null
            val isCancelled = info.state == WorkInfo.State.CANCELLED
            // It has pending work if:
            // - It's not periodic and is not finished.
            // - It's periodic and is not cancelled - since it'll be run again in a next iteration otherwise
            !isPeriodic && !info.state.isFinished || isPeriodic && !isCancelled
        }
    }

    override fun cancel(sessionId: String) {
        Timber.d("Cancelling work for sessionId: $sessionId")
        for (requestType in WorkManagerRequestType.entries) {
            workManager.cancelAllWorkByTag(workManagerTag(sessionId, requestType))
        }
    }
}
