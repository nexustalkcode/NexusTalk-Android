/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl.create

import io.element.android.libraries.matrix.api.poll.PollKind
import kotlinx.collections.immutable.ImmutableList

/**
 * 创建投票状态数据类
 *
 * 表示创建或编辑投票界面的完整状态，包含投票问题、选项、类型等信息。
 * 此状态由 CreatePollPresenter 生成，供 CreatePollView 使用。
 *
 * @property mode 创建模式（新建或编辑）
 * @property canSave 是否可以保存（表单是否有效）
 * @property canAddAnswer 是否可以添加选项（是否达到最大数量）
 * @property question 投票问题
 * @property answers 投票选项列表（包含文本和删除权限）
 * @property pollKind 投票类型（公开或匿名）
 * @property showBackConfirmation 是否显示返回确认对话框（有未保存更改时）
 * @property showDeleteConfirmation 是否显示删除确认对话框
 * @property eventSink 事件处理函数，用于将用户操作转换为事件
 */
data class CreatePollState(
    val mode: Mode,
    val canSave: Boolean,
    val canAddAnswer: Boolean,
    val question: String,
    val answers: ImmutableList<Answer>,
    val pollKind: PollKind,
    val showBackConfirmation: Boolean,
    val showDeleteConfirmation: Boolean,
    val eventSink: (CreatePollEvent) -> Unit,
) {
    /**
     * 创建模式枚举
     */
    enum class Mode {
        /** 新建投票 */
        New,
        /** 编辑投票 */
        Edit,
    }

    /** 是否可以删除投票 */
    val canDelete: Boolean = mode == Mode.Edit
}

/**
 * 投票选项数据类（UI 用）
 *
 * 用于在 UI 中显示单个投票选项的简化版本。
 * 包含答案文本和该答案是否可以被删除。
 *
 * @property text 选项文本内容
 * @property canDelete 是否可以删除该选项（取决于当前答案总数）
 */
data class Answer(
    val text: String,
    val canDelete: Boolean,
)
