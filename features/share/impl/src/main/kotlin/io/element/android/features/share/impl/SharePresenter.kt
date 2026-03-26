/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.share.impl

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.architecture.runCatchingUpdatingState
import io.element.android.libraries.core.bool.orFalse
import io.element.android.libraries.di.annotations.SessionCoroutineScope
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.JoinedRoom
import io.element.android.libraries.mediaupload.api.MediaOptimizationConfigProvider
import io.element.android.libraries.mediaupload.api.MediaSenderRoomFactory
import io.element.android.services.appnavstate.api.ActiveRoomsHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * 分享 Presenter
 *
 * 负责处理分享功能的业务逻辑，包括：
 * - 处理分享意图
 * - 上传媒体文件到房间
 * - 发送文本消息到房间
 * - 管理分享状态
 *
 * 使用协程进行异步操作，确保主线程流畅响应。
 *
 * @property intent 要分享的 Intent 内容
 * @property sessionCoroutineScope 会话级协程作用域
 * @property shareIntentHandler 分享意图处理器
 * @property matrixClient Matrix 客户端
 * @property mediaSenderRoomFactory 媒体发送器工厂
 * @property activeRoomsHolder 活动房间持有者
 * @property mediaOptimizationConfigProvider 媒体优化配置提供者
 * @see ShareState 分享状态
 * @see ShareIntentHandler 分享意图处理器接口
 */
/**
 * Share Presenter.
 *
 * Responsible for handling the business logic of the share feature, including:
 * - Processing share intents
 * - Uploading media files to rooms
 * - Sending text messages to rooms
 * - Managing share state
 *
 * Uses coroutines for async operations to ensure smooth main thread responsiveness.
 *
 * @property intent The Intent to be shared
 * @property sessionCoroutineScope Session-level coroutine scope
 * @property shareIntentHandler Share intent handler
 * @property matrixClient Matrix client
 * @property mediaSenderRoomFactory Media sender room factory
 * @property activeRoomsHolder Active rooms holder
 * @property mediaOptimizationConfigProvider Media optimization config provider
 * @see ShareState Share state
 * @see ShareIntentHandler Share intent handler interface
 */
@AssistedInject
class SharePresenter(
    @Assisted private val intent: Intent,
    @SessionCoroutineScope
    private val sessionCoroutineScope: CoroutineScope,
    private val shareIntentHandler: ShareIntentHandler,
    private val matrixClient: MatrixClient,
    private val mediaSenderRoomFactory: MediaSenderRoomFactory,
    private val activeRoomsHolder: ActiveRoomsHolder,
    private val mediaOptimizationConfigProvider: MediaOptimizationConfigProvider,
) : Presenter<ShareState> {
    /**
     * Presenter 工厂接口
     */
    /**
     * Presenter factory interface.
     */
    @AssistedFactory
    fun interface Factory {
        /**
         * 创建 SharePresenter 实例
         *
         * @param intent 分享意图
         * @return SharePresenter 实例
         */
        /**
         * Creates a SharePresenter instance.
         *
         * @param intent The share intent
         * @return SharePresenter instance
         */
        fun create(intent: Intent): SharePresenter
    }

    /** 分享操作状态 */
    /** The share action state */
    private val shareActionState: MutableState<AsyncAction<List<RoomId>>> = mutableStateOf(AsyncAction.Uninitialized)

    /**
     * 当房间被选中时调用
     *
     * @param roomIds 要分享到的房间 ID 列表
     */
    /**
     * Called when a room is selected.
     *
     * @param roomIds The list of room IDs to share to
     */
    fun onRoomSelected(roomIds: List<RoomId>) {
        sessionCoroutineScope.share(intent, roomIds)
    }

    /**
     * 创建视图状态
     *
     * @return ShareState 当前分享的状态
     */
    /**
     * Creates the view state.
     *
     * @return ShareState The current share state
     */
    @Composable
    override fun present(): ShareState {
        fun handleEvent(event: ShareEvents) {
            when (event) {
                ShareEvents.ClearError -> shareActionState.value = AsyncAction.Uninitialized
            }
        }

        return ShareState(
            shareAction = shareActionState.value,
            eventSink = ::handleEvent,
        )
    }

    /**
     * 获取已加入的房间
     *
     * @param roomId 房间 ID
     * @return JoinedRoom 实例，如果房间不存在则返回 null
     */
    /**
     * Gets a joined room.
     *
     * @param roomId The room ID
     * @return JoinedRoom instance, or null if the room doesn't exist
     */
    private suspend fun getJoinedRoom(roomId: RoomId): JoinedRoom? {
        return activeRoomsHolder.getActiveRoom(matrixClient.sessionId)
            ?.takeIf { it.roomId == roomId }
            ?: matrixClient.getJoinedRoom(roomId)
    }

    /**
     * 执行分享操作
     *
     * @param intent 分享意图
     * @param roomIds 目标房间 ID 列表
     */
    /**
     * Executes the share operation.
     *
     * @param intent The share intent
     * @param roomIds The target room ID list
     */
    private fun CoroutineScope.share(
        intent: Intent,
        roomIds: List<RoomId>,
    ) = launch {
        suspend {
            val result = shareIntentHandler.handleIncomingShareIntent(
                intent,
                onUris = { filesToShare ->
                    if (filesToShare.isEmpty()) {
                        false
                    } else {
                        roomIds
                            .map { roomId ->
                                val room = getJoinedRoom(roomId) ?: return@map false
                                val mediaSender = mediaSenderRoomFactory.create(room = room)
                                filesToShare
                                    .map { fileToShare ->
                                        val result = mediaSender.sendMedia(
                                            uri = fileToShare.uri,
                                            mimeType = fileToShare.mimeType,
                                            mediaOptimizationConfig = mediaOptimizationConfigProvider.get(),
                                        )
                                        // If the coroutine was cancelled, destroy the room and rethrow the exception
                                        val cancellationException = result.exceptionOrNull() as? CancellationException
                                        if (cancellationException != null) {
                                            if (activeRoomsHolder.getActiveRoomMatching(matrixClient.sessionId, roomId) == null) {
                                                room.destroy()
                                            }
                                            throw cancellationException
                                        }
                                        result.isSuccess
                                    }
                                    .all { isSuccess -> isSuccess }
                                    .also {
                                        if (activeRoomsHolder.getActiveRoomMatching(matrixClient.sessionId, roomId) == null) {
                                            room.destroy()
                                        }
                                    }
                            }
                            .all { it }
                    }
                },
                onPlainText = { text ->
                    roomIds
                        .map { roomId ->
                            getJoinedRoom(roomId)?.liveTimeline?.sendMessage(
                                body = text,
                                htmlBody = null,
                                intentionalMentions = emptyList(),
                            )?.isSuccess.orFalse()
                        }
                        .all { it }
                }
            )
            if (!result) {
                error("Failed to handle incoming share intent")
            }
            roomIds
        }.runCatchingUpdatingState(shareActionState)
    }
}
