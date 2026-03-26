/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model

import androidx.compose.runtime.Immutable
import io.element.android.features.messages.impl.timeline.components.MessageShieldData
import io.element.android.features.messages.impl.timeline.model.TimelineItemGroupPosition.First
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemImageContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemStickerContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemTextBasedContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemVideoContent
import io.element.android.features.messages.impl.timeline.model.virtual.TimelineItemVirtualModel
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.core.SendHandle
import io.element.android.libraries.matrix.api.core.ThreadId
import io.element.android.libraries.matrix.api.core.TransactionId
import io.element.android.libraries.matrix.api.core.UniqueId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.timeline.item.ThreadSummary
import io.element.android.libraries.matrix.api.timeline.item.TimelineItemDebugInfo
import io.element.android.libraries.matrix.api.timeline.item.event.EventOrTransactionId
import io.element.android.libraries.matrix.api.timeline.item.event.LocalEventSendState
import io.element.android.libraries.matrix.api.timeline.item.event.MessageShield
import io.element.android.libraries.matrix.api.timeline.item.event.MessageShieldProvider
import io.element.android.libraries.matrix.api.timeline.item.event.ProfileDetails
import io.element.android.libraries.matrix.api.timeline.item.event.SendHandleProvider
import io.element.android.libraries.matrix.api.timeline.item.event.TimelineItemDebugInfoProvider
import io.element.android.libraries.matrix.api.timeline.item.event.TimelineItemEventOrigin
import io.element.android.libraries.matrix.api.timeline.item.event.getDisambiguatedDisplayName
import io.element.android.libraries.matrix.ui.messages.reply.InReplyToDetails
import kotlinx.collections.immutable.ImmutableList

/**
 * 时间线项目密封接口
 *
 * 定义时间线中显示的各种项目类型，包括事件、虚拟项目和分组事件。
 * 使用 @Immutable 注解标记为不可变。
 *
 * @see UniqueId 唯一ID
 * @see EventId 事件ID
 * @see TimelineItemEventContent 事件内容
 * @see TimelineItemVirtualModel 虚拟模型
 */
@Immutable
sealed interface TimelineItem {
    /**
     * 获取项目的唯一标识符
     *
     * @return 唯一ID
     */
    fun identifier(): UniqueId = when (this) {
        is Event -> id
        is Virtual -> id
        is GroupedEvents -> id
    }

    /**
     * 检查是否是指定ID的事件
     *
     * @param eventId 事件ID
     * @return 是否匹配
     */
    fun isEvent(eventId: EventId?): Boolean {
        if (eventId == null) return false
        return when (this) {
            is Event -> this.eventId == eventId
            else -> false
        }
    }

    /**
     * 获取内容类型
     *
     * @return 内容类型字符串
     */
    fun contentType(): String = when (this) {
        is Event -> content.type
        is Virtual -> model.type
        is GroupedEvents -> "groupedEvent"
    }

    /**
     * 虚拟时间线项目
     *
     * 用于显示日期分隔符等非事件内容。
     *
     * @property id 唯一ID
     * @property model 虚拟项目模型
     */
    data class Virtual(
        val id: UniqueId,
        val model: TimelineItemVirtualModel
    ) : TimelineItem

    /**
     * 时间线事件数据类
     *
     * 表示时间线中的一个消息事件。
     * 注意：eventId 在本地回显时可能为 null。
     *
     * @property id 唯一ID
     * @property eventId 事件ID（本地回显时可能为null）
     * @property transactionId 事务ID
     * @property senderId 发送者用户ID
     * @property senderProfile 发送者资料详情
     * @property senderAvatar 发送者头像数据
     * @property content 事件内容
     * @property sentTimeMillis 发送时间（毫秒）
     * @property sentTime 发送时间字符串
     * @property isMine 是否为自己发送的消息
     * @property isEditable 是否可编辑
     * @property canBeRepliedTo 是否可回复
     * @property groupPosition 分组位置
     * @property reactionsState 反应状态
     * @property readReceiptState 已读回执状态
     * @property localSendState 本地发送状态
     * @property inReplyTo 回复详情
     * @property threadInfo 线程信息
     * @property origin 事件来源
     * @property timelineItemDebugInfoProvider 调试信息提供器
     * @property messageShieldProvider 消息盾牌提供器
     * @property sendHandleProvider 发送句柄提供器
     * @property forwarder 转发者用户ID（如果有历史分享）
     * @property forwarderProfile 转发者资料（如果有缓存）
     */
    data class Event(
        val id: UniqueId,
        // Note: eventId can be null when the event is a local echo
        val eventId: EventId? = null,
        val transactionId: TransactionId? = null,
        val senderId: UserId,
        val senderProfile: ProfileDetails,
        val senderAvatar: AvatarData,
        val content: TimelineItemEventContent,
        val sentTimeMillis: Long = 0L,
        val sentTime: String = "",
        val isMine: Boolean = false,
        val isEditable: Boolean,
        val canBeRepliedTo: Boolean,
        val groupPosition: TimelineItemGroupPosition = TimelineItemGroupPosition.None,
        val reactionsState: TimelineItemReactions,
        val readReceiptState: TimelineItemReadReceipts,
        val localSendState: LocalEventSendState?,
        val inReplyTo: InReplyToDetails?,
        val threadInfo: TimelineItemThreadInfo?,
        val origin: TimelineItemEventOrigin?,
        val timelineItemDebugInfoProvider: TimelineItemDebugInfoProvider,
        val messageShieldProvider: MessageShieldProvider,
        val sendHandleProvider: SendHandleProvider,
        /**
         * If the keys to this message were forwarded by another user via history sharing (MSC4268), the ID of that user.
         * If this is non-null, then [messageShieldProvider] will also return [MessageShield.AuthenticityNotGuaranteed].
         */
        val forwarder: UserId?,
        /** If [forwarder] is set, the profile of the forwarding user, if it was cached at the time the `EventTimelineItem` was created. */
        val forwarderProfile: ProfileDetails?,
    ) : TimelineItem {
        val showSenderAvatar = (groupPosition is TimelineItemGroupPosition.Last ||groupPosition is TimelineItemGroupPosition.None) && !isMine
        val showSenderName = groupPosition.isNew() && !isMine

        val safeSenderName: String = senderProfile.getDisambiguatedDisplayName(senderId)

        val failedToSend: Boolean = localSendState is LocalEventSendState.Failed

        val isTextMessage: Boolean = content is TimelineItemTextBasedContent

        val isSticker: Boolean = content is TimelineItemStickerContent

        val isRemote = eventId != null

        /** Whether a click on any part of the event bubble should trigger the 'onContentClick' callback.
         *
         *  This is `true` for all events except for visual media events with a caption or formatted caption.
         */
        val isWholeContentClickable = when (content) {
            is TimelineItemStickerContent -> content.formattedCaption == null && content.caption == null
            is TimelineItemImageContent -> content.formattedCaption == null && content.caption == null
            is TimelineItemVideoContent -> content.formattedCaption == null && content.caption == null
            else -> true
        }

        val eventOrTransactionId: EventOrTransactionId
            get() = EventOrTransactionId.from(eventId = eventId, transactionId = transactionId)

        // No need to be lazy here?
        val messageShield: MessageShieldData? = messageShieldProvider(strict = false)?.let {
            MessageShieldData(it, forwarder, forwarderProfile)
        }

        val debugInfo: TimelineItemDebugInfo
            get() = timelineItemDebugInfoProvider()

        val sendhandle: SendHandle? get() = sendHandleProvider()
    }

    /**
     * 分组事件数据类
     *
     * 将多个相关事件分组显示。
     *
     * @property id 唯一ID
     * @property events 事件列表
     * @property aggregatedReadReceipts 聚合的已读回执
     */
    data class GroupedEvents(
        val id: UniqueId,
        val events: ImmutableList<Event>,
        val aggregatedReadReceipts: ImmutableList<ReadReceiptData>,
    ) : TimelineItem
}

/**
 * 时间线线程信息密封接口
 *
 * 定义线程相关的信息类型。
 */
sealed interface TimelineItemThreadInfo {
    /**
     * 线程根事件
     *
     * @property summary 线程摘要
     * @property latestEventText 最新事件文本
     */
    data class ThreadRoot(val summary: ThreadSummary, val latestEventText: String?) : TimelineItemThreadInfo

    /**
     * 线程回复
     *
     * @property threadRootId 线程根事件ID
     */
    data class ThreadResponse(val threadRootId: ThreadId) : TimelineItemThreadInfo
}
