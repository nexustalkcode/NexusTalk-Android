/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.replace
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.createroom.api.CreateRoomEntryPoint
import io.element.android.features.createroom.impl.addpeople.AddPeopleNode
import io.element.android.features.createroom.impl.configureroom.ConfigureRoomNode
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.core.RoomId
import kotlinx.parcelize.Parcelize

/**
 * 创建房间流程节点
 *
 * 创建房间功能的主流程节点，管理整个创建房间的导航流程。
 * 包含两个步骤：配置房间 -> 添加人员
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class CreateRoomFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
) : BaseFlowNode<CreateRoomFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.ConfigureRoom(isSpace = plugins.filterIsInstance<Inputs>().first().isSpace),
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins
) {
    /**
     * 输入数据类
     *
     * @property isSpace 是否创建为空间
     */
    @Parcelize
    data class Inputs(
        val isSpace: Boolean
    ) : NodeInputs, Parcelable

    /** 创建房间入口点的回调接口 */
    private val callback: CreateRoomEntryPoint.Callback = callback()
    private val addPeopleCallback = plugins.filterIsInstance<CreateRoomEntryPoint.AddPeopleCallback>().firstOrNull()

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            is NavTarget.ConfigureRoom -> {
                val inputs = ConfigureRoomNode.Inputs(isSpace = navTarget.isSpace)
                val callback = object : ConfigureRoomNode.Callback {
                    override fun onCreateRoomSuccess(roomId: RoomId) {
                        addPeopleCallback?.onAddPeopleShown(roomId)
                        backstack.replace(NavTarget.AddPeople(roomId))
                    }
                }
                createNode<ConfigureRoomNode>(buildContext, plugins = listOf(inputs, callback))
            }
            is NavTarget.AddPeople -> {
                val inputs = AddPeopleNode.Inputs(navTarget.roomId)
                val callback: AddPeopleNode.Callback = object : AddPeopleNode.Callback {
                    override fun onFinish() {
                        callback.onRoomCreated(navTarget.roomId)
                    }
                }
                createNode<AddPeopleNode>(buildContext, plugins = listOf(inputs, callback))
            }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        BackstackView()
    }

    /**
     * 导航目标密封接口
     *
     * 定义创建房间流程中的各个导航节点
     */
    sealed interface NavTarget : Parcelable {
        /**
         * 配置房间步骤
         *
         * @property isSpace 是否创建为空间
         */
        @Parcelize
        data class ConfigureRoom(val isSpace: Boolean) : NavTarget

        /**
         * 添加人员步骤
         *
         * @property roomId 已创建的房间 ID
         */
        @Parcelize
        data class AddPeople(val roomId: RoomId) : NavTarget
    }
}
