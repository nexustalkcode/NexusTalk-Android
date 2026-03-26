/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.actionlist.model

import androidx.annotation.VisibleForTesting

/**
 * 时间线项目动作比较器
 *
 * 实现 [Comparator] 接口，用于对动作列表进行排序。
 * 动作的显示顺序遵循Figma设计规范中的用户体验最佳实践。
 * 排序逻辑基于预定义的动作优先级列表。
 *
 * @see Comparator 比较器接口
 * @see TimelineItemAction 时间线动作
 * @see <a href="https://www.figma.com/design/ux3tYoZV9WghC7hHT9Fhk0/Compound-iOS-Components?node-id=2946-2392">Figma设计规范</a>
 */
class TimelineItemActionComparator : Comparator<TimelineItemAction> {
    /**
     * 动作排序列表
     *
     * 定义动作的显示优先级顺序。
     * 优先级从高到低排列：
     * 1. 结束投票 - 最高优先级
     * 2. 在时间线中查看
     * 3. 回复
     * 4. 在线程中回复
     * 5. 转发
     * 6. 编辑
     * 7. 编辑投票
     * 8. 添加标题
     * 9. 编辑标题
     * 10. 复制链接
     * 11. 置顶/取消置顶
     * 12. 复制文本/复制标题
     * 13. 删除标题
     * 14. 查看源代码
     * 15. 举报内容
     * 16. 删除 - 最低优先级
     *
     * 此顺序基于用户操作频率和操作安全性综合考虑。
     */
    @VisibleForTesting
    val orderedList = listOf(
        TimelineItemAction.EndPoll,
        TimelineItemAction.ViewInTimeline,
        TimelineItemAction.Reply,
        TimelineItemAction.ReplyInThread,
        TimelineItemAction.Forward,
        TimelineItemAction.Edit,
        TimelineItemAction.EditPoll,
        TimelineItemAction.AddCaption,
        TimelineItemAction.EditCaption,
        TimelineItemAction.CopyLink,
        TimelineItemAction.Pin,
        TimelineItemAction.Unpin,
        TimelineItemAction.CopyText,
        TimelineItemAction.CopyCaption,
        TimelineItemAction.RemoveCaption,
        TimelineItemAction.ViewSource,
        TimelineItemAction.ReportContent,
        TimelineItemAction.Redact,
    )

    override fun compare(o1: TimelineItemAction, o2: TimelineItemAction): Int {
        val index1 = orderedList.indexOf(o1)
        val index2 = orderedList.indexOf(o2)
        return index1.compareTo(index2)
    }
}
