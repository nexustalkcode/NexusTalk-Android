/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model.event

import androidx.compose.runtime.Immutable
import io.element.android.libraries.matrix.api.media.MediaSource
import kotlin.time.Duration

@Immutable
/**
 * 所有时间线事件内容模型的共同接口。
 */
sealed interface TimelineItemEventContent {
    /** 用于调试和路由的稳定类型标识。 */
    val type: String
}

/**
 * 表示该事件内容本身具备“可被编辑”属性。
 */
interface TimelineItemEventMutableContent {
    /** Whether the event has been edited. */
    val isEdited: Boolean
}

@Immutable
/**
 * 带附件的事件内容共同接口。
 */
sealed interface TimelineItemEventContentWithAttachment :
    TimelineItemEventContent,
    TimelineItemEventMutableContent {
    val filename: String
    val fileSize: Long?
    val caption: String?
    val formattedCaption: CharSequence?
    val mediaSource: MediaSource
    val mimeType: String
    val formattedFileSize: String
    val fileExtension: String

    /**
     * 优先用于展示的描述文案。
     */
    val bestDescription: String
        get() = caption ?: filename
}

/**
 * 判断当前事件内容是否允许复制。
 */
fun TimelineItemEventContent.canBeCopied(): Boolean =
    this is TimelineItemTextBasedContent

/**
 * 判断当前事件内容是否允许转发。
 */
fun TimelineItemEventContent.canBeForwarded(): Boolean =
    when (this) {
        is TimelineItemTextBasedContent,
        is TimelineItemImageContent,
        is TimelineItemFileContent,
        is TimelineItemAudioContent,
        is TimelineItemVideoContent,
        is TimelineItemLocationContent,
        is TimelineItemVoiceContent -> true
        // Stickers can't be forwarded (yet) so we don't show the option
        // See https://github.com/element-hq/element-x-android/issues/2161
        is TimelineItemStickerContent -> false
        else -> false
    }

/**
 * 判断当前事件内容是否允许添加 reaction。
 *
 * 这里只判断内容类型，不考虑用户权级。
 */
fun TimelineItemEventContent.canReact(): Boolean =
    when (this) {
        is TimelineItemTextBasedContent,
        is TimelineItemAudioContent,
        is TimelineItemEncryptedContent,
        is TimelineItemFileContent,
        is TimelineItemImageContent,
        is TimelineItemStickerContent,
        is TimelineItemLocationContent,
        is TimelineItemPollContent,
        is TimelineItemVoiceContent,
        is TimelineItemVideoContent -> true
        is TimelineItemStateContent,
        is TimelineItemRedactedContent,
        is TimelineItemLegacyCallInviteContent,
        is TimelineItemRtcNotificationContent,
        TimelineItemUnknownContent -> false
    }

/**
 * 判断当前事件内容是否已经被编辑过。
 */
fun TimelineItemEventContent.isEdited(): Boolean = when (this) {
    is TimelineItemEventMutableContent -> isEdited
    else -> false
}

/**
 * 判断当前事件内容是否已被撤回。
 */
fun TimelineItemEventContent.isRedacted(): Boolean = this is TimelineItemRedactedContent

/**
 * 如果当前内容携带时长信息，则返回对应时长。
 */
fun TimelineItemEventContentWithAttachment.duration(): Duration? {
    return when (this) {
        is TimelineItemAudioContent -> duration
        is TimelineItemVideoContent -> duration
        is TimelineItemVoiceContent -> duration
        else -> null
    }
}
