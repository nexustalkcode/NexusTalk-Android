/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.actionlist.model

/**
 * 时间线项目动作后处理器接口
 *
 * 用于在动作列表显示前对动作进行后处理。
 * 允许在运行时动态修改或过滤可用动作列表。
 * 例如：可以根据用户权限、房间设置或功能开关来添加或移除特定动作。
 *
 * @see TimelineItemAction 时间线动作
 * @see Default 默认实现（不做任何处理）
 */
fun interface TimelineItemActionPostProcessor {
    /**
     * 处理动作列表
     *
     * 在动作列表显示前调用，允许对动作进行过滤、排序或添加额外动作。
     *
     * @param actions 原始动作列表
     * @return 处理后的动作列表
     */
    fun process(actions: List<TimelineItemAction>): List<TimelineItemAction>

    /**
     * 默认后处理器
     *
     * 不对动作列表做任何修改，直接返回原始列表。
     * 适用于不需要任何后处理的场景。
     */
    object Default : TimelineItemActionPostProcessor {
        override fun process(actions: List<TimelineItemAction>): List<TimelineItemAction> {
            return actions
        }
    }
}
