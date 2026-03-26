/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.utils.messagesummary

import android.content.Context
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemAudioContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEncryptedContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemFileContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemImageContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemLegacyCallInviteContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemLocationContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemPollContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemProfileChangeContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemRedactedContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemRtcNotificationContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemStateContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemStickerContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemTextBasedContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemUnknownContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemVideoContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemVoiceContent
import io.element.android.libraries.core.extensions.toSafeLength
import io.element.android.libraries.di.RoomScope
import io.element.android.libraries.di.annotations.ApplicationContext
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 默认消息摘要格式化器实现类
 *
 * 实现 MessageSummaryFormatter 接口，根据消息内容类型生成摘要文本：
 * - 文本消息：显示消息文本内容
 * - 位置消息：显示"共享位置"
 * - 加密消息：显示"无法解密"
 * - 已删除消息：显示"消息已移除"
 * - 投票消息：显示投票问题
 * - 语音消息：显示"语音消息"
 * - 图片消息：显示"图片"
 * - 贴纸消息：显示"贴纸"
 * - 视频消息：显示"视频"
 * - 文件消息：显示"文件"
 * - 音频消息：显示"音频"
 * - 通话邀请：显示"不支持的通话"
 * - 通话通知：显示"通话已开始"
 *
 * 使用 @ContributesBinding 注解绑定到 RoomScope。
 *
 * @property context Android上下文
 */
@ContributesBinding(RoomScope::class)
class DefaultMessageSummaryFormatter(
    @ApplicationContext private val context: Context,
) : MessageSummaryFormatter {
    override fun format(content: TimelineItemEventContent): String {
        return when (content) {
            is TimelineItemTextBasedContent -> content.plainText
            is TimelineItemProfileChangeContent -> content.body
            is TimelineItemStateContent -> content.body
            is TimelineItemLocationContent -> context.getString(CommonStrings.common_shared_location)
            is TimelineItemEncryptedContent -> context.getString(CommonStrings.common_unable_to_decrypt)
            is TimelineItemRedactedContent -> context.getString(CommonStrings.common_message_removed)
            is TimelineItemPollContent -> content.question
            is TimelineItemVoiceContent -> context.getString(CommonStrings.common_voice_message)
            is TimelineItemUnknownContent -> context.getString(CommonStrings.common_unsupported_event)
            is TimelineItemImageContent -> context.getString(CommonStrings.common_image)
            is TimelineItemStickerContent -> context.getString(CommonStrings.common_sticker)
            is TimelineItemVideoContent -> context.getString(CommonStrings.common_video)
            is TimelineItemFileContent -> context.getString(CommonStrings.common_file)
            is TimelineItemAudioContent -> context.getString(CommonStrings.common_audio)
            is TimelineItemLegacyCallInviteContent -> context.getString(CommonStrings.common_unsupported_call)
            is TimelineItemRtcNotificationContent -> context.getString(CommonStrings.common_call_started)
        }
            // Truncate the message to a safe length to avoid crashes in Compose
            .toSafeLength()
    }
}
