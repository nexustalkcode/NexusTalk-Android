/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.location.impl.send

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import im.vector.app.features.analytics.plan.Composer
import io.element.android.features.location.impl.common.MapDefaults
import io.element.android.features.location.impl.common.actions.LocationActions
import io.element.android.features.location.impl.common.permissions.PermissionsEvents
import io.element.android.features.location.impl.common.permissions.PermissionsPresenter
import io.element.android.features.location.impl.common.permissions.PermissionsState
import io.element.android.features.messages.api.MessageComposerContext
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.core.extensions.flatMap
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.matrix.api.room.CreateTimelineParams
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.matrix.api.room.location.AssetType
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.textcomposer.model.MessageComposerMode
import io.element.android.services.analytics.api.AnalyticsService
import kotlinx.coroutines.launch

/**
 * 发送位置 Presenter
 *
 * 负责处理发送位置页面的业务逻辑，包括：
 * - 管理位置权限状态
 * - 切换发送位置模式（发送者位置或标记位置）
 * - 发送位置消息
 * - 跟踪分析事件
 *
 * @property permissionsPresenterFactory 权限 Presenter 工厂
 * @property room 已加入的房间
 * @property timelineMode 时间线模式
 * @property analyticsService 分析服务
 * @property messageComposerContext 消息撰写器上下文
 * @property locationActions 位置操作
 * @property buildMeta 构建元信息
 * @see SendLocationState 发送位置状态
 */
@AssistedInject
class SendLocationPresenter(
    /** 权限 Presenter 工厂 */
    permissionsPresenterFactory: PermissionsPresenter.Factory,
    /** 已加入的房间 */
    private val room: JoinedRoom,
    /** 时间线模式 */
    @Assisted private val timelineMode: Timeline.Mode,
    /** 分析服务 */
    private val analyticsService: AnalyticsService,
    /** 消息撰写器上下文 */
    private val messageComposerContext: MessageComposerContext,
    /** 位置操作 */
    private val locationActions: LocationActions,
    /** 构建元信息 */
    private val buildMeta: BuildMeta,
) : Presenter<SendLocationState> {
    /**
     * 工厂接口，用于创建 SendLocationPresenter 实例
     */
    @AssistedFactory
    fun interface Factory {
        /**
         * 创建 SendLocationPresenter 实例
         *
         * @param timelineMode 时间线模式
         * @return SendLocationPresenter 发送位置Presenter实例
         */
        fun create(timelineMode: Timeline.Mode): SendLocationPresenter
    }

    /** 权限Presenter实例 */
    private val permissionsPresenter = permissionsPresenterFactory.create(MapDefaults.permissions)

    @Composable
    override fun present(): SendLocationState {
        val permissionsState: PermissionsState = permissionsPresenter.present()
        var mode: SendLocationState.Mode by remember {
            mutableStateOf(
                if (permissionsState.isAnyGranted) {
                    SendLocationState.Mode.SenderLocation
                } else {
                    SendLocationState.Mode.PinLocation
                }
            )
        }
        val appName by remember { derivedStateOf { buildMeta.applicationName } }
        var permissionDialog: SendLocationState.Dialog by remember {
            mutableStateOf(SendLocationState.Dialog.None)
        }
        val scope = rememberCoroutineScope()

        LaunchedEffect(permissionsState.permissions) {
            if (permissionsState.isAnyGranted) {
                mode = SendLocationState.Mode.SenderLocation
                permissionDialog = SendLocationState.Dialog.None
            }
        }

        fun handleEvent(event: SendLocationEvents) {
            when (event) {
                is SendLocationEvents.SendLocation -> scope.launch {
                    sendLocation(event, mode)
                }
                SendLocationEvents.SwitchToMyLocationMode -> when {
                    permissionsState.isAnyGranted -> mode = SendLocationState.Mode.SenderLocation
                    permissionsState.shouldShowRationale -> permissionDialog = SendLocationState.Dialog.PermissionRationale
                    else -> permissionDialog = SendLocationState.Dialog.PermissionDenied
                }
                SendLocationEvents.SwitchToPinLocationMode -> mode = SendLocationState.Mode.PinLocation
                SendLocationEvents.DismissDialog -> permissionDialog = SendLocationState.Dialog.None
                SendLocationEvents.OpenAppSettings -> {
                    locationActions.openSettings()
                    permissionDialog = SendLocationState.Dialog.None
                }
                SendLocationEvents.RequestPermissions -> permissionsState.eventSink(PermissionsEvents.RequestPermissions)
            }
        }

        return SendLocationState(
            permissionDialog = permissionDialog,
            mode = mode,
            hasLocationPermission = permissionsState.isAnyGranted,
            appName = appName,
            eventSink = ::handleEvent,
        )
    }

    private suspend fun sendLocation(
        event: SendLocationEvents.SendLocation,
        mode: SendLocationState.Mode,
    ) {
        val replyMode = messageComposerContext.composerMode as? MessageComposerMode.Reply
        val inReplyToEventId = replyMode?.eventId
        when (mode) {
            SendLocationState.Mode.PinLocation -> {
                val geoUri = event.cameraPosition.toGeoUri()
                getTimeline().flatMap {
                    it.sendLocation(
                        body = generateBody(geoUri),
                        geoUri = geoUri,
                        description = null,
                        zoomLevel = MapDefaults.DEFAULT_ZOOM.toInt(),
                        assetType = AssetType.PIN,
                        inReplyToEventId = inReplyToEventId,
                    )
                }
                analyticsService.capture(
                    Composer(
                        inThread = messageComposerContext.composerMode.inThread,
                        isEditing = messageComposerContext.composerMode.isEditing,
                        isReply = messageComposerContext.composerMode.isReply,
                        messageType = Composer.MessageType.LocationPin,
                    )
                )
            }
            SendLocationState.Mode.SenderLocation -> {
                val geoUri = event.toGeoUri()
                getTimeline().flatMap {
                    it.sendLocation(
                        body = generateBody(geoUri),
                        geoUri = geoUri,
                        description = null,
                        zoomLevel = MapDefaults.DEFAULT_ZOOM.toInt(),
                        assetType = AssetType.SENDER,
                        inReplyToEventId = inReplyToEventId,
                    )
                }
                analyticsService.capture(
                    Composer(
                        inThread = messageComposerContext.composerMode.inThread,
                        isEditing = messageComposerContext.composerMode.isEditing,
                        isReply = messageComposerContext.composerMode.isReply,
                        messageType = Composer.MessageType.LocationUser,
                    )
                )
            }
        }
    }

    private suspend fun getTimeline(): Result<Timeline> {
        return when (timelineMode) {
            is Timeline.Mode.Thread -> room.createTimeline(CreateTimelineParams.Threaded(timelineMode.threadRootId))
            else -> Result.success(room.liveTimeline)
        }
    }
}

private fun SendLocationEvents.SendLocation.toGeoUri(): String = location?.toGeoUri() ?: cameraPosition.toGeoUri()

private fun SendLocationEvents.SendLocation.CameraPosition.toGeoUri(): String = "geo:$lat,$lon"

private fun generateBody(uri: String): String = "Location was shared at $uri"
