/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.pinned.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import im.vector.app.features.analytics.plan.Interaction
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.messages.impl.actionlist.ActionListEvents
import io.element.android.features.messages.impl.actionlist.ActionListView
import io.element.android.features.messages.impl.actionlist.model.TimelineItemAction
import io.element.android.features.messages.impl.link.LinkEvents
import io.element.android.features.messages.impl.link.LinkView
import io.element.android.features.messages.impl.timeline.components.TimelineItemRow
import io.element.android.features.messages.impl.timeline.components.event.TimelineItemEventContentView
import io.element.android.features.messages.impl.timeline.components.layout.ContentAvoidingLayoutData
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemPollContent
import io.element.android.features.messages.impl.timeline.protection.TimelineProtectionEvent
import io.element.android.features.messages.impl.timeline.protection.TimelineProtectionState
import io.element.android.features.poll.api.pollcontent.PollTitleView
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.services.analytics.compose.LocalAnalyticsService
import io.element.android.services.analyticsproviders.api.trackers.captureInteraction
import io.element.android.wysiwyg.link.Link

/**
 * 固定消息列表视图
 *
 * Compose Composable函数，用于渲染固定消息列表界面。
 * 根据当前状态显示不同的UI：加载中、空状态、错误或消息列表。
 *
 * @param state 固定消息列表的当前状态
 * @param onBackClick 点击返回按钮回调
 * @param onEventClick 点击事件回调
 * @param onUserDataClick 点击用户头像回调
 * @param onLinkClick 点击链接回调
 * @param onLinkLongClick 长按链接回调
 * @param modifier Compose修饰符
 *
 * @see PinnedMessagesListState 固定消息列表状态
 * @see TimelineItem.Event 时间线事件
 * @see MatrixUser Matrix用户
 * @see Link 链接
 */
@Composable
fun PinnedMessagesListView(
    state: PinnedMessagesListState,
    onBackClick: () -> Unit,
    onEventClick: (event: TimelineItem.Event) -> Unit,
    onUserDataClick: (MatrixUser) -> Unit,
    onLinkClick: (Link) -> Unit,
    onLinkLongClick: (Link) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            val analyticsService = LocalAnalyticsService.current
            PinnedMessagesListTopBar(
                state = state,
                onBackClick = {
                    analyticsService.captureInteraction(Interaction.Name.PinnedMessageBannerCloseListButton)
                    onBackClick()
                }
            )
        },
        content = { padding ->
            PinnedMessagesListContent(
                state = state,
                onEventClick = onEventClick,
                onUserDataClick = onUserDataClick,
                onLinkClick = onLinkClick,
                onLinkLongClick = onLinkLongClick,
                onErrorDismiss = onBackClick,
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding),
            )
        }
    )
}

/**
 * 固定消息列表顶部栏
 *
 * 渲染固定消息列表页面的顶部导航栏。
 * 显示页面标题和返回按钮。
 *
 * @param state 固定消息列表状态
 * @param onBackClick 点击返回按钮回调
 * @param modifier Compose修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PinnedMessagesListTopBar(
    state: PinnedMessagesListState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        titleStr = state.title(),
        navigationIcon = { BackButton(onClick = onBackClick) },
        modifier = modifier,
    )
}

/**
 * 固定消息列表内容组件
 *
 * 根据状态渲染不同的内容：
 * - Failed: 显示错误对话框
 * - Empty: 显示空状态视图
 * - Filled: 显示消息列表
 * - Loading: 显示加载指示器
 *
 * @param state 固定消息列表状态
 * @param onEventClick 点击事件回调
 * @param onUserDataClick 点击用户头像回调
 * @param onLinkClick 点击链接回调
 * @param onLinkLongClick 长按链接回调
 * @param onErrorDismiss 错误对话框关闭回调
 * @param modifier Compose修饰符
 */
@Composable
private fun PinnedMessagesListContent(
    state: PinnedMessagesListState,
    onEventClick: (event: TimelineItem.Event) -> Unit,
    onUserDataClick: (MatrixUser) -> Unit,
    onLinkClick: (Link) -> Unit,
    onLinkLongClick: (Link) -> Unit,
    onErrorDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        when (state) {
            PinnedMessagesListState.Failed -> {
                ErrorDialog(
                    title = stringResource(id = CommonStrings.error_unknown),
                    content = stringResource(id = CommonStrings.error_failed_loading_messages),
                    onSubmit = onErrorDismiss
                )
            }
            PinnedMessagesListState.Empty -> PinnedMessagesListEmpty()
            is PinnedMessagesListState.Filled -> PinnedMessagesListLoaded(
                state = state,
                displayThreadSummaries = state.displayThreadSummaries,
                onEventClick = onEventClick,
                onUserDataClick = onUserDataClick,
                onLinkClick = onLinkClick,
                onLinkLongClick = onLinkLongClick,
            )
            PinnedMessagesListState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

/**
 * 固定消息列表空状态组件
 *
 * 当没有置顶消息时显示的空状态视图。
 * 包含图标、标题和描述文字，
 * 引导用户如何置顶消息。
 *
 * @param modifier Compose修饰符
 */
@Composable
private fun PinnedMessagesListEmpty(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(
            horizontal = 32.dp,
            vertical = 48.dp,
        ),
        contentAlignment = Alignment.Center,
    ) {
        val pinActionText = stringResource(id = CommonStrings.action_pin)
        IconTitleSubtitleMolecule(
            title = stringResource(id = CommonStrings.screen_pinned_timeline_empty_state_headline),
            subTitle = stringResource(id = CommonStrings.screen_pinned_timeline_empty_state_description, pinActionText),
            iconStyle = BigIcon.Style.Default(CompoundIcons.Pin()),
        )
    }
}

/**
 * 固定消息列表已加载内容组件
 *
 * 渲染置顶消息列表的主要视图。
 * 使用LazyColumn展示所有置顶消息，
 * 处理长按事件显示操作列表。
 *
 * @param state 已填充的固定消息列表状态
 * @param displayThreadSummaries 是否显示线程摘要
 * @param onEventClick 点击事件回调
 * @param onUserDataClick 点击用户头像回调
 * @param onLinkClick 点击链接回调
 * @param onLinkLongClick 长按链接回调
 * @param modifier Compose修饰符
 */
@Composable
private fun PinnedMessagesListLoaded(
    state: PinnedMessagesListState.Filled,
    displayThreadSummaries: Boolean,
    onEventClick: (event: TimelineItem.Event) -> Unit,
    onUserDataClick: (MatrixUser) -> Unit,
    onLinkClick: (Link) -> Unit,
    onLinkLongClick: (Link) -> Unit,
    modifier: Modifier = Modifier,
) {
    fun onActionSelected(timelineItemAction: TimelineItemAction, event: TimelineItem.Event) {
        state.actionListState.eventSink(
            ActionListEvents.Clear
        )
        state.eventSink(
            PinnedMessagesListEvents.HandleAction(
                action = timelineItemAction,
                event = event,
            )
        )
    }

    fun onMessageLongClick(event: TimelineItem.Event) {
        state.actionListState.eventSink(
            ActionListEvents.ComputeForMessage(
                event = event,
                userEventPermissions = state.userEventPermissions,
            )
        )
    }

    ActionListView(
        state = state.actionListState,
        onSelectAction = ::onActionSelected,
        onCustomReactionClick = {},
        onEmojiReactionClick = { _, _ -> },
        onVerifiedUserSendFailureClick = {}
    )
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = rememberLazyListState(),
        reverseLayout = true,
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(
            items = state.timelineItems,
            contentType = { timelineItem -> timelineItem.contentType() },
            key = { timelineItem -> timelineItem.identifier() },
        ) { timelineItem ->
            TimelineItemRow(
                timelineItem = timelineItem,
                timelineMode = Timeline.Mode.PinnedEvents,
                timelineRoomInfo = state.timelineRoomInfo,
                renderReadReceipts = false,
                timelineProtectionState = state.timelineProtectionState,
                isLastOutgoingMessage = false,
                focusedEventId = null,
                onUserDataClick = onUserDataClick,
                onLinkClick = { link ->
                    state.linkState.eventSink(LinkEvents.OnLinkClick(link))
                },
                onLinkLongClick = onLinkLongClick,
                onContentClick = onEventClick,
                onLongClick = ::onMessageLongClick,
                displayThreadSummaries = displayThreadSummaries,
                inReplyToClick = {},
                onReactionClick = { _, _ -> },
                onReactionLongClick = { _, _ -> },
                onMoreReactionsClick = {},
                onReadReceiptClick = {},
                onSwipeToReply = {},
                onJoinCallClick = {},
                eventSink = {},
                eventContentView = { event, contentModifier, onContentLayoutChange ->
                    TimelineItemEventContentViewWrapper(
                        event = event,
                        timelineProtectionState = state.timelineProtectionState,
                        onContentClick = { onEventClick(event) },
                        onLongClick = { onMessageLongClick(event) },
                        onLinkClick = { link ->
                            state.linkState.eventSink(LinkEvents.OnLinkClick(link))
                        },
                        onLinkLongClick = onLinkLongClick,
                        modifier = contentModifier,
                        onContentLayoutChange = onContentLayoutChange
                    )
                },
            )
        }
    }
    LinkView(
        state.linkState,
        onLinkValid = onLinkClick,
    )
}

/**
 * 时间线事件内容视图包装器
 *
 * 根据事件内容类型渲染相应的视图：
 * - 投票内容：使用PollTitleView渲染
 * - 其他内容：使用TimelineItemEventContentView渲染
 *
 * 同时处理媒体保护状态（隐藏/显示敏感内容）。
 *
 * @param event 时间线事件
 * @param timelineProtectionState 时间线保护状态
 * @param onContentClick 内容点击回调
 * @param onLinkClick 链接点击回调
 * @param onLinkLongClick 链接长按回调
 * @param onLongClick 长按回调
 * @param onContentLayoutChange 内容布局变化回调
 * @param modifier Compose修饰符
 */
@Composable
private fun TimelineItemEventContentViewWrapper(
    event: TimelineItem.Event,
    timelineProtectionState: TimelineProtectionState,
    onContentClick: () -> Unit,
    onLinkClick: (Link) -> Unit,
    onLinkLongClick: (Link) -> Unit,
    onLongClick: (() -> Unit)?,
    onContentLayoutChange: (ContentAvoidingLayoutData) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (event.content is TimelineItemPollContent) {
        PollTitleView(
            title = event.content.question,
            isPollEnded = event.content.isEnded,
            modifier = modifier
        )
    } else {
        TimelineItemEventContentView(
            content = event.content,
            hideMediaContent = timelineProtectionState.hideMediaContent(event.eventId),
            isMine = event.isMine,
            onShowContentClick = { timelineProtectionState.eventSink(TimelineProtectionEvent.ShowContent(event.eventId)) },
            onLinkClick = onLinkClick,
            onLinkLongClick = onLinkLongClick,
            eventSink = { },
            modifier = modifier,
            onContentClick = onContentClick,
            onLongClick = onLongClick,
            onContentLayoutChange = onContentLayoutChange
        )
    }
}

/**
 * 固定消息列表视图预览
 *
 * 用于Compose预览功能的预览组件。
 * 使用PinnedMessagesListStateProvider提供多种状态示例。
 *
 * @param state 固定消息列表状态
 * @see PinnedMessagesListStateProvider 状态提供器
 */
@PreviewsDayNight
@Composable
internal fun PinnedMessagesListViewPreview(@PreviewParameter(PinnedMessagesListStateProvider::class) state: PinnedMessagesListState) =
    ElementPreview {
        PinnedMessagesListView(
            state = state,
            onBackClick = {},
            onEventClick = { },
            onUserDataClick = {},
            onLinkClick = {},
            onLinkLongClick = {},
        )
    }
