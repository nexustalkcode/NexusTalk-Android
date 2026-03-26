/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.announcement.impl.spaces

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * 空间公告状态预览参数提供器
 *
 * 用于在 Compose 预览中生成示例状态数据。
 * 继承自 PreviewParameterProvider，为预览框架提供测试数据。
 *
 * @see SpaceAnnouncementState 空间公告状态数据类
 * @see androidx.compose.ui.tooling.preview.PreviewParameter 预览参数注解
 */
open class SpaceAnnouncementStateProvider : PreviewParameterProvider<SpaceAnnouncementState> {
    /**
     * 获取预览状态序列
     *
     * 返回一个包含示例状态的序列，用于在各种预览场景中展示 UI 效果。
     * 当前只提供一个默认状态的示例。
     *
     * @return Sequence<SpaceAnnouncementState> 空间公告状态的序列
     */
    override val values: Sequence<SpaceAnnouncementState>
        get() = sequenceOf(
            aSpaceAnnouncementState(),
        )
}

/**
 * 创建空间公告状态工厂函数
 *
 * 用于在测试或预览时快速创建 SpaceAnnouncementState 实例。
 * 提供默认参数方便调用，无需显式指定所有属性。
 *
 * @param eventSink 事件处理函数，默认值为空函数
 * @return SpaceAnnouncementState 空间公告状态实例
 * @see SpaceAnnouncementState 空间公告状态数据类
 */
fun aSpaceAnnouncementState(
    eventSink: (SpaceAnnouncementEvents) -> Unit = {},
) = SpaceAnnouncementState(
    eventSink = eventSink,
)
