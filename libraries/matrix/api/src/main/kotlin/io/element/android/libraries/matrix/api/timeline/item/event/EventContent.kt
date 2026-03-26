/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.timeline.item.event

import androidx.compose.runtime.Immutable
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.media.ImageInfo
import io.element.android.libraries.matrix.api.media.MediaSource
import io.element.android.libraries.matrix.api.poll.PollAnswer
import io.element.android.libraries.matrix.api.poll.PollKind
import io.element.android.libraries.matrix.api.timeline.item.EventThreadInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

/**
 * 时间线事件内容的密封接口。
 *
 * 表示 Matrix 时间线中各类事件的具体内容类型，包括消息、贴纸、投票、房间成员变更、
 * 无法解密内容、状态事件及解析失败等。用于在 UI 层根据事件类型进行分支渲染与处理。
 */
@Immutable
sealed interface EventContent

/**
 * 普通消息事件内容。
 *
 * @param body 消息正文文本
 * @param inReplyTo 若为回复消息，则包含被回复消息的引用信息；否则为 null
 * @param isEdited 该消息是否已被编辑过
 * @param threadInfo 若属于某条线程，则包含线程信息；否则为 null
 * @param type 消息类型（如文本、图片、文件等）
 */
data class MessageContent(
    val body: String,
    val inReplyTo: InReplyTo?,
    val isEdited: Boolean,
    val threadInfo: EventThreadInfo?,
    val type: MessageType,
) : EventContent

/**
 * 已撤回（红标）事件内容。
 *
 * 表示该事件已被管理员或发送者撤回，原始内容不可见。
 */
data object RedactedContent : EventContent

/**
 * 贴纸事件内容。
 *
 * @param filename 贴纸文件名
 * @param body 贴纸的可选描述文本，可为 null
 * @param info 图片元数据（尺寸、类型等）
 * @param source 媒体源（用于加载图片）
 * @param threadInfo 若属于某条线程，则包含线程信息；否则为 null
 * @property bestDescription 用于展示的最佳描述：优先使用 body，否则使用 filename
 */
data class StickerContent(
    val filename: String,
    val body: String?,
    val info: ImageInfo,
    val source: MediaSource,
    val threadInfo: EventThreadInfo?,
) : EventContent {
    val bestDescription: String
        get() = body ?: filename
}

/**
 * 投票事件内容。
 *
 * @param question 投票问题/标题
 * @param kind 投票类型（如一次性投票、多次选择等）
 * @param maxSelections 单用户最多可选项数量
 * @param answers 投票选项列表（不可变）
 * @param votes 各选项对应的投票用户 ID 列表，key 为选项 ID
 * @param endTime 投票结束时间戳（毫秒），未结束则为 null
 * @param isEdited 投票是否已被编辑
 * @param threadInfo 若属于某条线程，则包含线程信息；否则为 null
 */
data class PollContent(
    val question: String,
    val kind: PollKind,
    val maxSelections: ULong,
    val answers: ImmutableList<PollAnswer>,
    val votes: ImmutableMap<String, ImmutableList<UserId>>,
    val endTime: ULong?,
    val isEdited: Boolean,
    val threadInfo: EventThreadInfo?,
) : EventContent

/**
 * 无法解密的事件内容。
 *
 * 用于端到端加密房间中因缺少密钥或会话而无法解密的消息，可携带加密算法相关信息以便诊断。
 *
 * @param data 加密相关数据（Olm/Megolm 等），用于区分无法解密的原因
 * @param threadInfo 若属于某条线程，则包含线程信息；否则为 null
 */
data class UnableToDecryptContent(
    val data: Data,
    val threadInfo: EventThreadInfo?,
) : EventContent {
    /**
     * 无法解密时的加密算法/会话信息。
     */
    @Immutable
    sealed interface Data {
        /** Olm v1 加密（Curve25519 + AES-SHA2）对应的发送方密钥信息 */
        data class OlmV1Curve25519AesSha2(
            val senderKey: String
        ) : Data

        /** Megolm v1 加密（AES-SHA2）对应的会话 ID 及 UTD 原因 */
        data class MegolmV1AesSha2(
            val sessionId: String,
            val utdCause: UtdCause
        ) : Data

        /** 未知的加密类型 */
        data object Unknown : Data
    }
}

/**
 * 房间成员变更事件内容。
 *
 * 用于加入、离开、邀请、封禁等成员状态变化。
 *
 * @param userId 发生变更的用户 ID
 * @param userDisplayName 用户显示名，可能为 null
 * @param change 成员变更类型（如加入、离开、邀请等），可能为 null
 * @param reason 变更原因说明（如封禁原因），可能为 null
 */
data class RoomMembershipContent(
    val userId: UserId,
    val userDisplayName: String?,
    val change: MembershipChange?,
    val reason: String?,
) : EventContent

/**
 * 用户资料变更事件内容。
 *
 * 用于显示名称或头像的变更（如 m.room.member 中的 displayname/avatar 变化）。
 *
 * @param displayName 当前显示名，可能为 null
 * @param prevDisplayName 之前的显示名，可能为 null
 * @param avatarUrl 当前头像 URL，可能为 null
 * @param prevAvatarUrl 之前的头像 URL，可能为 null
 */
data class ProfileChangeContent(
    val displayName: String?,
    val prevDisplayName: String?,
    val avatarUrl: String?,
    val prevAvatarUrl: String?
) : EventContent

/**
 * 通用状态事件内容。
 *
 * 用于房间状态事件（如 room name、topic 等），由 [stateKey] 与 [content] 共同描述。
 *
 * @param stateKey 状态键，用于区分同一事件类型下的不同状态
 * @param content 具体状态内容（由 [OtherState] 表示）
 */
data class StateContent(
    val stateKey: String,
    val content: OtherState
) : EventContent

/**
 * 消息类事件解析失败时的占位内容。
 *
 * 当服务端下发的消息类事件无法被正确解析时使用，便于在 UI 显示错误或占位信息。
 *
 * @param eventType 原始事件类型字符串
 * @param error 解析错误描述
 */
data class FailedToParseMessageLikeContent(
    val eventType: String,
    val error: String
) : EventContent

/**
 * 状态事件解析失败时的占位内容。
 *
 * 当状态事件无法被正确解析时使用。
 *
 * @param eventType 原始事件类型字符串
 * @param stateKey 状态键
 * @param error 解析错误描述
 */
data class FailedToParseStateContent(
    val eventType: String,
    val stateKey: String,
    val error: String
) : EventContent

/**
 * 旧版通话邀请事件内容（占位）。
 *
 * 用于兼容旧版 VoIP 邀请事件，不携带具体通话详情。
 */
data object LegacyCallInviteContent : EventContent

/**
 * 通话通知事件内容（占位）。
 *
 * 用于通话相关通知类事件。
 */
data object CallNotifyContent : EventContent

/**
 * 未知类型事件内容。
 *
 * 当事件类型无法映射到上述任一具体类型时使用。
 */
data object UnknownContent : EventContent
