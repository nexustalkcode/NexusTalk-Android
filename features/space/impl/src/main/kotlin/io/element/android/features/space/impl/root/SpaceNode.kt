/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.space.impl.root

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteView
import io.element.android.features.space.impl.di.SpaceFlowScope
import io.element.android.libraries.androidutils.R
import io.element.android.libraries.androidutils.system.startSharePlainTextIntent
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.spaces.SpaceRoomList
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.coroutines.launch
import timber.log.Timber

@ContributesNode(SpaceFlowScope::class)
@AssistedInject
/**
 * Space 主页节点。
 *
 * 负责连接 [SpacePresenter] 与 [SpaceView]，并把分享、进入房间、打开设置、
 * 查看成员和离开空间等操作桥接给上层流程。
 */
class SpaceNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    private val presenter: SpacePresenter,
    private val matrixClient: MatrixClient,
    private val spaceRoomList: SpaceRoomList,
    private val acceptDeclineInviteView: AcceptDeclineInviteView,
) : Node(buildContext, plugins = plugins) {
    /**
     * Space 页面向上抛出的导航回调。
     */
    interface Callback : Plugin {
        fun navigateToRoom(roomId: RoomId, viaParameters: List<String>)
        fun navigateToSpaceSettings()
        fun navigateToRoomMemberList()
        fun startLeaveSpaceFlow()
        fun navigateToAddRoom()
    }

    private val callback: Callback = callback()

    /**
     * 分享当前 Space 的 permalink。
     *
     * @param context 当前上下文，用于拉起系统分享面板。
     */
    private fun onShareRoom(context: Context) = lifecycleScope.launch {
        matrixClient.getRoom(spaceRoomList.roomId)?.use { room ->
            room.getPermalink()
                .onSuccess { permalink ->
                    context.startSharePlainTextIntent(
                        activityResultLauncher = null,
                        chooserTitle = context.getString(CommonStrings.common_share_space),
                        text = permalink,
                        noActivityFoundMessage = context.getString(R.string.error_no_compatible_app_found)
                    )
                }
                .onFailure {
                    Timber.e(it)
                }
        }
    }

    /**
     * 渲染 Space 主页。
     *
     * @param modifier 应用于页面根节点的修饰符。
     */
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        val context = LocalContext.current
        SpaceView(
            state = state,
            onBackClick = ::navigateUp,
            onLeaveSpaceClick = {
                callback.startLeaveSpaceFlow()
            },
            onRoomClick = { spaceRoom ->
                callback.navigateToRoom(spaceRoom.roomId, spaceRoom.via)
            },
            onSettingsClick = {
                callback.navigateToSpaceSettings()
            },
            onShareSpace = {
                onShareRoom(context)
            },
            onViewMembersClick = {
                callback.navigateToRoomMemberList()
            },
            onAddRoomClick = {
                callback.navigateToAddRoom()
            },
            acceptDeclineInviteView = {
                acceptDeclineInviteView.Render(
                    state = state.acceptDeclineInviteState,
                    onAcceptInviteSuccess = { roomId ->
                        callback.navigateToRoom(roomId, emptyList())
                    },
                    onDeclineInviteSuccess = { roomId ->
                        // No action needed
                    },
                    modifier = Modifier
                )
            },
            modifier = modifier
        )
    }
}
