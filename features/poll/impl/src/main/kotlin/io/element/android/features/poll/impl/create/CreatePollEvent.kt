/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.poll.impl.create

import io.element.android.libraries.matrix.api.poll.PollKind

/**
 * 创建投票事件密封接口
 *
 * 定义了创建投票界面中所有可能发生的用户事件。
 * 使用密封接口确保事件类型的完整性和安全性。
 */
sealed interface CreatePollEvent {
    /** 保存投票事件 - 触发保存新创建的投票或编辑后的投票 */
    data object Save : CreatePollEvent

    /**
     * 删除投票事件 - 触发删除投票（仅在编辑模式下可用）
     *
     * @property confirmed 是否已确认删除
     */
    data class Delete(val confirmed: Boolean) : CreatePollEvent

    /**
     * 设置投票问题事件 - 更新投票的问题文本
     *
     * @property question 新的问题文本
     */
    data class SetQuestion(val question: String) : CreatePollEvent

    /**
     * 设置投票答案事件 - 更新指定索引的答案文本
     *
     * @property index 答案的索引位置
     * @property text 新的答案文本
     */
    data class SetAnswer(val index: Int, val text: String) : CreatePollEvent

    /** 添加答案事件 - 在投票中添加一个新的答案选项 */
    data object AddAnswer : CreatePollEvent

    /**
     * 删除答案事件 - 删除指定索引的答案选项
     *
     * @property index 要删除的答案索引位置
     */
    data class RemoveAnswer(val index: Int) : CreatePollEvent

    /**
     * 设置投票类型事件 - 切换投票的公开/匿名类型
     *
     * @property pollKind 投票类型（公开或匿名）
     */
    data class SetPollKind(val pollKind: PollKind) : CreatePollEvent

    /** 返回导航事件 - 触发返回上一界面 */
    data object NavBack : CreatePollEvent

    /** 确认返回事件 - 确认是否有未保存的更改需要处理 */
    data object ConfirmNavBack : CreatePollEvent

    /** 隐藏确认对话框事件 - 隐藏所有显示的确认对话框 */
    data object HideConfirmation : CreatePollEvent
}
