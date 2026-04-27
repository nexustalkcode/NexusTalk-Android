/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.transition.JumpToEndTransitionHandler
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.createroom.api.CreateRoomEntryPoint
import io.element.android.features.startchat.DefaultStartChatNavigator
import io.element.android.features.startchat.api.StartChatEntryPoint
import io.element.android.features.startchat.impl.joinbyaddress.JoinRoomByAddressNode
import io.element.android.features.startchat.impl.root.StartChatNode
import io.element.android.features.startchat.impl.scanuser.ScanUserQrCodeNode
import io.element.android.features.startchat.impl.userlist.UserListDataStore
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.OverlayView
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.architecture.overlay.operation.hide
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import kotlinx.parcelize.Parcelize

@ContributesNode(SessionScope::class)
@AssistedInject
/**
 * 开始聊天总流程节点。
 *
 * 负责协调主页面、创建房间、按地址加入和扫码加人等子流程，
 * 并通过 overlay 展示需要临时覆盖在主页面上的子页面。
 */
class StartChatFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val createRoomEntryPoint: CreateRoomEntryPoint,
    private val userListDataStore: UserListDataStore,
) : BaseFlowNode<StartChatFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.Root,
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins
) {
    /**
     * 开始聊天流程中的导航目标。
     */
    sealed interface NavTarget : Parcelable {
        @Parcelize
        data object Root : NavTarget

        @Parcelize
        data object NewRoom : NavTarget

        @Parcelize
        data object JoinByAddress : NavTarget

        @Parcelize
        data object ScanUserQrCode : NavTarget
    }

    private val callback: StartChatEntryPoint.Callback = callback()
    private val navigator = DefaultStartChatNavigator(
        backstack = backstack,
        overlay = overlay,
        openRoom = callback::onRoomCreated,
        openRoomDirectory = callback::navigateToRoomDirectory,
    )

    /**
     * 根据导航目标创建对应子节点。
     *
     * @param navTarget 当前需要解析的导航目标。
     * @param buildContext 子节点构建上下文。
     */
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.Root -> {
                createNode<StartChatNode>(buildContext = buildContext, plugins = listOf(navigator))
            }
            NavTarget.NewRoom -> {
                val callback = object : CreateRoomEntryPoint.Callback {
                    override fun onRoomCreated(roomId: RoomId) {
                        overlay.hide()
                        navigator.onRoomCreated(roomId.toRoomIdOrAlias(), emptyList())
                    }
                }
                val addPeopleCallback = object : CreateRoomEntryPoint.AddPeopleCallback {
                    override fun onAddPeopleShown(roomId: RoomId) {
                        navigator.hideStartChat()
                    }
                }
                createRoomEntryPoint.createNode(
                    isSpace = false,
                    parentNode = this,
                    buildContext = buildContext,
                    callback = callback,
                    addPeopleCallback = addPeopleCallback,
                )
            }
            NavTarget.JoinByAddress -> {
                createNode<JoinRoomByAddressNode>(buildContext = buildContext, plugins = listOf(navigator))
            }
            NavTarget.ScanUserQrCode -> {
                val callback = object : ScanUserQrCodeNode.Callback {
                    override fun onUserIdScanned(userId: String) {
                        userListDataStore.setInitialQuery(userId)
                    }
                }
                createNode<ScanUserQrCodeNode>(
                    buildContext = buildContext,
                    plugins = listOf(navigator, callback)
                )
            }
        }
    }

    /**
     * 渲染主 back stack 与 overlay。
     *
     * @param modifier 应用于根节点的修饰符。
     */
    @Composable
    override fun View(modifier: Modifier) {
        Box(modifier = modifier) {
            BackstackView()
            OverlayView(transitionHandler = remember { JumpToEndTransitionHandler() })
        }
    }
}
