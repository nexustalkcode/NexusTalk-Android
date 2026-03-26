/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.impl.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import io.element.android.features.announcement.impl.AnnouncementPresenter
import io.element.android.features.announcement.impl.AnnouncementState
import io.element.android.features.announcement.impl.spaces.SpaceAnnouncementPresenter
import io.element.android.features.announcement.impl.spaces.SpaceAnnouncementState
import io.element.android.libraries.architecture.Presenter

/**
 * 公告模块依赖注入配置
 *
 * 使用 Metro 依赖注入框架绑定公告功能相关的 Presenter 实现。
 * 该模块将具体的 Presenter 实现绑定到其接口，便于在其他组件中注入使用。
 *
 * @see io.element.android.libraries.architecture.Presenter Presenter 接口
 * @see AnnouncementPresenter 公告 Presenter 实现
 * @see SpaceAnnouncementPresenter 空间公告 Presenter 实现
 */
@ContributesTo(AppScope::class)
@BindingContainer
interface AnnouncementModule {
    /**
     * 绑定公告 Presenter
     *
     * 将 AnnouncementPresenter 绑定到 Presenter<AnnouncementState> 接口，
     * 使其可以通过依赖注入在其他组件中使用。
     *
     * @param presenter 公告 Presenter 实例
     * @return Presenter<AnnouncementState> 泛型 Presenter 接口
     * @see AnnouncementPresenter 公告 Presenter 实现类
     * @see AnnouncementState 公告状态类型
     */
    @Binds
    fun bindAnnouncementPresenter(presenter: AnnouncementPresenter): Presenter<AnnouncementState>

    /**
     * 绑定空间公告 Presenter
     *
     * 将 SpaceAnnouncementPresenter 绑定到 Presenter<SpaceAnnouncementState> 接口，
     * 使其可以通过依赖注入在其他组件中使用。
     *
     * @param presenter 空间公告 Presenter 实例
     * @return Presenter<SpaceAnnouncementState> 泛型 Presenter 接口
     * @see SpaceAnnouncementPresenter 空间公告 Presenter 实现类
     * @see SpaceAnnouncementState 空间公告状态类型
     */
    @Binds
    fun bindSpaceAnnouncementPresenter(presenter: SpaceAnnouncementPresenter): Presenter<SpaceAnnouncementState>
}
