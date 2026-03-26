/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl

import io.element.android.features.messages.api.timeline.voicemessages.composer.VoiceMessageComposerState
import io.element.android.features.messages.impl.actionlist.ActionListState
import io.element.android.features.messages.impl.crypto.historyvisible.HistoryVisibleState
import io.element.android.features.messages.impl.crypto.identity.IdentityChangeState
import io.element.android.features.messages.impl.link.LinkState
import io.element.android.features.messages.impl.messagecomposer.MessageComposerState
import io.element.android.features.messages.impl.pinned.banner.PinnedMessagesBannerState
import io.element.android.features.messages.impl.timeline.TimelineState
import io.element.android.features.messages.impl.timeline.components.customreaction.CustomReactionState
import io.element.android.features.messages.impl.timeline.components.reactionsummary.ReactionSummaryState
import io.element.android.features.messages.impl.timeline.components.receipt.bottomsheet.ReadReceiptBottomSheetState
import io.element.android.features.messages.impl.timeline.protection.TimelineProtectionState
import io.element.android.features.roomcall.api.RoomCallState
import io.element.android.features.roommembermoderation.api.RoomMemberModerationState
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.encryption.identity.IdentityState
import io.element.android.libraries.matrix.api.room.tombstone.SuccessorRoom
import kotlinx.collections.immutable.ImmutableList

/**
 * 消息页面状态数据类
 *
 * 表示消息页面的完整状态，包含房间信息、消息编辑器、时间线、加密状态、操作列表等多种状态。
 *
 * @property roomId 房间 ID
 * @property roomName 房间名称
 * @property roomAvatar 房间头像
 * @property heroes 房间成员头像列表
 * @property userEventPermissions 用户事件权限
 * @property composerState 消息编辑器状态
 * @property voiceMessageComposerState 语音消息编辑器状态
 * @property timelineState 时间线状态
 * @property timelineProtectionState 时间线保护状态
 * @property identityChangeState 身份更改状态
 * @property historyVisibleState 历史可见性状态
 * @property linkState 链接状态
 * @property actionListState 操作列表状态
 * @property customReactionState 自定义反应状态
 * @property reactionSummaryState 反应摘要状态
 * @property readReceiptBottomSheetState 已读回执底部表单状态
 * @property snackbarMessage 提示消息
 * @property inviteProgress 邀请进度
 * @property showReinvitePrompt 是否显示重新邀请提示
 * @property enableTextFormatting 是否启用文本格式
 * @property roomCallState 房间通话状态
 * @property appName 应用名称
 * @property pinnedMessagesBannerState 固定消息横幅状态
 * @property dmUserVerificationState DM 用户验证状态
 * @property roomMemberModerationState 房间成员 moderation 状态
 * @property successorRoom 继承房间
 * @property eventSink 事件处理函数
 */
data class MessagesState(
    val roomId: RoomId,
    val roomName: String?,
    val roomAvatar: AvatarData,
    val heroes: ImmutableList<AvatarData>,
    val userEventPermissions: UserEventPermissions,
    val composerState: MessageComposerState,
    val voiceMessageComposerState: VoiceMessageComposerState,
    val timelineState: TimelineState,
    val timelineProtectionState: TimelineProtectionState,
    val identityChangeState: IdentityChangeState,
    val historyVisibleState: HistoryVisibleState,
    val linkState: LinkState,
    val actionListState: ActionListState,
    val customReactionState: CustomReactionState,
    val reactionSummaryState: ReactionSummaryState,
    val readReceiptBottomSheetState: ReadReceiptBottomSheetState,
    val snackbarMessage: SnackbarMessage?,
    val inviteProgress: AsyncData<Unit>,
    val showReinvitePrompt: Boolean,
    val enableTextFormatting: Boolean,
    val roomCallState: RoomCallState,
    val appName: String,
    val pinnedMessagesBannerState: PinnedMessagesBannerState,
    val dmUserVerificationState: IdentityState?,
    val roomMemberModerationState: RoomMemberModerationState,
    val successorRoom: SuccessorRoom?,
    val eventSink: (MessagesEvents) -> Unit
) {
    /** 是否为墓碑状态（房间已迁移） */
    val isTombstoned = successorRoom != null
}
