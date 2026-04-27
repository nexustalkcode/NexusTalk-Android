/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.joinbyaddress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.features.startchat.StartChatNavigator
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.data.tryOrNull
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.element.android.libraries.matrix.api.room.alias.RoomAliasHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

private const val ADDRESS_RESOLVE_TIMEOUT_IN_SECONDS = 10

@AssistedInject
/**
 * “按地址加入房间”页面 Presenter。
 *
 * 负责处理地址输入、别名解析、校验结果展示以及成功后的导航跳转。
 */
class JoinRoomByAddressPresenter(
    @Assisted private val navigator: StartChatNavigator,
    private val client: MatrixClient,
    private val roomAliasHelper: RoomAliasHelper,
) : Presenter<JoinRoomByAddressState> {
    /**
     * 创建 Presenter 的 Assisted 工厂。
     */
    @AssistedFactory
    interface Factory {
        fun create(navigator: StartChatNavigator): JoinRoomByAddressPresenter
    }

    /**
     * 生成页面状态并处理用户事件。
     */
    @Composable
    override fun present(): JoinRoomByAddressState {
        var address by remember { mutableStateOf("") }
        var internalAddressState by remember { mutableStateOf<RoomAddressState>(RoomAddressState.Unknown) }
        var validateAddress: Boolean by remember { mutableStateOf(false) }

        fun handleEvent(event: JoinRoomByAddressEvent) {
            when (event) {
                JoinRoomByAddressEvent.Continue -> {
                    when (val currentState = internalAddressState) {
                        is RoomAddressState.RoomFound -> onRoomFound(currentState)
                        else -> validateAddress = true
                    }
                }
                JoinRoomByAddressEvent.Dismiss -> navigator.onDismissJoinRoomByAddress()
                is JoinRoomByAddressEvent.UpdateAddress -> {
                    validateAddress = false
                    address = event.address.trim()
                }
            }
        }

        RoomAddressStateEffect(
            fullAddress = address,
            onRoomAddressStateChange = { addressState ->
                internalAddressState = addressState
                if (addressState is RoomAddressState.RoomFound && validateAddress) {
                    onRoomFound(addressState)
                }
            }
        )

        val addressState by remember {
            derivedStateOf {
                // We only want to show the "RoomFound" state as long as the user didn't validate the address.
                if (validateAddress || internalAddressState is RoomAddressState.RoomFound) {
                    internalAddressState
                } else {
                    RoomAddressState.Unknown
                }
            }
        }

        return JoinRoomByAddressState(
            address = address,
            addressState = addressState,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 当地址成功解析到房间后，关闭浮层并交给导航器打开房间。
     *
     * @param state 当前解析成功的房间状态。
     */
    private fun onRoomFound(state: RoomAddressState.RoomFound) {
        navigator.onDismissJoinRoomByAddress()
        navigator.onRoomCreated(
            roomIdOrAlias = state.resolved.roomId.toRoomIdOrAlias(),
            serverNames = state.resolved.servers
        )
    }

    /**
     * 监听地址变化并异步解析房间别名。
     *
     * @param fullAddress 用户当前输入的完整地址。
     * @param onRoomAddressStateChange 用于回写地址解析状态的回调。
     */
    @Composable
    private fun RoomAddressStateEffect(
        fullAddress: String,
        onRoomAddressStateChange: (RoomAddressState) -> Unit,
    ) {
        val onChange by rememberUpdatedState(onRoomAddressStateChange)
        LaunchedEffect(fullAddress) {
            // Whenever the address changes, reset the state to unknown
            onChange(RoomAddressState.Unknown)
            // debounce the room address resolution
            delay(300)
            val roomAlias = tryOrNull { RoomAlias(fullAddress) }
            if (roomAlias != null) {
                onChange(RoomAddressState.Resolving)
                onChange(client.resolveRoomAddress(roomAlias))
            } else {
                onChange(RoomAddressState.Invalid)
            }
        }
    }

    /**
     * 调用 Matrix 层解析房间别名，并在超时后返回“未找到”状态。
     *
     * @param roomAlias 需要解析的房间别名。
     */
    private suspend fun MatrixClient.resolveRoomAddress(roomAlias: RoomAlias): RoomAddressState {
        return withTimeoutOrNull(ADDRESS_RESOLVE_TIMEOUT_IN_SECONDS.seconds) {
            resolveRoomAlias(roomAlias)
                .fold(
                    onSuccess = { resolved ->
                        if (resolved.isPresent) {
                            RoomAddressState.RoomFound(resolved.get())
                        } else {
                            roomAlias.toInvalidOrNotFound()
                        }
                    },
                    onFailure = { _ ->
                        roomAlias.toInvalidOrNotFound()
                    }
                )
        } ?: RoomAddressState.RoomNotFound
    }

    /**
     * 根据别名格式是否有效，把解析失败区分为“无效地址”或“房间不存在”。
     */
    private fun RoomAlias.toInvalidOrNotFound(): RoomAddressState {
        return if (roomAliasHelper.isRoomAliasValid(this)) {
            RoomAddressState.RoomNotFound
        } else {
            RoomAddressState.Invalid
        }
    }
}
