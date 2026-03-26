/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.impl.spaces

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.features.announcement.api.Announcement
import io.element.android.features.announcement.impl.store.AnnouncementStatus
import io.element.android.features.announcement.impl.store.AnnouncementStore
import io.element.android.libraries.architecture.Presenter
import kotlinx.coroutines.launch

/**
 * 空间公告 Presenter
 *
 * 负责处理空间公告界面的业务逻辑和状态管理。
 * 管理公告的显示状态和用户交互，将用户的操作转化为状态更新。
 * 当用户点击"继续"按钮时，会将空间公告标记为已显示状态。
 *
 * @property announcementStore 公告存储，用于持久化公告状态
 * @see SpaceAnnouncementStore 公告存储接口
 * @see SpaceAnnouncementState 空间公告状态数据类
 * @see SpaceAnnouncementEvents 空间公告事件 sealed 接口
 */
@Inject
class SpaceAnnouncementPresenter(
    /** 公告存储，用于持久化公告状态 */
    private val announcementStore: AnnouncementStore,
) : Presenter<SpaceAnnouncementState> {
    /**
     * 生成界面状态
     *
     * 创建并返回空间公告的界面状态，包含事件处理函数。
     * 事件处理函数在协程作用域内执行，确保异步操作的正确处理。
     *
     * @return SpaceAnnouncementState 空间公告状态，包含事件处理函数
     * @see SpaceAnnouncementState 空间公告状态数据类
     * @see SpaceAnnouncementEvents 空间公告事件 sealed 接口
     */
    @Composable
    override fun present(): SpaceAnnouncementState {
        val localCoroutineScope = rememberCoroutineScope()

        /**
         * 处理用户事件
         *
         * 根据事件类型执行相应的业务逻辑。
         * Continue 事件会将空间公告状态更新为已显示(Shown)。
         *
         * @param event 空间公告事件
         * @see SpaceAnnouncementEvents 空间公告事件 sealed 接口
         * @see AnnouncementStatus 公告状态枚举
         */
        fun handleEvent(event: SpaceAnnouncementEvents) {
            when (event) {
                SpaceAnnouncementEvents.Continue -> localCoroutineScope.launch {
                    announcementStore.setAnnouncementStatus(Announcement.Space, AnnouncementStatus.Shown)
                }
            }
        }

        return SpaceAnnouncementState(
            eventSink = ::handleEvent,
        )
    }
}
