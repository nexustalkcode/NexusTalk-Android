/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.node
import com.bumble.appyx.testing.junit4.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.element.android.features.createroom.api.CreateRoomEntryPoint
import io.element.android.features.startchat.api.StartChatEntryPoint
import io.element.android.features.startchat.impl.StartChatFlowNode.NavTarget
import io.element.android.features.startchat.impl.userlist.UserListDataStore
import io.element.android.libraries.architecture.overlay.operation.show
import io.element.android.libraries.matrix.api.core.RoomIdOrAlias
import io.element.android.libraries.matrix.test.A_ROOM_ID
import org.junit.Rule
import org.junit.Test

class StartChatFlowNodeTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when room is created from new room flow then new room overlay is dismissed before opening room`() {
        val createRoomEntryPoint = FakeCreateRoomEntryPoint()
        val callback = FakeStartChatCallback()
        val flowNode = StartChatFlowNode(
            buildContext = BuildContext.root(savedStateMap = null),
            plugins = listOf(callback),
            createRoomEntryPoint = createRoomEntryPoint,
            userListDataStore = UserListDataStore(),
        )

        flowNode.overlay.show(NavTarget.NewRoom)
        flowNode.resolve(NavTarget.NewRoom, BuildContext.root(savedStateMap = null))

        createRoomEntryPoint.callback?.onRoomCreated(A_ROOM_ID)

        assertThat(flowNode.overlay.elements.value).isEmpty()
        assertThat(callback.openedRoomIdOrAlias).isEqualTo(RoomIdOrAlias.Id(A_ROOM_ID))
    }

    private class FakeCreateRoomEntryPoint : CreateRoomEntryPoint {
        var callback: CreateRoomEntryPoint.Callback? = null

        override fun createNode(
            isSpace: Boolean,
            parentNode: Node,
            buildContext: BuildContext,
            callback: CreateRoomEntryPoint.Callback,
            addPeopleCallback: CreateRoomEntryPoint.AddPeopleCallback?,
        ): Node {
            this.callback = callback
            return node(buildContext) {}
        }
    }

    private class FakeStartChatCallback : StartChatEntryPoint.Callback {
        var openedRoomIdOrAlias: RoomIdOrAlias? = null

        override fun onRoomCreated(roomIdOrAlias: RoomIdOrAlias, serverNames: List<String>) {
            openedRoomIdOrAlias = roomIdOrAlias
        }

        override fun navigateToRoomDirectory() = Unit
    }
}
