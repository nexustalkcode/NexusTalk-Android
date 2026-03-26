/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.share.impl

import android.content.Intent
import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.model.permanent.PermanentNavModel
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.share.api.ShareEntryPoint
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.roomselect.api.RoomSelectEntryPoint
import io.element.android.libraries.roomselect.api.RoomSelectMode
import kotlinx.parcelize.Parcelize

/**
 * 分享节点
 *
 * 作为分享功能的父节点，管理房间选择和分享操作的导航。
 * 使用 PermanentNavModel 保持节点持久化，支持返回时恢复状态。
 *
 * @property NavTarget 导航目标类型
 * @property Inputs 输入数据类，包含分享意图
 * @see SharePresenter 分享 Presenter
 * @see ShareView 分享视图
 */
/**
 * Share node.
 *
 * Acts as the parent node for the share feature, managing navigation for room selection and share operations.
 * Uses PermanentNavModel to keep the node persistent, supporting state restoration when returning.
 *
 * @property NavTarget Navigation target type
 * @property Inputs Input data class containing the share intent
 * @see SharePresenter Share Presenter
 * @see ShareView Share view
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class ShareNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: SharePresenter.Factory,
    private val roomSelectEntryPoint: RoomSelectEntryPoint,
) : ParentNode<ShareNode.NavTarget>(
    navModel = PermanentNavModel(
        navTargets = setOf(NavTarget),
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
) {
    /** 导航目标 */
    /** Navigation target */
    @Parcelize
    object NavTarget : Parcelable

    /**
     * 节点输入数据
     *
     * @property intent 分享意图
     */
    /**
     * Node input data.
     *
     * @property intent The share intent
     */
    data class Inputs(val intent: Intent) : NodeInputs

    private val inputs = inputs<Inputs>()
    private val presenter = presenterFactory.create(inputs.intent)
    private val callback: ShareEntryPoint.Callback = callback()

    /**
     * 解析导航目标并创建对应的节点
     *
     * @param navTarget 导航目标
     * @param buildContext 构建上下文
     * @return 解析后的节点实例
     */
    /**
     * Resolves the navigation target and creates the corresponding node.
     *
     * @param navTarget The navigation target
     * @param buildContext The build context
     * @return The resolved node instance
     */
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        val callback = object : RoomSelectEntryPoint.Callback {
            override fun onRoomSelected(roomIds: List<RoomId>) {
                presenter.onRoomSelected(roomIds)
            }

            override fun onCancel() {
                callback.onDone(emptyList())
            }
        }

        return roomSelectEntryPoint.createNode(
            parentNode = this,
            buildContext = buildContext,
            params = RoomSelectEntryPoint.Params(mode = RoomSelectMode.Share),
            callback = callback,
        )
    }

    /**
     * 渲染分享节点的视图
     *
     * @param modifier 修饰符
     */
    /**
     * Renders the view of the share node.
     *
     * @param modifier The modifier
     */
    @Composable
    override fun View(modifier: Modifier) {
        Box(modifier = modifier) {
            // Will render to room select screen
            Children(
                navModel = navModel,
            )

            val state = presenter.present()
            ShareView(
                state = state,
                onShareSuccess = callback::onDone,
            )
        }
    }
}
