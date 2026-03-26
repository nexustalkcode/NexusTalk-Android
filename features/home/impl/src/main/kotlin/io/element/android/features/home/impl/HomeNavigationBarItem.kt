/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import io.element.android.compound.tokens.generated.CompoundIcons

/**
 * 首页底部导航栏项目枚举
 *
 * 定义首页底部导航栏的各个选项，包括社区、聊天、空间和设置。
 *
 * @property labelRes 标签字符串资源 ID
 * @property isVisible 是否可见
 */
enum class HomeNavigationBarItem(
    @StringRes
    /** 标签字符串资源 ID */
    val labelRes: Int,
    /** 是否在导航栏中显示 */
    val isVisible: Boolean = true,
) {
    Community(
        /** 社区标签 */
        labelRes = R.string.screen_home_tab_community
    ),
    /** 聊天标签页 */
    Chats(
        labelRes = R.string.screen_home_tab_chats
    ),
    /** 空间标签页 */
    Spaces(
        labelRes = R.string.screen_home_tab_spaces,
        isVisible = false
    ),
    /** 设置标签页 */
    Settings(
        labelRes = io.element.android.libraries.ui.strings.R.string.common_settings
    );

    /**
     * 获取导航栏图标
     *
     * @param isSelected 是否被选中
     * @return 对应的图标
     */
    @Composable
    fun icon(
        isSelected: Boolean,
    ) = when (this) {
        Community -> if (isSelected) CompoundIcons.GroupV1Solid() else CompoundIcons.GroupV1()
        Chats -> if (isSelected) CompoundIcons.ChatSolid() else CompoundIcons.Chat()
        Spaces -> if (isSelected) CompoundIcons.SpaceSolid() else CompoundIcons.Space()
        Settings -> if (isSelected) CompoundIcons.SettingsV1Solid() else CompoundIcons.SettingsV1()
    }

    companion object {
        /**
         * 根据索引获取导航栏项目
         *
         * @param index 索引值
         * @return 对应的导航栏项目，如果索引无效则返回默认的 Community
         */
        fun from(index: Int): HomeNavigationBarItem {
            return entries.getOrElse(index) { Community }
        }

        /**
         * 获取所有可见的导航栏项目
         *
         * @return 可见导航栏项目列表
         */
        fun visibleEntries() = entries.filter { it.isVisible }
    }
}
