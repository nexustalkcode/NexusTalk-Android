/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.logout.api.LogoutUseCase
import io.element.android.libraries.matrix.api.MatrixClientProvider
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.sessionstorage.api.SessionStore
import timber.log.Timber

/**
 * 默认退出登录用例实现
 *
 * 实现了 LogoutUseCase 接口，负责退出所有已登录的会话。
 * 遍历所有已存储的会话，对每个会话执行退出登录操作。
 *
 * @property sessionStore 会话存储，用于获取所有已登录的会话
 * @property matrixClientProvider Matrix 客户端提供者，用于获取或恢复客户端实例
 */
@ContributesBinding(AppScope::class)
class DefaultLogoutUseCase(
    private val sessionStore: SessionStore,
    private val matrixClientProvider: MatrixClientProvider,
) : LogoutUseCase {
    /**
     * 退出所有已登录的用户会话
     *
     * @param ignoreSdkError 是否忽略 SDK 错误，强制退出所有会话
     */
    override suspend fun logoutAll(ignoreSdkError: Boolean) {
        sessionStore.getAllSessions()
            .map { sessionData ->
                SessionId(sessionData.userId)
            }
            .forEach { sessionId ->
                Timber.d("正在退出会话: $sessionId")
                matrixClientProvider.getOrRestore(sessionId).fold(
                    onSuccess = { client ->
                        client.logout(userInitiated = true, ignoreSdkError = ignoreSdkError)
                    },
                    onFailure = { error ->
                        Timber.e(error, "无法获取或恢复会话 $sessionId 的 MatrixClient")
                    }
                )
            }
    }
}
