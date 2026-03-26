/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl

import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.securityandprivacy.api.SecurityAndPrivacyEntryPoint
import io.element.android.features.securityandprivacy.api.securityAndPrivacyPermissions
import io.element.android.features.securityandprivacy.impl.editroomaddress.EditRoomAddressNode
import io.element.android.features.securityandprivacy.impl.manageauthorizedspaces.ManageAuthorizedSpacesNode
import io.element.android.features.securityandprivacy.impl.root.SecurityAndPrivacyNode
import io.element.android.libraries.architecture.BackstackView
import io.element.android.libraries.architecture.BaseFlowNode
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.room.powerlevels.use
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * 安全与隐私流程节点
 *
 * 安全与隐私功能的主流程节点，继承自 BaseFlowNode。
 * 管理整个安全与隐私设置的导航流程，包括：
 * - 主安全与隐私设置页面
 * - 编辑房间地址页面
 * - 管理授权空间页面
 *
 * @property room 已加入的房间，用于获取和修改房间的安全与隐私设置
 * @see BaseFlowNode 基础流程节点
 * @see NavTarget 导航目标
 */
@ContributesNode(RoomScope::class)
@AssistedInject
class SecurityAndPrivacyFlowNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    /** 已加入的房间，用于获取和修改房间设置 */
    private val room: JoinedRoom,
) : BaseFlowNode<SecurityAndPrivacyFlowNode.NavTarget>(
    backstack = BackStack(
        initialElement = NavTarget.SecurityAndPrivacy,
        savedStateMap = buildContext.savedStateMap,
    ),
    buildContext = buildContext,
    plugins = plugins,
) {
    /**
     * 导航目标密封接口
     *
     * 定义安全与隐私流程中可能的导航目标。
     */
    sealed interface NavTarget : Parcelable {
        /** 主安全与隐私设置页面 */
        @Parcelize
        data object SecurityAndPrivacy : NavTarget

        /** 编辑房间地址页面 */
        @Parcelize
        data object EditRoomAddress : NavTarget

        /** 管理授权空间页面 */
        @Parcelize
        data object ManageAuthorizedSpaces : NavTarget
    }

    private val callback: SecurityAndPrivacyEntryPoint.Callback = callback()

    @VisibleForTesting
    val navigator = BackstackSecurityAndPrivacyNavigator(callback, backstack)

    override fun onBuilt() {
        super.onBuilt()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                room.roomInfoFlow
                    .map { roomInfo ->
                        room.roomPermissions().use(false) { perms ->
                            perms.securityAndPrivacyPermissions().hasAny(roomInfo.isSpace, roomInfo.joinRule)
                        }
                    }
                    .filter { canEdit -> !canEdit }
                    .first()
                // If the user can no longer edit security and privacy, exit the flow
                callback.onDone()
            }
        }
    }

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.SecurityAndPrivacy -> {
                createNode<SecurityAndPrivacyNode>(buildContext, plugins = listOf(navigator))
            }
            NavTarget.EditRoomAddress -> {
                createNode<EditRoomAddressNode>(buildContext, plugins = listOf(navigator))
            }
            NavTarget.ManageAuthorizedSpaces -> {
                createNode<ManageAuthorizedSpacesNode>(buildContext, plugins = listOf(navigator))
            }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        BackstackView(modifier)
    }
}
