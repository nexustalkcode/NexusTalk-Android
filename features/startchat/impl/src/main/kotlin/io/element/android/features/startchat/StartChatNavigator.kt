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

/**
 * “开始聊天”流程对外暴露的导航接口。
 */
interface StartChatNavigator : Plugin {
    /** 表示主页面当前是否仍应可见。 */
    val isStartChatVisible: StateFlow<Boolean>

    /** 处理房间创建或解析完成后的跳转。 */
    fun onRoomCreated(roomIdOrAlias: RoomIdOrAlias, serverNames: List<String>)

    /** 打开创建房间流程。 */
    fun onCreateNewRoom()

    /** 显示“按地址加入房间”弹层。 */
    fun onShowJoinRoomByAddress()

    /** 关闭“按地址加入房间”弹层。 */
    fun onDismissJoinRoomByAddress()

    /** 打开房间目录。 */
    fun onOpenRoomDirectory()

    /** 显示扫码加人页面。 */
    fun onShowScanUserQrCode()

    /** 隐藏开始聊天主页面。 */
    fun hideStartChat()
}

/**
 * [StartChatNavigator] 的默认实现。
 *
 * 通过 back stack、overlay 和外部回调组合实现开始聊天流程的导航。
 */
class DefaultStartChatNavigator(
    private val backstack: BackStack<NavTarget>,
    private val overlay: Overlay<NavTarget>,
    private val openRoom: (RoomIdOrAlias, List<String>) -> Unit,
    private val openRoomDirectory: () -> Unit,
) : StartChatNavigator {
    private val startChatVisible = MutableStateFlow(true)

    override val isStartChatVisible: StateFlow<Boolean> = startChatVisible.asStateFlow()

    /**
     * 把已创建/已解析的房间交给外部打开。
     */
    override fun onRoomCreated(roomIdOrAlias: RoomIdOrAlias, serverNames: List<String>) =
        openRoom(roomIdOrAlias, serverNames)

    /**
     * 导航到房间目录。
     */
    override fun onOpenRoomDirectory() = openRoomDirectory()

    /**
     * 显示创建房间浮层。
     */
    override fun onCreateNewRoom() {
        overlay.show(NavTarget.NewRoom)
    }

    /**
     * 显示按地址加入房间浮层。
     */
    override fun onShowJoinRoomByAddress() {
        overlay.show(NavTarget.JoinByAddress)
    }

    /**
     * 关闭当前浮层。
     */
    override fun onDismissJoinRoomByAddress() {
        overlay.hide()
    }

    /**
     * 显示扫码加人浮层。
     */
    override fun onShowScanUserQrCode() {
        overlay.show(NavTarget.ScanUserQrCode)
    }

    /**
     * 隐藏开始聊天主页。
     */
    override fun hideStartChat() {
        startChatVisible.value = false
    }
}
