/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.actionlist.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.element.android.libraries.designsystem.icons.CompoundDrawables
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 时间线项目动作枚举
 *
 * 定义消息可以执行的各种操作动作。
 * 每个动作都包含显示名称、图标和是否为破坏性操作的标记。
 * 破坏性操作（如删除、举报）会以特殊样式显示以提醒用户。
 *
 * @property titleRes 动作显示名称的字符串资源ID
 * @property icon 动作图标的资源ID
 * @property destructive 是否为破坏性操作，破坏性操作会以红色高亮显示
 *
 * @see CommonStrings 通用字符串资源
 * @see CompoundDrawables 复合图标资源
 */
enum class TimelineItemAction(
    /** 动作显示名称的字符串资源ID */
    @StringRes val titleRes: Int,
    /** 动作图标的资源ID */
    @DrawableRes val icon: Int,
    /** 是否为破坏性操作 */
    val destructive: Boolean = false
) {
    /**
     * 在时间线中查看
     * 允许用户跳转到消息在时间线中的原始位置
     */
    ViewInTimeline(CommonStrings.action_view_in_timeline, CompoundDrawables.ic_compound_visibility_on),

    /**
     * 转发消息
     * 允许用户将消息转发给其他联系人或房间
     */
    Forward(CommonStrings.action_forward, CompoundDrawables.ic_compound_forward),

    /**
     * 复制文本内容
     * 复制消息的文本内容到剪贴板
     */
    CopyText(CommonStrings.action_copy_text, CompoundDrawables.ic_compound_copy),

    /**
     * 复制标题
     * 复制媒体文件（如图片、视频）的标题/说明文字到剪贴板
     */
    CopyCaption(CommonStrings.action_copy_caption, CompoundDrawables.ic_compound_copy),

    /**
     * 复制链接
     * 复制消息的Matrix permalink链接到剪贴板
     */
    CopyLink(CommonStrings.action_copy_link_to_message, CompoundDrawables.ic_compound_link),

    /**
     * 删除消息（涂黑）
     * 删除消息内容，使其对所有用户不可见。
     * 这是一个破坏性操作。
     */
    Redact(CommonStrings.action_remove, CompoundDrawables.ic_compound_delete, destructive = true),

    /**
     * 回复消息
     * 在当前房间中回复该消息
     */
    Reply(CommonStrings.action_reply, CompoundDrawables.ic_compound_reply),

    /**
     * 在线程中回复
     * 在线程中回复该消息（如果线程功能已启用）
     */
    ReplyInThread(CommonStrings.action_reply_in_thread, CompoundDrawables.ic_compound_reply),

    /**
     * 编辑消息
     * 修改消息的文本内容
     */
    Edit(CommonStrings.action_edit, CompoundDrawables.ic_compound_edit),

    /**
     * 编辑投票
     * 修改投票的选项或设置
     */
    EditPoll(CommonStrings.action_edit_poll, CompoundDrawables.ic_compound_edit),

    /**
     * 编辑标题
     * 修改媒体文件的标题/说明文字
     */
    EditCaption(CommonStrings.action_edit_caption, CompoundDrawables.ic_compound_edit),

    /**
     * 添加标题
     * 为没有标题的媒体文件添加标题/说明文字
     */
    AddCaption(CommonStrings.action_add_caption, CompoundDrawables.ic_compound_edit),

    /**
     * 删除标题
     * 移除媒体文件的标题/说明文字。
     * 这是一个破坏性操作。
     */
    RemoveCaption(CommonStrings.action_remove_caption, CompoundDrawables.ic_compound_close, destructive = true),

    /**
     * 查看源代码
     * 显示消息的原始Matrix事件JSON内容（仅开发者模式可用）
     */
    ViewSource(CommonStrings.action_view_source, CompoundDrawables.ic_compound_code),

    /**
     * 举报内容
     * 向房间管理员举报该消息内容不当。
     * 这是一个破坏性操作。
     */
    ReportContent(CommonStrings.action_report_content, CompoundDrawables.ic_compound_chat_problem, destructive = true),

    /**
     * 结束投票
     * 结束一个正在进行的投票，不再接受新的投票
     */
    EndPoll(CommonStrings.action_end_poll, CompoundDrawables.ic_compound_polls_end),

    /**
     * 置顶消息
     * 将消息置顶到房间的置顶消息列表中
     */
    Pin(CommonStrings.action_pin, CompoundDrawables.ic_compound_pin),

    /**
     * 取消置顶
     * 将消息从房间的置顶消息列表中移除
     */
    Unpin(CommonStrings.action_unpin, CompoundDrawables.ic_compound_unpin),
}
