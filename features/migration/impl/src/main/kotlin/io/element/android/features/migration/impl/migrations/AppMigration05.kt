/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.migration.impl.migrations

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import io.element.android.libraries.di.BaseDirectory
import io.element.android.libraries.sessionstorage.api.SessionStore
import java.io.File

@ContributesIntoSet(AppScope::class)
/**
 * 迁移到会话目录持久化路径结构的步骤。
 */
class AppMigration05(
    private val sessionStore: SessionStore,
    @BaseDirectory private val baseDirectory: File,
) : AppMigration {
    override val order: Int = 5

    /**
     * 为缺失会话目录路径的历史会话补齐默认路径。
     */
    override suspend fun migrate(isFreshInstall: Boolean) {
        val allSessions = sessionStore.getAllSessions()
        for (session in allSessions) {
            if (session.sessionPath.isEmpty()) {
                val sessionPath = File(baseDirectory, session.userId.replace(':', '_')).absolutePath
                sessionStore.updateData(session.copy(sessionPath = sessionPath))
            }
        }
    }
}
