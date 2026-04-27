/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:Suppress("WildcardImport")
@file:OptIn(ExperimentalCoroutinesApi::class)

package io.element.android.features.verifysession.impl.outgoing

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import io.element.android.features.verifysession.impl.util.andLogStateChange
import io.element.android.features.verifysession.impl.util.logReceivedEvents
import io.element.android.libraries.core.bool.orFalse
import io.element.android.libraries.core.data.tryOrNull
import io.element.android.libraries.matrix.api.encryption.EncryptionService
import io.element.android.libraries.matrix.api.encryption.RecoveryState
import io.element.android.libraries.matrix.api.verification.SessionVerificationData
import io.element.android.libraries.matrix.api.verification.SessionVerificationService
import io.element.android.libraries.matrix.api.verification.VerificationRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.timeout
import kotlin.time.Duration.Companion.seconds
import com.freeletics.flowredux.dsl.State as MachineState

@OptIn(FlowPreview::class)
/**
 * 发起验证流程状态机。
 *
 * 负责管理“请求验证、等待接受、开始 SAS、比对 challenge、完成或取消”这一整条状态迁移链路。
 */
class OutgoingVerificationStateMachine(
    private val sessionVerificationService: SessionVerificationService,
    private val encryptionService: EncryptionService,
) : FlowReduxStateMachine<OutgoingVerificationStateMachine.State, OutgoingVerificationStateMachine.Event>(
    initialState = State.Initial,
) {
    init {
        spec {
            inState<State.Initial> {
                on<Event.RequestVerification> { event, state ->
                    state.override { State.RequestingVerification(event.verificationRequest).andLogStateChange() }
                }
            }
            inState<State.RequestingVerification> {
                onEnterEffect { event ->
                    when (event.verificationRequest) {
                        is VerificationRequest.Outgoing.CurrentSession -> sessionVerificationService.requestCurrentSessionVerification()
                        is VerificationRequest.Outgoing.User -> sessionVerificationService.requestUserVerification(event.verificationRequest.userId)
                    }
                }
                on<Event.DidAcceptVerificationRequest> { _, state ->
                    state.override { State.VerificationRequestAccepted.andLogStateChange() }
                }
            }
            inState<State.StartingSasVerification> {
                onEnterEffect {
                    sessionVerificationService.startVerification()
                }
            }
            inState<State.VerificationRequestAccepted> {
                on<Event.StartSasVerification> { _, state ->
                    state.override { State.StartingSasVerification.andLogStateChange() }
                }
            }
            inState<State.Canceled> {
                on<Event.Reset> { _, state ->
                    sessionVerificationService.reset(cancelAnyPendingVerificationAttempt = false)
                    state.override { State.Initial.andLogStateChange() }
                }
            }
            inState<State.SasVerificationStarted> {
                on<Event.DidReceiveChallenge> { event, state ->
                    state.override { State.Verifying.ChallengeReceived(event.data).andLogStateChange() }
                }
            }
            inState<State.Verifying.ChallengeReceived> {
                on<Event.AcceptChallenge> { _, state ->
                    state.override { State.Verifying.Replying(state.snapshot.data, accept = true).andLogStateChange() }
                }
                on<Event.DeclineChallenge> { _, state ->
                    state.override { State.Verifying.Replying(state.snapshot.data, accept = false).andLogStateChange() }
                }
            }
            inState<State.Verifying.Replying> {
                onEnterEffect { state ->
                    if (state.accept) {
                        sessionVerificationService.approveVerification()
                    } else {
                        sessionVerificationService.declineVerification()
                    }
                }
                on<Event.DidAcceptChallenge> { _, state ->
                    // If a key backup exists, wait until it's restored or a timeout happens
                    val hasBackup = encryptionService.doesBackupExistOnServer().getOrNull().orFalse()
                    if (hasBackup) {
                        tryOrNull {
                            encryptionService.recoveryStateStateFlow.filter { it == RecoveryState.ENABLED }
                                .timeout(10.seconds)
                                .first()
                        }
                    }
                    state.override { State.Completed.andLogStateChange() }
                }
            }
            inState {
                logReceivedEvents()
                on<Event.DidStartSasVerification> { _, state: MachineState<State> ->
                    state.override { State.SasVerificationStarted.andLogStateChange() }
                }
                on<Event.Cancel> { event, state: MachineState<State> ->
                    when (state.snapshot) {
                        State.Initial, State.Completed, is State.Canceled -> state.override { State.Exit }
                        // For some reason `cancelVerification` is not calling its delegate `didCancel` method so we don't pass from
                        // `Canceling` state to `Canceled` automatically anymore
                        else -> {
                            sessionVerificationService.cancelVerification()
                            state.override { State.Canceled.andLogStateChange() }
                        }
                    }
                }
                on<Event.DidCancel> { event, state: MachineState<State> ->
                    state.override { State.Canceled.andLogStateChange() }
                }
                on<Event.DidFail> { event, state: MachineState<State> ->
                    state.override { State.Canceled.andLogStateChange() }
                }
            }
        }
    }

    /**
     * 发起验证流程的状态集合。
     */
    sealed interface State {
        /** 初始状态，提示用户在另一端做好准备。 */
        data object Initial : State

        /** 已发出验证请求，等待对方接受。 */
        data class RequestingVerification(val verificationRequest: VerificationRequest.Outgoing) : State

        /** 对方已接受验证请求，等待开始 SAS。 */
        data object VerificationRequestAccepted : State

        /** 正在启动 SAS 验证。 */
        data object StartingSasVerification : State

        /** SAS 验证已启动。 */
        data object SasVerificationStarted : State

        /**
         * 正处于 challenge 比对阶段的子状态。
         *
         * @property data 当前 challenge 对应的会话验证数据。
         */
        sealed class Verifying(open val data: SessionVerificationData) : State {
            /** 已收到 challenge，等待用户比对。 */
            data class ChallengeReceived(override val data: SessionVerificationData) : Verifying(data)

            /** 正在向远端回复 challenge 结果。 */
            data class Replying(override val data: SessionVerificationData, val accept: Boolean) : Verifying(data)
        }

        /** 验证已被本地或远端取消。 */
        data object Canceled : State

        /** 验证成功完成。 */
        data object Completed : State

        /** 流程应立即退出。 */
        data object Exit : State
    }

    /**
     * 发起验证流程中会收到的事件集合。
     */
    sealed interface Event {
        /** 请求开始验证。 */
        data class RequestVerification(val verificationRequest: VerificationRequest.Outgoing) : Event

        /** 当前验证请求已被接受。 */
        data object DidAcceptVerificationRequest : Event

        /** 请求开始 SAS 验证。 */
        data object StartSasVerification : Event

        /** SAS 验证已成功启动。 */
        data object DidStartSasVerification : Event

        /** 已收到比对所需的 challenge 数据。 */
        data class DidReceiveChallenge(val data: SessionVerificationData) : Event

        /** 用户确认表情一致。 */
        data object AcceptChallenge : Event

        /** 用户确认表情不一致。 */
        data object DeclineChallenge : Event

        /** 远端已确认当前 challenge。 */
        data object DidAcceptChallenge : Event

        /** 请求取消当前验证流程。 */
        data object Cancel : Event

        /** 当前验证已被取消。 */
        data object DidCancel : Event

        /** 当前验证流程失败。 */
        data object DidFail : Event

        /** 把流程重置回初始状态。 */
        data object Reset : Event
    }
}
