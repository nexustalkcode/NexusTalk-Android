/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rolesandpermissions.impl.roles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.rolesandpermissions.api.ChangeRoomMemberRolesListType
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.appyx.launchMolecule
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.core.bool.orFalse
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.room.RoomMember
import kotlinx.coroutines.flow.first

@ContributesNode(RoomScope::class)
@AssistedInject
/**
 * 修改成员角色页面节点。
 */
class ChangeRolesNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: ChangeRolesPresenter.Factory,
) : Node(buildContext, plugins = plugins) {
    /**
     * 节点输入。
     */
    data class Inputs(
        val listType: ChangeRoomMemberRolesListType,
    ) : NodeInputs

    private val inputs: Inputs = inputs()
    private val presenter = presenterFactory.create(inputs.listType.toRoomMemberRole())
    private val stateFlow = launchMolecule { presenter.present() }

    /**
     * 等待保存流程结束，并返回是否保存成功。
     */
    suspend fun waitForCompletion(): Boolean {
        val successState = stateFlow.first { it.savingState.isSuccess() }
        return successState.savingState.dataOrNull().orFalse()
    }

    /**
     * 渲染修改成员角色页面。
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state by stateFlow.collectAsState()
        ChangeRolesView(
            state = state,
            modifier = modifier,
        )
    }
}

/**
 * 把入口层的列表类型映射为对应房间成员角色。
 */
internal fun ChangeRoomMemberRolesListType.toRoomMemberRole() = when (this) {
    ChangeRoomMemberRolesListType.Admins -> RoomMember.Role.Admin
    ChangeRoomMemberRolesListType.Moderators -> RoomMember.Role.Moderator
    ChangeRoomMemberRolesListType.SelectNewOwnersWhenLeaving -> RoomMember.Role.Owner(isCreator = false)
}
