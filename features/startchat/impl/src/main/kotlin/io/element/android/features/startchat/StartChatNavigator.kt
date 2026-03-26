/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat

import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import io.element.android.features.startchat.impl.StartChatFlowNode.NavTarget
import io.element.android.libraries.architecture.overlay.Overlay
import io.element.android.libraries.architecture.overlay.operation.hide
import io.element.android.libraries.architecture.overlay.operation.show
import io.element.android.libraries.matrix.api.core.RoomIdOrAlias
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface StartChatNavigator : Plugin {
    val isStartChatVisible: StateFlow<Boolean>
    fun onRoomCreated(roomIdOrAlias: RoomIdOrAlias, serverNames: List<String>)
    fun onCreateNewRoom()
    fun onShowJoinRoomByAddress()
    fun onDismissJoinRoomByAddress()
    fun onOpenRoomDirectory()
    fun onShowScanUserQrCode()
    fun hideStartChat()
}

class DefaultStartChatNavigator(
    private val backstack: BackStack<NavTarget>,
    private val overlay: Overlay<NavTarget>,
    private val openRoom: (RoomIdOrAlias, List<String>) -> Unit,
    private val openRoomDirectory: () -> Unit,
) : StartChatNavigator {
    private val startChatVisible = MutableStateFlow(true)

    override val isStartChatVisible: StateFlow<Boolean> = startChatVisible.asStateFlow()

    override fun onRoomCreated(roomIdOrAlias: RoomIdOrAlias, serverNames: List<String>) =
        openRoom(roomIdOrAlias, serverNames)

    override fun onOpenRoomDirectory() = openRoomDirectory()

    override fun onCreateNewRoom() {
        overlay.show(NavTarget.NewRoom)
    }

    override fun onShowJoinRoomByAddress() {
        overlay.show(NavTarget.JoinByAddress)
    }

    override fun onDismissJoinRoomByAddress() {
        overlay.hide()
    }

    override fun onShowScanUserQrCode() {
        overlay.show(NavTarget.ScanUserQrCode)
    }

    override fun hideStartChat() {
        startChatVisible.value = false
    }
}
