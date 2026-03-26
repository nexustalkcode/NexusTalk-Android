/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.resolver

import dev.zacsweers.metro.Inject
import io.element.android.libraries.core.coroutine.CoroutineDispatchers
import io.element.android.libraries.core.coroutine.parallelMap
import io.element.android.libraries.core.uri.ensureProtocol
import io.element.android.libraries.core.uri.isValidUrl
import io.element.android.libraries.matrix.api.auth.HomeServerLoginCompatibilityChecker
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Collections

/**
 * Homeserver 解析器
 *
 * 根据用户输入的搜索词解析和验证 homeserver。
 * 支持从输入推断可能的域名，并并行验证服务器兼容性。
 *
 * @property dispatchers 协程调度器
 * @property homeServerLoginCompatibilityChecker 服务器登录兼容性检查器
 * @see HomeserverData 解析后的 homeserver 数据
 */
@Inject
class HomeserverResolver(
    private val dispatchers: CoroutineDispatchers,
    private val homeServerLoginCompatibilityChecker: HomeServerLoginCompatibilityChecker,
) {
    fun resolve(userInput: String): Flow<List<HomeserverData>> = flow {
        val flowContext = currentCoroutineContext()
        val trimmedUserInput = userInput.trim()
        if (trimmedUserInput.length < 4) return@flow
        val candidateBase = trimmedUserInput.ensureProtocol().removeSuffix("/")
        val list = getUrlCandidates(candidateBase)
        val currentList = Collections.synchronizedList(mutableListOf<HomeserverData>())
        // Run all the requests in parallel
        withContext(dispatchers.io) {
            list.parallelMap { url ->
                val isValid = homeServerLoginCompatibilityChecker.check(url)
                    .onFailure { Timber.w(it, "Failed to check compatibility with homeserver $url") }
                    .getOrNull()
                    ?: return@parallelMap

                // Emit the list as soon as possible
                if (isValid) {
                    currentList.add(HomeserverData(homeserverUrl = url))
                    withContext(flowContext) {
                        emit(currentList.toList())
                    }
                }
            }
        }
        // If list is empty, and candidateBase is a valid an URL, do not block the user.
        // A unsupported homeserver / homeserver not found error will be displayed if the user continues
        if (currentList.isEmpty() && candidateBase.isValidUrl()) {
            emit(listOf(HomeserverData(homeserverUrl = candidateBase)))
        }
    }

    private fun getUrlCandidates(data: String): List<String> {
        return buildList {
            if (data.contains(".")) {
                // TLD detected?
            } else {
                add("$data.org")
                add("$data.com")
                add("$data.io")
            }
            // Always try what the user has entered
            add(data)
        }
    }
}
