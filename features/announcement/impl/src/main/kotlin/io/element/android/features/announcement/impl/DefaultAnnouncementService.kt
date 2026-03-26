/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.announcement.api.Announcement
import io.element.android.features.announcement.api.AnnouncementService
import io.element.android.features.announcement.impl.spaces.SpaceAnnouncementState
import io.element.android.features.announcement.impl.spaces.SpaceAnnouncementView
import io.element.android.features.announcement.impl.store.AnnouncementStatus
import io.element.android.features.announcement.impl.store.AnnouncementStore
import io.element.android.libraries.architecture.Presenter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

@ContributesBinding(AppScope::class)
/**
 * 默认公告服务实现类
 *
 * 实现 AnnouncementService 接口，提供公告的完整功能。
 * 负责公告的显示控制、状态管理以及 UI 渲染。
 * 使用注入的存储和 Presenter 来管理公告的生命周期。
 *
 * @property announcementStore 公告存储，用于持久化公告状态
 * @property announcementPresenter 公告 Presenter，用于生成全局公告状态
 * @property spaceAnnouncementPresenter 空间公告 Presenter，用于生成空间公告界面状态
 * @see AnnouncementService 公告服务接口
 * @see AnnouncementStore 公告存储接口
 * @see AnnouncementPresenter 公告 Presenter
 * @see SpaceAnnouncementPresenter 空间公告 Presenter
 */
class DefaultAnnouncementService(
    /** 公告存储，用于持久化公告状态 */
    private val announcementStore: AnnouncementStore,
    /** 公告 Presenter，用于生成全局公告状态 */
    private val announcementPresenter: Presenter<AnnouncementState>,
    /** 空间公告 Presenter，用于生成空间公告界面状态 */
    private val spaceAnnouncementPresenter: Presenter<SpaceAnnouncementState>,
) : AnnouncementService {
    /**
     * 显示公告
     *
     * 根据公告类型执行相应的显示逻辑。
     * 空间公告会检查是否从未显示过，只有未显示过的才会标记为显示状态。
     * 新通知声音公告会直接设置为显示状态。
     *
     * @param announcement 要显示的公告类型
     * @see Announcement 公告类型枚举
     * @see AnnouncementStatus 公告状态枚举
     */
    override suspend fun showAnnouncement(announcement: Announcement) {
        when (announcement) {
            Announcement.Space -> showSpaceAnnouncement()
            Announcement.NewNotificationSound -> {
                announcementStore.setAnnouncementStatus(Announcement.NewNotificationSound, AnnouncementStatus.Show)
            }
        }
    }

    /**
     * 处理公告关闭事件
     *
     * 将指定公告的状态更新为已显示(Shown)，确保该公告后续不再显示。
     *
     * @param announcement 被关闭的公告类型
     * @see Announcement 公告类型枚举
     * @see AnnouncementStatus 公告状态枚举
     */
    override suspend fun onAnnouncementDismissed(announcement: Announcement) {
        announcementStore.setAnnouncementStatus(announcement, AnnouncementStatus.Shown)
    }

    /**
     * 获取待显示公告的 Flow
     *
     * 监听所有公告的状态，返回当前需要显示的公告列表。
     * 使用 combine 操作符合并多个公告状态流，实现实时更新。
     *
     * @return Flow<List<Announcement>> 待显示公告列表的 Flow
     * @see Announcement 公告类型枚举
     * @see AnnouncementStatus 公告状态枚举
     */
    override fun announcementsToShowFlow(): Flow<List<Announcement>> {
        return combine(
            announcementStore.announcementStatusFlow(Announcement.Space),
            announcementStore.announcementStatusFlow(Announcement.NewNotificationSound),
        ) { spaceAnnouncementStatus, newNotificationSoundStatus ->
            buildList {
                if (spaceAnnouncementStatus == AnnouncementStatus.Show) {
                    add(Announcement.Space)
                }
                if (newNotificationSoundStatus == AnnouncementStatus.Show) {
                    add(Announcement.NewNotificationSound)
                }
            }
        }
    }

    /**
     * 显示空间公告
     *
     * 仅当空间公告从未显示过时(NeverShown)，才将其状态设置为显示(Show)。
     * 这确保了空间公告只会向用户展示一次。
     *
     * @see AnnouncementStatus 公告状态枚举
     */
    private suspend fun showSpaceAnnouncement() {
        val currentValue = announcementStore.announcementStatusFlow(Announcement.Space).first()
        if (currentValue == AnnouncementStatus.NeverShown) {
            announcementStore.setAnnouncementStatus(Announcement.Space, AnnouncementStatus.Show)
        }
    }

    /**
     * 渲染公告界面
     *
     * 使用 Composable 渲染公告的 UI 组件。
     * 根据 announcementPresenter 生成的状态决定是否显示空间公告。
     * 公告内容通过 spaceAnnouncementPresenter 生成的 SpaceAnnouncementView 进行渲染。
     * 使用 AnimatedVisibility 实现公告的淡入淡出动画效果。
     *
     * @param modifier 修饰符，用于控制布局和行为
     * @see androidx.compose.animation.AnimatedVisibility 动画可见性
     * @see SpaceAnnouncementView 空间公告视图
     */
    @Composable
    override fun Render(modifier: Modifier) {
        val announcementState = announcementPresenter.present()
        Box(modifier = modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = announcementState.showSpaceAnnouncement,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                val spaceAnnouncementState = spaceAnnouncementPresenter.present()
                SpaceAnnouncementView(
                    state = spaceAnnouncementState,
                )
            }
        }
    }
}
