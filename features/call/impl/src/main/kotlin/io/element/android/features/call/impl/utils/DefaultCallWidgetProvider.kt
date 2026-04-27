/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.utils

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.matrix.api.MatrixClientProvider
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.room.isDm
import io.element.android.libraries.matrix.api.widget.CallWidgetMode
import io.element.android.libraries.matrix.api.widget.CallWidgetSettingsProvider
import io.element.android.libraries.preferences.api.store.AppPreferencesStore
import io.element.android.services.appnavstate.api.ActiveRoomsHolder
import kotlinx.coroutines.flow.firstOrNull

/** 嵌入式通话小组件的基础 URL */
private const val EMBEDDED_CALL_WIDGET_BASE_URL = "https://appassets.androidplatform.net/element-call/index.html"

/**
 * 通话小组件提供者默认实现
 *
 * 实现 CallWidgetProvider 接口，提供从 Matrix 房间获取通话小组件的功能。
 * 支持自定义 Element Call 基础 URL（通过用户偏好设置），或使用嵌入式版本。
 *
 * @param matrixClientsProvider Matrix 客户端提供者
 * @param appPreferencesStore 应用偏好设置存储
 * @param callWidgetSettingsProvider 通话小组件设置提供者
 * @param activeRoomsHolder 活动房间持有者
 *
 * @see CallWidgetProvider 通话小组件提供者接口
 * @see EMBEDDED_CALL_WIDGET_BASE_URL 嵌入式版本基础 URL
 */
@ContributesBinding(AppScope::class)
class DefaultCallWidgetProvider(
    private val matrixClientsProvider: MatrixClientProvider,
    private val appPreferencesStore: AppPreferencesStore,
    private val callWidgetSettingsProvider: CallWidgetSettingsProvider,
    private val activeRoomsHolder: ActiveRoomsHolder,
) : CallWidgetProvider {
    /**
     * 获取通话小组件
     *
     * 从 Matrix 房间获取 Element Call 小组件，包括 Widget Driver 和呼叫 URL。
     *
     * @param sessionId 会话 ID
     * @param roomId 房间 ID
     * @param clientId 客户端 ID
     * @param languageTag 语言标签（可选）
     * @param theme 主题（可选）
     * @return Result<GetWidgetResult> 包含 Widget Driver 和 URL 的结果
     */
    override suspend fun getWidget(
        sessionId: SessionId,
        roomId: RoomId,
        clientId: String,
        languageTag: String?,
        theme: String?,
        callMode: CallWidgetMode,
    ): Result<CallWidgetProvider.GetWidgetResult> = runCatchingExceptions {
        val matrixClient = matrixClientsProvider.getOrRestore(sessionId).getOrThrow()
        val room = activeRoomsHolder.getActiveRoomMatching(sessionId, roomId)
            ?: matrixClient.getJoinedRoom(roomId)
            ?: error("Room not found")

        val customBaseUrl = appPreferencesStore.getCustomElementCallBaseUrlFlow().firstOrNull()
        val baseUrl = customBaseUrl ?: EMBEDDED_CALL_WIDGET_BASE_URL

        val roomInfo = room.info()
        val isEncrypted = roomInfo.isEncrypted ?: room.getUpdatedIsEncrypted().getOrThrow()
        val widgetSettings = callWidgetSettingsProvider.provide(
            baseUrl = baseUrl,
            encrypted = isEncrypted,
            direct = room.isDm(),
            hasActiveCall = roomInfo.hasRoomCall,
            callMode = callMode,
        )
        val callUrl = room.generateWidgetWebViewUrl(
            widgetSettings = widgetSettings,
            clientId = clientId,
            languageTag = languageTag,
            theme = theme,
        ).getOrThrow()

        val driver = room.getWidgetDriver(widgetSettings).getOrThrow()

        CallWidgetProvider.GetWidgetResult(
            driver = driver,
            url = callUrl,
        )
    }
}
