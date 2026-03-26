/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:Suppress("WildcardImport")
@file:OptIn(ExperimentalCoroutinesApi::class)

package io.element.android.features.securebackup.impl.setup

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.freeletics.flowredux.dsl.State as MachineState

/**
 * 安全备份设置状态机
 *
 * 使用 FlowRedux 库实现的安全备份设置流程状态机。
 * 管理从初始状态到创建恢复密钥、用户保存密钥、错误处理等流程。
 */
@Inject
class SecureBackupSetupStateMachine : FlowReduxStateMachine<SecureBackupSetupStateMachine.State, SecureBackupSetupStateMachine.Event>(
    initialState = State.Initial
) {
    init {
        spec {
            inState<State.Initial> {
                on { _: Event.UserCreatesKey, state: MachineState<State.Initial> ->
                    state.override { State.CreatingKey }
                }
            }
            inState<State.CreatingKey> {
                on { event: Event.SdkError, state: MachineState<State.CreatingKey> ->
                    state.override { State.Error(event.exception) }
                }
                on { event: Event.SdkHasCreatedKey, state: MachineState<State.CreatingKey> ->
                    state.override { State.KeyCreated(event.key) }
                }
            }
            inState<State.KeyCreated> {
                on { _: Event.UserSavedKey, state: MachineState<State.KeyCreated> ->
                    state.override { State.KeyCreatedAndSaved(state.snapshot.key) }
                }
            }
            inState<State.Error> {
                on { _: Event.ClearError, state: MachineState<State.Error> ->
                    state.override { State.Initial }
                }
            }
            inState<State.KeyCreatedAndSaved> {
            }
        }
    }

    /**
     * 状态机状态密封接口
     */
    sealed interface State {
        /** 初始状态 */
        data object Initial : State

        /** 正在创建密钥状态 */
        data object CreatingKey : State

        /** 密钥已创建状态，包含密钥内容 */
        data class KeyCreated(val key: String) : State

        /** 密钥已创建并保存状态 */
        data class KeyCreatedAndSaved(val key: String) : State

        /** 错误状态 */
        data class Error(val exception: Exception) : State
    }

    /**
     * 状态机事件密封接口
     */
    sealed interface Event {
        /** 用户创建密钥事件 */
        data object UserCreatesKey : Event

        /** SDK 已创建密钥事件 */
        data class SdkHasCreatedKey(val key: String) : Event

        /** SDK 错误事件 */
        data class SdkError(val exception: Exception) : Event

        /** 用户已保存密钥事件 */
        data object UserSavedKey : Event

        /** 清除错误事件 */
        data object ClearError : Event
    }
}
