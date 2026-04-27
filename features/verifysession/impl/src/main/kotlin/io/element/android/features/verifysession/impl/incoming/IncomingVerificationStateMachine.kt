/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package io.element.android.features.verifysession.impl.incoming

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.zacsweers.metro.Inject
import io.element.android.features.verifysession.impl.util.andLogStateChange
import io.element.android.features.verifysession.impl.util.logReceivedEvents
import io.element.android.libraries.matrix.api.verification.SessionVerificationData
import io.element.android.libraries.matrix.api.verification.SessionVerificationService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.freeletics.flowredux.dsl.State as MachineState

@Inject
/**
 * 传入验证流程状态机。
 *
 * 负责把用户在“接受请求、比对表情、确认或拒绝”等步骤中的动作，
 * 映射为底层验证服务调用和可观察的 UI 状态迁移。
 */
class IncomingVerificationStateMachine(
    private val sessionVerificationService: SessionVerificationService,
) : FlowReduxStateMachine<IncomingVerificationStateMachine.State, IncomingVerificationStateMachine.Event>(
    initialState = State.Initial(isCancelled = false)
) {
    init {
        spec {
            inState<State.Initial> {
                on<Event.AcceptIncomingRequest> { _, state ->
                    state.override { State.AcceptingIncomingVerification.andLogStateChange() }
                }
            }
            inState<State.AcceptingIncomingVerification> {
                onEnterEffect {
                    sessionVerificationService.acceptVerificationRequest()
                }
                on { event: Event.DidReceiveChallenge, state ->
                    state.override { State.ChallengeReceived(event.data).andLogStateChange() }
                }
            }
            inState<State.ChallengeReceived> {
                on<Event.AcceptChallenge> { _, state ->
                    state.override { State.AcceptingChallenge(state.snapshot.data).andLogStateChange() }
                }
                on<Event.DeclineChallenge> { _, state ->
                    state.override { State.RejectingChallenge(state.snapshot.data).andLogStateChange() }
                }
            }
            inState<State.AcceptingChallenge> {
                onEnterEffect {
                    sessionVerificationService.approveVerification()
                }
                on<Event.DidAcceptChallenge> { _, state ->
                    state.override { State.Completed.andLogStateChange() }
                }
            }
            inState<State.RejectingChallenge> {
                onEnterEffect {
                    sessionVerificationService.declineVerification()
                }
            }
            inState<State.Canceling> {
                onEnterEffect {
                    sessionVerificationService.cancelVerification()
                }
            }
            inState {
                logReceivedEvents()
                on<Event.Cancel> { _, state: MachineState<State> ->
                    when (state.snapshot) {
                        State.Completed, State.Canceled -> state.noChange()
                        else -> {
                            sessionVerificationService.cancelVerification()
                            state.override { State.Canceled.andLogStateChange() }
                        }
                    }
                }
                on<Event.DidCancel> { _, state: MachineState<State> ->
                    when (state.snapshot) {
                        is State.RejectingChallenge -> {
                            state.override { State.Failure.andLogStateChange() }
                        }
                        is State.Initial -> state.mutate { State.Initial(isCancelled = true).andLogStateChange() }
                        State.AcceptingIncomingVerification,
                        State.RejectingIncomingVerification,
                        is State.ChallengeReceived,
                        is State.AcceptingChallenge,
                        State.Canceling -> state.override { State.Canceled.andLogStateChange() }
                        State.Canceled,
                        State.Completed,
                        State.Failure -> state.noChange()
                    }
                }
                on<Event.DidFail> { _, state: MachineState<State> ->
                    state.override { State.Failure.andLogStateChange() }
                }
            }
        }
    }

    /**
     * 传入验证流程的状态集合。
     */
    sealed interface State {
        /** 初始状态，验证尚未开始。 */
        data class Initial(val isCancelled: Boolean) : State

        /** 正在接受传入的验证请求。 */
        data object AcceptingIncomingVerification : State

        /** 正在拒绝传入的验证请求。 */
        data object RejectingIncomingVerification : State

        /** 已收到验证 challenge，可供用户比对表情。 */
        data class ChallengeReceived(val data: SessionVerificationData) : State

        /** 正在确认 challenge 一致。 */
        data class AcceptingChallenge(val data: SessionVerificationData) : State

        /** 正在拒绝当前 challenge。 */
        data class RejectingChallenge(val data: SessionVerificationData) : State

        /** 正在取消验证。 */
        data object Canceling : State

        /** 验证已被本地或远端取消。 */
        data object Canceled : State

        /** 验证成功完成。 */
        data object Completed : State

        /** 验证失败。 */
        data object Failure : State

        /**
         * 判断当前状态是否仍处于等待用户决策或结果返回的处理中阶段。
         */
        fun isPending(): Boolean = when (this) {
            AcceptingIncomingVerification, RejectingIncomingVerification, Failure, is ChallengeReceived, is AcceptingChallenge, is RejectingChallenge -> true
            is Initial, Canceling, Canceled, Completed -> false
        }
    }

    /**
     * 传入验证流程中会收到的事件集合。
     */
    sealed interface Event {
        /** 用户接受传入的验证请求。 */
        data object AcceptIncomingRequest : Event

        /** 已收到比对所需的 challenge 数据。 */
        data class DidReceiveChallenge(val data: SessionVerificationData) : Event

        /** 用户确认表情一致。 */
        data object AcceptChallenge : Event

        /** 用户确认表情不一致。 */
        data object DeclineChallenge : Event

        /** 远端已确认 challenge。 */
        data object DidAcceptChallenge : Event

        /** 请求取消当前验证。 */
        data object Cancel : Event

        /** 验证已被取消。 */
        data object DidCancel : Event

        /** 当前验证流程失败。 */
        data object DidFail : Event
    }
}
