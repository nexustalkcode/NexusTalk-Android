/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.push.test

import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.push.api.PushService
import io.element.android.libraries.push.api.history.PushHistoryItem
import io.element.android.libraries.pushproviders.api.Distributor
import io.element.android.libraries.pushproviders.api.PushProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePushService(
    private val testPushBlock: suspend (SessionId) -> Boolean = { true },
    private val availablePushProviders: List<PushProvider> = emptyList(),
    private val registerWithLambda: (MatrixClient, PushProvider, Distributor) -> Result<Unit> = { _, _, _ ->
        Result.success(Unit)
    },
    private val currentPushProvider: (SessionId) -> PushProvider? = { availablePushProviders.firstOrNull() },
    private val selectPushProviderLambda: suspend (SessionId, PushProvider) -> Unit = { _, _ ->
        error("selectPushProviderLambda should be provided in tests")
    },
    private val setIgnoreRegistrationErrorLambda: (SessionId, Boolean) -> Unit = { _, _ ->
        error("setIgnoreRegistrationErrorLambda should be provided in tests")
    },
    private val resetPushHistoryResult: () -> Unit = {
        error("resetPushHistoryResult should be provided in tests")
    },
    private val resetBatteryOptimizationStateResult: () -> Unit = {
        error("resetBatteryOptimizationStateResult should be provided in tests")
    },
    private val onServiceUnregisteredResult: (UserId) -> Unit = {
        error("onServiceUnregisteredResult should be provided in tests")
    },
    private val ensurePusherIsRegisteredResult: () -> Result<Unit> = {
        error("ensurePusherIsRegisteredResult should be provided in tests")
    },
) : PushService {
    override suspend fun getCurrentPushProvider(sessionId: SessionId): PushProvider? {
        return registeredPushProvider ?: currentPushProvider(sessionId)
    }

    override fun getAvailablePushProviders(): List<PushProvider> {
        return availablePushProviders
    }

    private var registeredPushProvider: PushProvider? = null

    override suspend fun registerWith(
        matrixClient: MatrixClient,
        pushProvider: PushProvider,
        distributor: Distributor,
    ): Result<Unit> {
        return registerWithLambda(matrixClient, pushProvider, distributor)
            .also {
                if (it.isSuccess) {
                    registeredPushProvider = pushProvider
                }
            }
    }

    override suspend fun ensurePusherIsRegistered(matrixClient: MatrixClient): Result<Unit> {
        return ensurePusherIsRegisteredResult()
    }

    override suspend fun selectPushProvider(sessionId: SessionId, pushProvider: PushProvider) {
        selectPushProviderLambda(sessionId, pushProvider)
    }

    private val ignoreRegistrationError = MutableStateFlow(false)

    override fun ignoreRegistrationError(sessionId: SessionId): Flow<Boolean> {
        return ignoreRegistrationError
    }

    override suspend fun setIgnoreRegistrationError(sessionId: SessionId, ignore: Boolean) {
        ignoreRegistrationError.value = ignore
        setIgnoreRegistrationErrorLambda(sessionId, ignore)
    }

    override suspend fun testPush(sessionId: SessionId): Boolean = testPushBlock(sessionId)

    private val pushHistoryItemsFlow = MutableStateFlow<List<PushHistoryItem>>(emptyList())

    override fun getPushHistoryItemsFlow(): Flow<List<PushHistoryItem>> {
        return pushHistoryItemsFlow
    }

    fun emitPushHistoryItems(items: List<PushHistoryItem>) {
        pushHistoryItemsFlow.value = items
    }

    private val pushCounterFlow = MutableStateFlow(0)

    override val pushCounter: Flow<Int> = pushCounterFlow

    fun emitPushCounter(counter: Int) {
        pushCounterFlow.value = counter
    }

    override suspend fun resetPushHistory() = resetPushHistoryResult()

    override suspend fun resetBatteryOptimizationState() {
        resetBatteryOptimizationStateResult()
    }

    override suspend fun onServiceUnregistered(userId: UserId) {
        onServiceUnregisteredResult(userId)
    }
}
