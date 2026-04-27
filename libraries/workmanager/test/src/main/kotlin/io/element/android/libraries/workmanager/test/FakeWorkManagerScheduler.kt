/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.workmanager.test

import io.element.android.libraries.workmanager.api.WorkManagerRequest
import io.element.android.libraries.workmanager.api.WorkManagerRequestType
import io.element.android.libraries.workmanager.api.WorkManagerScheduler

class FakeWorkManagerScheduler(
    private val submitLambda: (WorkManagerRequest) -> Unit = {
        error("submitLambda should be provided in tests")
    },
    private val hasPendingWorkLambda: (String, WorkManagerRequestType) -> Boolean = { _, _ -> false },
    private val cancelLambda: (String) -> Unit = {
        error("cancelLambda should be provided in tests")
    },
) : WorkManagerScheduler {
    override fun submit(workManagerRequest: WorkManagerRequest) {
        submitLambda(workManagerRequest)
    }

    override fun hasPendingWork(sessionId: String, requestType: WorkManagerRequestType): Boolean {
        return hasPendingWorkLambda(sessionId, requestType)
    }

    override fun cancel(sessionId: String) {
        cancelLambda(sessionId)
    }
}
