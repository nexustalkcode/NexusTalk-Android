/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.workmanager.api

interface WorkManagerScheduler {
    fun submit(workManagerRequest: WorkManagerRequest)
    // workmanager 只需要一个稳定的会话字符串来打 tag，不需要依赖 matrix api 里的 SessionId 类型别名。
    fun hasPendingWork(sessionId: String, requestType: WorkManagerRequestType): Boolean
    fun cancel(sessionId: String)
}

fun workManagerTag(sessionId: String, requestType: WorkManagerRequestType): String {
    val prefix = when (requestType) {
        WorkManagerRequestType.NOTIFICATION_SYNC -> "notifications"
        WorkManagerRequestType.DB_VACUUM -> "db_vacuum"
    }
    return "$prefix-$sessionId"
}

enum class WorkManagerRequestType {
    NOTIFICATION_SYNC,
    DB_VACUUM,
}
