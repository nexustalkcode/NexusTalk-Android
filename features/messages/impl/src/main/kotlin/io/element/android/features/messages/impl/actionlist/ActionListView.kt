/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.actionlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.messages.impl.actionlist.model.TimelineItemAction
import io.element.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailure
import io.element.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailure.ChangedIdentity
import io.element.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailure.None
import io.element.android.features.messages.impl.crypto.sendfailure.VerifiedUserSendFailure.UnsignedDevice
import io.element.android.features.messages.impl.timeline.a11y.a11yReactionAction
import io.element.android.features.messages.impl.timeline.components.MessageShieldView
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemAudioContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEncryptedContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemFileContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemImageContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemLegacyCallInviteContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemLocationContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemPollContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemRedactedContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemRtcNotificationContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemStateContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemStickerContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemTextBasedContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemUnknownContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemVideoContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemVoiceContent
import io.element.android.features.messages.impl.utils.messagesummary.DefaultMessageSummaryFormatter
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.text.toSp
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.ListItemStyle
import io.element.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.hide
import io.element.android.libraries.matrix.ui.messages.sender.SenderName
import io.element.android.libraries.matrix.ui.messages.sender.SenderNameMode
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList

/**
 * 动作列表视图主入口
 *
 * 显示消息动作列表的底部弹出模态框。
 * 当用户点击消息时，显示可用的动作选项列表，包括回复、转发、编辑等操作。
 *
 * @param state 动作列表当前状态，包含目标消息和可用动作
 * @param onSelectAction 用户选择某个动作时的回调函数
 * @param onEmojiReactionClick 用户点击表情反应时的回调函数
 * @param onCustomReactionClick 用户点击自定义反应按钮时的回调函数
 * @param onVerifiedUserSendFailureClick 用户点击已验证用户发送失败提示时的回调函数
 * @param modifier 视图修饰符
 *
 * @see ActionListState 动作列表状态
 * @see TimelineItemAction 动作项
 * @see ModalBottomSheet 底部弹出模态框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionListView(
    state: ActionListState,
    onSelectAction: (action: TimelineItemAction, TimelineItem.Event) -> Unit,
    onEmojiReactionClick: (String, TimelineItem.Event) -> Unit,
    onCustomReactionClick: (TimelineItem.Event) -> Unit,
    onVerifiedUserSendFailureClick: (TimelineItem.Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val targetItem = (state.target as? ActionListState.Target.Success)?.event

    fun onItemActionClick(
        itemAction: TimelineItemAction
    ) {
        if (targetItem == null) return
        sheetState.hide(coroutineScope) {
            state.eventSink(ActionListEvents.Clear)
            onSelectAction(itemAction, targetItem)
        }
    }

    fun onEmojiReactionClick(emoji: String) {
        if (targetItem == null) return
        sheetState.hide(coroutineScope) {
            state.eventSink(ActionListEvents.Clear)
            onEmojiReactionClick(emoji, targetItem)
        }
    }

    fun onCustomReactionClick() {
        if (targetItem == null) return
        sheetState.hide(coroutineScope) {
            state.eventSink(ActionListEvents.Clear)
            onCustomReactionClick(targetItem)
        }
    }

    fun onDismiss() {
        state.eventSink(ActionListEvents.Clear)
    }

    fun onVerifiedUserSendFailureClick() {
        if (targetItem == null) return
        sheetState.hide(coroutineScope) {
            state.eventSink(ActionListEvents.Clear)
            onVerifiedUserSendFailureClick(targetItem)
        }
    }

    if (targetItem != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = ::onDismiss,
            modifier = modifier,
        ) {
            ActionListViewContent(
                state = state,
                onActionClick = ::onItemActionClick,
                onEmojiReactionClick = ::onEmojiReactionClick,
                onCustomReactionClick = ::onCustomReactionClick,
                onVerifiedUserSendFailureClick = ::onVerifiedUserSendFailureClick,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    }
}

/**
 * 动作列表内容视图
 *
 * 动作列表的主要内容包括：
 * - 消息摘要区域：显示发送者头像、名称、时间和消息预览
 * - 消息盾牌视图：如果消息有安全盾牌则显示
 * - 已验证用户发送失败视图：如果存在发送失败则显示
 * - Emoji反应行：显示最近使用的表情和自定义反应按钮
 * - 动作列表项：显示所有可用的动作选项
 *
 * @param state 动作列表状态
 * @param onActionClick 用户点击动作项时的回调
 * @param onEmojiReactionClick 用户点击表情时的回调
 * @param onCustomReactionClick 用户点击自定义反应按钮时的回调
 * @param onVerifiedUserSendFailureClick 用户点击发送失败提示时的回调
 * @param modifier 视图修饰符
 */
@Composable
private fun ActionListViewContent(
    state: ActionListState,
    onActionClick: (TimelineItemAction) -> Unit,
    onEmojiReactionClick: (String) -> Unit,
    onCustomReactionClick: () -> Unit,
    onVerifiedUserSendFailureClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val target = state.target) {
        is ActionListState.Target.Loading,
        ActionListState.Target.None -> {
            // Crashes if sheetContent size is zero
            Box(modifier = modifier.size(1.dp))
        }

        is ActionListState.Target.Success -> {
            val actions = target.actions
            LazyColumn(
                modifier = modifier.fillMaxWidth()
            ) {
                item {
                    Column {
                        MessageSummary(
                            event = target.event,
                            sentTimeFull = target.sentTimeFull,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clearAndSetSemantics {},
                        )
                        if (target.event.messageShield != null) {
                            MessageShieldView(
                                shield = target.event.messageShield,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                        HorizontalDivider()
                    }
                }
                if (target.verifiedUserSendFailure != None) {
                    item {
                        VerifiedUserSendFailureView(
                            sendFailure = target.verifiedUserSendFailure,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onVerifiedUserSendFailureClick
                        )
                        HorizontalDivider()
                    }
                }
                if (target.displayEmojiReactions) {
                    item {
                        EmojiReactionsRow(
                            recentEmojis = target.recentEmojis,
                            highlightedEmojis = target.event.reactionsState.highlightedKeys,
                            onEmojiReactionClick = onEmojiReactionClick,
                            onCustomReactionClick = onCustomReactionClick,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        HorizontalDivider()
                    }
                }
                items(
                    items = actions,
                ) { action ->
                    ListItem(
                        modifier = Modifier.clickable {
                            onActionClick(action)
                        },
                        headlineContent = {
                            Text(text = stringResource(id = action.titleRes))
                        },
                        leadingContent = ListItemContent.Icon(IconSource.Resource(action.icon)),
                        style = when {
                            action.destructive -> ListItemStyle.Destructive
                            else -> ListItemStyle.Primary
                        }
                    )
                }
            }
        }
    }
}

/**
 * 消息摘要组件
 *
 * 在动作列表顶部显示的消息预览信息。
 * 包含发送者头像、名称、发送时间以及消息内容的简短预览。
 * 根据不同的消息内容类型(text、image、file、voice等)显示相应的摘要信息。
 *
 * @param event 消息事件
 * @param sentTimeFull 完整格式的发送时间文本
 * @param modifier 视图修饰符
 *
 * @see TimelineItem.Event 消息事件
 * @see Avatar 头像组件
 * @see SenderName 发送者名称组件
 */
@Suppress("MultipleEmitters") // False positive
@Composable
private fun MessageSummary(
    event: TimelineItem.Event,
    sentTimeFull: String,
    modifier: Modifier = Modifier,
) {
    val content: @Composable () -> Unit
    val icon: @Composable () -> Unit = {
        Avatar(
            avatarData = event.senderAvatar.copy(size = AvatarSize.MessageActionSender),
            avatarType = AvatarType.User,
        )
    }
    val contentStyle = ElementTheme.typography.fontBodyMdRegular.copy(color = ElementTheme.colors.textSecondary)

    @Composable
    fun ContentForBody(body: String) {
        Text(body, style = contentStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }

    val context = LocalContext.current
    val formatter = remember(context) { DefaultMessageSummaryFormatter(context) }
    val textContent = remember(event.content) { formatter.format(event) }

    when (event.content) {
        is TimelineItemTextBasedContent,
        is TimelineItemStateContent,
        is TimelineItemEncryptedContent,
        is TimelineItemRedactedContent,
        is TimelineItemUnknownContent -> content = { ContentForBody(textContent) }
        is TimelineItemLocationContent -> {
            content = { ContentForBody(stringResource(CommonStrings.common_shared_location)) }
        }
        is TimelineItemImageContent -> {
            content = { ContentForBody(event.content.bestDescription) }
        }
        is TimelineItemStickerContent -> {
            content = { ContentForBody(event.content.bestDescription) }
        }
        is TimelineItemVideoContent -> {
            content = { ContentForBody(event.content.bestDescription) }
        }
        is TimelineItemFileContent -> {
            content = { ContentForBody(event.content.bestDescription) }
        }
        is TimelineItemAudioContent -> {
            content = { ContentForBody(event.content.bestDescription) }
        }
        is TimelineItemVoiceContent -> {
            content = { ContentForBody(textContent) }
        }
        is TimelineItemPollContent -> {
            content = { ContentForBody(textContent) }
        }
        is TimelineItemLegacyCallInviteContent -> {
            content = { ContentForBody(textContent) }
        }
        is TimelineItemRtcNotificationContent -> {
            content = { ContentForBody(stringResource(CommonStrings.common_call_started)) }
        }
    }
    Row(modifier = modifier) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row {
                SenderName(
                    modifier = Modifier.weight(1f),
                    senderId = event.senderId,
                    senderProfile = event.senderProfile,
                    senderNameMode = SenderNameMode.ActionList,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = sentTimeFull,
                    style = ElementTheme.typography.fontBodyXsRegular,
                    color = ElementTheme.colors.textSecondary,
                    textAlign = TextAlign.End,
                )
            }
            content()
        }
    }
}

/**
 * Emoji反应行组件
 *
 * 显示最近使用的emoji表情列表供用户快速反应，
 * 同时提供一个"添加反应"按钮让用户选择更多emoji。
 * 包含一个水平滚动的emoji列表和右侧的添加按钮。
 * 列表右侧有渐变效果以提示可滚动。
 *
 * @param recentEmojis 最近使用的emoji列表
 * @param highlightedEmojis 当前消息已添加的高亮emoji列表
 * @param onEmojiReactionClick 点击emoji时的回调
 * @param onCustomReactionClick 点击添加自定义反应按钮时的回调
 * @param modifier 视图修饰符
 */
private val emojiRippleRadius = 24.dp

@Composable
private fun EmojiReactionsRow(
    recentEmojis: ImmutableList<String>,
    highlightedEmojis: ImmutableList<String>,
    onEmojiReactionClick: (String) -> Unit,
    onCustomReactionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(end = 16.dp, top = 16.dp, bottom = 16.dp),
    ) {
        val backgroundColor = ElementTheme.colors.bgCanvasDefault

        LazyRow(
            modifier = Modifier
                .weight(1f, fill = true)
                .drawWithContent {
                    val gradientWidth = 24.dp.toPx()
                    val width = size.width
                    drawContent()

                    drawRect(
                        brush = Brush.horizontalGradient(
                            0.0f to Color.Transparent,
                            1.0f to backgroundColor,
                            startX = width - gradientWidth,
                            endX = width,
                        ),
                        topLeft = Offset(width - gradientWidth, 0f),
                        size = Size(gradientWidth, size.height)
                    )
                },
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(recentEmojis) { emoji ->
                val isHighlighted = highlightedEmojis.contains(emoji)
                EmojiButton(
                    modifier = Modifier
                        // Make it appear after the more useful actions for the accessibility service
                        .semantics {
                            traversalIndex = 1f
                        },
                    emoji = emoji,
                    isHighlighted = isHighlighted,
                    onClick = onEmojiReactionClick
                )
            }
        }

        Box(
            modifier = Modifier.padding(end = 10.dp).requiredSize(48.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Icon(
                imageVector = CompoundIcons.ReactionAdd(),
                contentDescription = stringResource(id = CommonStrings.a11y_react_with_other_emojis),
                tint = ElementTheme.colors.iconSecondary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        enabled = true,
                        onClick = onCustomReactionClick,
                        indication = ripple(bounded = false, radius = emojiRippleRadius),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    // Make it appear after the more useful actions for the accessibility service
                    .semantics {
                        traversalIndex = 1f
                    }
            )
        }
    }
}

/**
 * 已验证用户发送失败视图组件
 *
 * 当消息发送失败时显示的错误提示视图。
 * 展示不同类型的发送失败错误信息：
 * - 未知设备：提示用户使用未签名设备发送
 * - 身份变更：提示接收者身份已变更
 *
 * @param sendFailure 发送失败状态
 * @param onClick 点击该视图时的回调
 * @param modifier 视图修饰符
 *
 * @see VerifiedUserSendFailure 发送失败类型
 * @see ListItem 列表项组件
 */
@Composable
private fun VerifiedUserSendFailureView(
    sendFailure: VerifiedUserSendFailure,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    @Composable
    @ReadOnlyComposable
    fun VerifiedUserSendFailure.headline(): String {
        return when (this) {
            is None -> ""
            is UnsignedDevice.FromOther -> stringResource(CommonStrings.screen_timeline_item_menu_send_failure_unsigned_device, userDisplayName)
            is UnsignedDevice.FromYou -> stringResource(CommonStrings.screen_timeline_item_menu_send_failure_you_unsigned_device)
            is ChangedIdentity -> stringResource(CommonStrings.screen_timeline_item_menu_send_failure_changed_identity, userDisplayName)
        }
    }

    ListItem(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ErrorSolid())),
        trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChevronRight())),
        headlineContent = {
            Text(
                text = sendFailure.headline(),
                style = ElementTheme.typography.fontBodySmMedium,
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
            leadingIconColor = ElementTheme.colors.iconCriticalPrimary,
            trailingIconColor = ElementTheme.colors.iconPrimary,
            headlineColor = ElementTheme.colors.textCriticalPrimary,
        ),
    )
}

/**
 * Emoji按钮组件
 *
 * 显示单个emoji表情的圆形按钮。
 * 支持高亮状态（用户已添加该emoji时显示背景色）。
 * 包含无障碍功能标签，说明用户是否已添加该反应。
 *
 * @param emoji 要显示的emoji字符
 * @param isHighlighted 是否高亮显示（用户已添加该反应）
 * @param onClick 点击按钮时的回调
 * @param modifier 视图修饰符
 *
 * @see ripple 涟漪效果
 * @see a11yReactionAction 无障碍标签生成
 */
@Composable
private fun EmojiButton(
    emoji: String,
    isHighlighted: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isHighlighted) {
        ElementTheme.colors.bgActionPrimaryRest
    } else {
        Color.Transparent
    }
    val a11yClickLabel = a11yReactionAction(
        emoji = emoji,
        userAlreadyReacted = isHighlighted,
    )
    Box(
        modifier = modifier
            .size(48.dp)
            .background(backgroundColor, CircleShape)
            .clickable(
                onClickLabel = a11yClickLabel,
                onClick = { onClick(emoji) },
                indication = ripple(bounded = false, radius = emojiRippleRadius),
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            emoji,
            style = ElementTheme.typography.fontBodyLgRegular.copy(fontSize = 24.dp.toSp(), color = Color.White),
        )
    }
}

/**
 * 动作列表视图预览
 *
 * 用于开发阶段预览动作列表的不同状态。
 * 使用 [ActionListStateProvider] 提供多种测试状态。
 *
 * @param state 由 [ActionListStateProvider] 提供的预览状态参数
 *
 * @see PreviewsDayNight 日夜模式预览注解
 * @see ActionListStateProvider 状态提供器
 */
@PreviewsDayNight
@Composable
internal fun ActionListViewContentPreview(
    @PreviewParameter(ActionListStateProvider::class) state: ActionListState
) = ElementPreview {
    ActionListViewContent(
        state = state,
        onActionClick = {},
        onEmojiReactionClick = {},
        onCustomReactionClick = {},
        onVerifiedUserSendFailureClick = {},
    )
}
