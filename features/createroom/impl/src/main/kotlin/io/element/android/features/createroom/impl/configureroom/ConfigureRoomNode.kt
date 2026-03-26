/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl.configureroom

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bumble.appyx.core.lifecycle.subscribe
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.MobileScreen
import io.element.android.annotations.ContributesNode
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.callback
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.services.analytics.api.AnalyticsService
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

/**
 * 配置房间节点
 *
 * 创建房间流程中"配置房间"步骤的节点。
 * 负责展示房间配置界面，包括房间名称、主题、头像、可见性等设置。
 *
 * @property buildContext 构建上下文
 * @property plugins 插件列表
 * @property presenterFactory 配置房间 Presenter 工厂
 * @property analyticsService 分析服务，用于记录用户行为
 */
@ContributesNode(SessionScope::class)
@AssistedInject
class ConfigureRoomNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
    presenterFactory: ConfigureRoomPresenter.Factory,
    private val analyticsService: AnalyticsService,
) : Node(buildContext, plugins = plugins) {
    /**
     * 回调接口
     *
     * 定义房间创建成功后的回调方法
     */
    interface Callback : Plugin {
        /**
         * 房间创建成功回调
         *
         * @param roomId 创建的房间 ID
         */
        fun onCreateRoomSuccess(roomId: RoomId)
    }

    /**
     * 输入数据类
     *
     * @property isSpace 是否创建为空间
     */
    @Parcelize
    data class Inputs(
        val isSpace: Boolean,
    ) : NodeInputs, Parcelable

    /** 输入参数 */
    private val inputs = inputs<Inputs>()

    /** 配置房间 Presenter */
    private val presenter = presenterFactory.create(inputs.isSpace)

    init {
        lifecycle.subscribe(
            onResume = {
                analyticsService.screen(MobileScreen(screenName = MobileScreen.ScreenName.CreateRoom))
            }
        )
    }

    private val callback: Callback = callback()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun View(modifier: Modifier) {
        val state = presenter.present()
        val coroutineScope = rememberCoroutineScope()
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        fun onDismiss() {
            coroutineScope.launch {
                sheetState.hide()
                navigateUp()
            }
        }

        fun onCreateRoomSuccess(roomId: RoomId) {
            coroutineScope.launch {
                sheetState.hide()
                callback.onCreateRoomSuccess(roomId)
            }
        }

        ModalBottomSheet(
            onDismissRequest = ::onDismiss,
            sheetState = sheetState,
        ) {
            ConfigureRoomView(
                state = state,
                modifier = modifier,
                onBackClick = ::onDismiss,
                onCreateRoomSuccess = ::onCreateRoomSuccess,
            )
        }
    }
}
