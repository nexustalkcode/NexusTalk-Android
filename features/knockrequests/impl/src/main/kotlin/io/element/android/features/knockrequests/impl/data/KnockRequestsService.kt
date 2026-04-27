/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.impl.data

import io.element.android.features.knockrequests.api.KnockRequestPermissions
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.room.knock.KnockRequest
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.supervisorScope

/**
 * 负责聚合、过滤并处理房间中的敲门请求。
 *
 * 该服务把底层 Matrix 的敲门请求流转换成 UI 可消费的数据，
 * 同时维护一个“已处理请求”集合，用于在本地先行隐藏已经接受或拒绝的请求，
 * 避免等待下一次 sync 才反映到界面。
 */
class KnockRequestsService(
    knockRequestsFlow: Flow<List<KnockRequest>>,
    permissionsFlow: Flow<KnockRequestPermissions>,
    isKnockFeatureEnabledFlow: Flow<Boolean>,
    coroutineScope: CoroutineScope,
) {
    // Keep track of the knock requests that have been handled, so we don't have to wait for sync to remove them.
    private val handledKnockRequestIds = MutableStateFlow<Set<EventId>>(emptySet())

    val knockRequestsFlow = combine(
        isKnockFeatureEnabledFlow,
        knockRequestsFlow,
        handledKnockRequestIds,
    ) { isKnockEnabled, knockRequests, handledKnockIds ->
        if (!isKnockEnabled) {
            AsyncData.Success(persistentListOf())
        } else {
            val presentableKnockRequests = knockRequests
                .filter { it.eventId !in handledKnockIds }
                .map { inner -> KnockRequestWrapper(inner) }
                .toImmutableList()
            AsyncData.Success(presentableKnockRequests)
        }
    }.stateIn(coroutineScope, SharingStarted.Lazily, AsyncData.Loading())

    val permissionsFlow = permissionsFlow.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = KnockRequestPermissions(canAccept = false, canDecline = false, canBan = false)
    )

    private fun knockRequestsList() = knockRequestsFlow.value.dataOrNull().orEmpty()

    private fun getKnockRequestById(eventId: EventId): KnockRequestWrapper? {
        return knockRequestsList().find { it.eventId == eventId }
    }

    /**
     * 接受一条敲门请求。
     *
     * @param knockRequest 需要接受的敲门请求。
     * @param optimistic 是否在服务端返回前先把请求标记为已处理。
     */
    suspend fun acceptKnockRequest(knockRequest: KnockRequestPresentable, optimistic: Boolean = false): Result<Unit> {
        val wrapped = getKnockRequestById(knockRequest.eventId) ?: return knockRequestNotFoundResult()
        return handleKnockRequest(wrapped, optimistic) { accept() }
    }

    /**
     * 拒绝一条敲门请求。
     *
     * @param knockRequest 需要拒绝的敲门请求。
     * @param optimistic 是否在服务端返回前先把请求标记为已处理。
     */
    suspend fun declineKnockRequest(knockRequest: KnockRequestPresentable, optimistic: Boolean = false): Result<Unit> {
        val wrapped = getKnockRequestById(knockRequest.eventId) ?: return knockRequestNotFoundResult()
        return handleKnockRequest(wrapped, optimistic) { decline(null) }
    }

    /**
     * 通过封禁用户的方式拒绝一条敲门请求。
     *
     * @param knockRequest 需要拒绝并封禁的敲门请求。
     * @param optimistic 是否在服务端返回前先把请求标记为已处理。
     */
    suspend fun declineAndBanKnockRequest(knockRequest: KnockRequestPresentable, optimistic: Boolean = false): Result<Unit> {
        val wrapped = getKnockRequestById(knockRequest.eventId) ?: return knockRequestNotFoundResult()
        return handleKnockRequest(wrapped, optimistic) { declineAndBan(null) }
    }

    /**
     * 接受当前已知的全部敲门请求。
     *
     * @param optimistic 是否在服务端返回前先把这些请求标记为已处理。
     */
    suspend fun acceptAllKnockRequests(optimistic: Boolean = false): Result<Unit> = supervisorScope {
        val results = knockRequestsList()
            .map { knockRequest ->
                async {
                    acceptKnockRequest(knockRequest, optimistic = optimistic)
                }
            }
            .awaitAll()
        if (results.all { it.isSuccess }) {
            Result.success(Unit)
        } else {
            Result.failure(KnockRequestsException.AcceptAllPartiallyFailed)
        }
    }

    /**
     * 将当前已知的全部敲门请求标记为已读。
     */
    suspend fun markAllKnockRequestsAsSeen() = supervisorScope {
        knockRequestsList()
            .map { knockRequest ->
                async { knockRequest.markAsSeen() }
            }
            .awaitAll()
    }

    /**
     * 统一处理单条敲门请求的乐观更新与回滚逻辑。
     *
     * @param knockRequest 当前需要处理的敲门请求包装对象。
     * @param optimistic 是否启用乐观更新。
     * @param action 真正发给底层 SDK 的处理动作。
     */
    private suspend fun handleKnockRequest(
        knockRequest: KnockRequestWrapper,
        optimistic: Boolean,
        action: suspend (KnockRequestWrapper.() -> Result<Unit>)
    ): Result<Unit> {
        if (optimistic) {
            handledKnockRequestIds.getAndUpdate { it + knockRequest.eventId }
        }
        return action(knockRequest)
            .onFailure {
                if (optimistic) {
                    handledKnockRequestIds.getAndUpdate { it - knockRequest.eventId }
                }
            }
            .onSuccess {
                if (!optimistic) {
                    handledKnockRequestIds.getAndUpdate { it + knockRequest.eventId }
                }
            }
    }
}

/**
 * 生成“请求不存在”的统一失败结果。
 */
private fun knockRequestNotFoundResult() = Result.failure<Unit>(KnockRequestsException.KnockRequestNotFound)
