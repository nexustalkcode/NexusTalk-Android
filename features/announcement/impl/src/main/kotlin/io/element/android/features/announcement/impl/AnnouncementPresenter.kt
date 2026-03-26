/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.zacsweers.metro.Inject
import io.element.android.features.announcement.api.Announcement
import io.element.android.features.announcement.impl.store.AnnouncementStatus
import io.element.android.features.announcement.impl.store.AnnouncementStore
import io.element.android.libraries.architecture.Presenter
import kotlinx.coroutines.flow.map

/**
 * 公告 Presenter
 *
 * 负责管理公告功能的全局状态和逻辑。
 * 监控各类公告的显示状态，并将状态转换为 UI 可用的 AnnouncementState。
 * 该 Presenter 是公告功能的核心业务逻辑层，负责协调存储和状态管理。
 *
 * @property announcementStore 公告存储，用于持久化和读取公告状态
 * @see AnnouncementStore 公告存储接口
 * @see AnnouncementState 公告状态数据类
 */
@Inject
class AnnouncementPresenter(
    /** 公告存储，用于持久化和读取公告状态 */
    private val announcementStore: AnnouncementStore,
) : Presenter<AnnouncementState> {
    /**
     * 生成界面状态
     *
     * 通过收集公告存储中的状态流，生成当前界面所需的公告状态。
     * 该方法会在 Composable 重组时自动调用，保持 UI 与状态的同步。
     *
     * @return AnnouncementState 公告状态，包含是否显示空间公告等信息
     * @see AnnouncementState 公告状态数据类
     */
    @Composable
    override fun present(): AnnouncementState {
        val showSpaceAnnouncement by remember {
            announcementStore.announcementStatusFlow(Announcement.Space).map {
                it == AnnouncementStatus.Show
            }
        }.collectAsState(false)
        return AnnouncementState(
            showSpaceAnnouncement = showSpaceAnnouncement,
        )
    }
}
