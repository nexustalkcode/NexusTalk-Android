/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.messages.impl.crypto.sendfailure.resolve.ResolveVerifiedUserSendFailureView
import io.element.android.features.messages.impl.timeline.components.TimelineItemRow
import io.element.android.features.messages.impl.timeline.components.toText
import io.element.android.features.messages.impl.timeline.di.LocalTimelineItemPresenterFactories
import io.element.android.features.messages.impl.timeline.di.aFakeTimelineItemPresenterFactories
import io.element.android.features.messages.impl.timeline.focus.FocusRequestStateView
import io.element.android.features.messages.impl.timeline.model.NewEventState
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContentProvider
import io.element.android.features.messages.impl.timeline.protection.TimelineProtectionState
import io.element.android.features.messages.impl.timeline.protection.aTimelineProtectionState
import io.element.android.libraries.androidutils.system.copyToClipboard
import io.element.android.libraries.designsystem.components.dialogs.AlertDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.FloatingActionButton
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.utils.animateScrollToItemCenter
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.libraries.ui.utils.time.isTalkbackActive
import io.element.android.wysiwyg.link.Link
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

/**
 * 时间线视图
 *
 * 渲染消息时间线的用户界面，支持滚动、跳转到底部、新事件提示、焦点事件聚焦等功能。
 *
 * @param state 时间线状态
 * @param timelineProtectionState 时间线保护状态
 * @param onUserDataClick 用户数据点击事件
 * @param onLinkClick 链接点击事件
 * @param onContentClick 内容点击事件
 * @param onMessageLongClick 消息长按事件
 * @param onSwipeToReply 左滑回复事件
 * @param onReactionClick 反应点击事件
 * @param onReactionLongClick 反应长按事件
 * @param onMoreReactionsClick 更多反应点击事件
 * @param onReadReceiptClick 已读回执点击事件
 * @param onJoinCallClick 加入通话点击事件
 * @param modifier 修饰符
 * @param lazyListState LazyList 状态
 * @param forceJumpToBottomVisibility 强制显示跳转到底部按钮
 * @param nestedScrollConnection 嵌套滚动连接
 */
@Composable
fun TimelineView(
    state: TimelineState,
    timelineProtectionState: TimelineProtectionState,
    onUserDataClick: (MatrixUser) -> Unit,
    onLinkClick: (Link) -> Unit,
    onContentClick: (TimelineItem.Event) -> Unit,
    onMessageLongClick: (TimelineItem.Event) -> Unit,
    onSwipeToReply: (TimelineItem.Event) -> Unit,
    onReactionClick: (emoji: String, TimelineItem.Event) -> Unit,
    onReactionLongClick: (emoji: String, TimelineItem.Event) -> Unit,
    onMoreReactionsClick: (TimelineItem.Event) -> Unit,
    onReadReceiptClick: (TimelineItem.Event) -> Unit,
    onJoinCallClick: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    forceJumpToBottomVisibility: Boolean = false,
    nestedScrollConnection: NestedScrollConnection = rememberNestedScrollInteropConnection(),
) {
    /**
     * 清除焦点请求状态
     */
    fun clearFocusRequestState() {
        state.eventSink(TimelineEvents.ClearFocusRequestState)
    }

    /**
     * 滚动完成处理
     *
     * @param firstVisibleIndex 第一个可见项索引
     */
    fun onScrollFinishAt(firstVisibleIndex: Int) {
        state.eventSink(TimelineEvents.OnScrollFinished(firstVisibleIndex))
    }

    /**
     * 事件聚焦渲染完成处理
     */
    fun onFocusEventRender() {
        state.eventSink(TimelineEvents.OnFocusEventRender)
    }

    /**
     * 跳转到实时处理
     */
    fun onJumpToLive() {
        state.eventSink(TimelineEvents.JumpToLive)
    }

    val context = LocalContext.current
    val toastMessage = stringResource(CommonStrings.common_copied_to_clipboard)
    val view = LocalView.current
    // 当 TalkBack 启用时禁用反向布局，以避免当前 Compose UI 版本中出现错误的排序问题
    val useReverseLayout = !isTalkbackActive()

    /**
     * 回复点击处理
     *
     * @param eventId 事件 ID
     */
    fun inReplyToClick(eventId: EventId) {
        state.eventSink(TimelineEvents.FocusOnEvent(eventId))
    }

    /**
     * 链接长按处理
     *
     * @param link 链接
     */
    fun onLinkLongClick(link: Link) {
        view.performHapticFeedback(
            HapticFeedbackConstants.LONG_PRESS
        )
        context.copyToClipboard(
            text = link.url,
            toastMessage = toastMessage,
        )
    }

    /**
     * 预取更多项目
     */
    fun prefetchMoreItems() {
        state.eventSink(TimelineEvents.LoadMore(Timeline.PaginationDirection.BACKWARDS))
    }

    // 首次显示时间线时为避免闪烁或故障而设置淡入动画
    AnimatedVisibility(visible = true, enter = fadeIn()) {
        Box(modifier) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection)
                    .testTag(TestTags.timeline),
                state = lazyListState,
                reverseLayout = useReverseLayout,
                contentPadding = PaddingValues(top = 64.dp, bottom = 8.dp),
            ) {
                items(
                    items = state.timelineItems,
                    contentType = { timelineItem -> timelineItem.contentType() },
                    key = { timelineItem -> timelineItem.identifier() },
                ) { timelineItem ->
                    TimelineItemRow(
                        timelineItem = timelineItem,
                        timelineMode = state.timelineMode,
                        timelineRoomInfo = state.timelineRoomInfo,
                        isLatestCallNotify = state.isLatestCallNotify((timelineItem as? TimelineItem.Event)?.eventId),
                        timelineProtectionState = timelineProtectionState,
                        renderReadReceipts = state.renderReadReceipts,
                        isLastOutgoingMessage = state.isLastOutgoingMessage(timelineItem.identifier()),
                        focusedEventId = state.focusedEventId,
                        displayThreadSummaries = state.displayThreadSummaries,
                        onUserDataClick = onUserDataClick,
                        onLinkClick = onLinkClick,
                        onLinkLongClick = ::onLinkLongClick,
                        onContentClick = onContentClick,
                        onLongClick = onMessageLongClick,
                        inReplyToClick = ::inReplyToClick,
                        onReactionClick = onReactionClick,
                        onReactionLongClick = onReactionLongClick,
                        onMoreReactionsClick = onMoreReactionsClick,
                        onReadReceiptClick = onReadReceiptClick,
                        onSwipeToReply = onSwipeToReply,
                        onJoinCallClick = onJoinCallClick,
                        eventSink = state.eventSink,
                    )
                }
            }

            /**
             * 焦点请求状态视图
             */
            FocusRequestStateView(
                focusRequestState = state.focusRequestState,
                onClearFocusRequestState = ::clearFocusRequestState
            )

            /**
             * 时间线预取助手
             */
            TimelinePrefetchingHelper(
                lazyListState = lazyListState,
                prefetch = ::prefetchMoreItems
            )

            /**
             * 时间线滚动助手
             */
            TimelineScrollHelper(
                hasAnyEvent = state.hasAnyEvent,
                lazyListState = lazyListState,
                forceJumpToBottomVisibility = forceJumpToBottomVisibility,
                newEventState = state.newEventState,
                isLive = state.isLive,
                focusRequestState = state.focusRequestState,
                onScrollFinishAt = ::onScrollFinishAt,
                onJumpToLive = ::onJumpToLive,
                onFocusEventRender = ::onFocusEventRender,
            )
        }
    }

    /**
     * 解决验证用户发送失败视图
     */
    ResolveVerifiedUserSendFailureView(state = state.resolveVerifiedUserSendFailureState)

    /**
     * 消息盾牌对话框
     */
    MessageShieldDialog(state)
}

/**
 * 消息盾牌对话框
 *
 * @param state 时间线状态
 */
@Composable
private fun MessageShieldDialog(state: TimelineState) {
    val messageShield = state.messageShieldDialogData ?: return
    AlertDialog(
        content = messageShield.toText(),
        onDismiss = { state.eventSink.invoke(TimelineEvents.HideShieldDialog) },
    )
}

/**
 * 时间线预取助手
 *
 * 在用户滚动时预取更多时间线项目以提高性能。
 *
 * @param lazyListState LazyList 状态
 * @param prefetch 预取函数
 */
@Composable
private fun TimelinePrefetchingHelper(
    lazyListState: LazyListState,
    prefetch: () -> Unit,
) {
    val latestPrefetch by rememberUpdatedState(prefetch)

    LaunchedEffect(Unit) {
        // 我们对这些使用快照流，因为使用 `LaunchedEffect` 配合 `derivedState` 响应不够及时
        val firstVisibleItemIndexFlow = snapshotFlow { lazyListState.firstVisibleItemIndex }
        val layoutInfoFlow = snapshotFlow { lazyListState.layoutInfo }
        val isScrollingFlow = snapshotFlow { lazyListState.isScrollInProgress }
            // 此值变化太频繁，因此我们对其进行防抖以避免不必要的预取。这相当于条件性的 'throttleLatest'
            .conflate()
            .transform { isScrolling ->
                emit(isScrolling)
                if (isScrolling) delay(100.milliseconds)
            }

        val isCloseToStartOfLoadedTimelineFlow = combine(layoutInfoFlow, firstVisibleItemIndexFlow) { layoutInfo, firstVisibleItemIndex ->
            firstVisibleItemIndex + layoutInfo.visibleItemsInfo.size >= layoutInfo.totalItemsCount - 40
        }

        combine(
            isCloseToStartOfLoadedTimelineFlow.distinctUntilChanged(),
            isScrollingFlow.distinctUntilChanged(),
        ) { needsPrefetch, isScrolling ->
            needsPrefetch && isScrolling
        }
            .distinctUntilChanged()
            .collectLatest { needsPrefetch ->
                if (needsPrefetch) {
                    Timber.d("Prefetching pagination with ${lazyListState.layoutInfo.totalItemsCount} items")
                    latestPrefetch()
                }
            }
    }
}

/**
 * 时间线滚动助手
 *
 * 辅助组件，处理滚动到底部、跳转到实时、焦点事件聚焦等逻辑。
 *
 * @param hasAnyEvent 是否有任何事件
 * @param lazyListState LazyList 状态
 * @param newEventState 新事件状态
 * @param isLive 是否为实时模式
 * @param forceJumpToBottomVisibility 强制显示跳转到底部按钮
 * @param focusRequestState 焦点请求状态
 * @param onScrollFinishAt 滚动完成回调
 * @param onJumpToLive 跳转到实时回调
 * @param onFocusEventRender 焦点事件渲染完成回调
 */
@Composable
private fun BoxScope.TimelineScrollHelper(
    hasAnyEvent: Boolean,
    lazyListState: LazyListState,
    newEventState: NewEventState,
    isLive: Boolean,
    forceJumpToBottomVisibility: Boolean,
    focusRequestState: FocusRequestState,
    onScrollFinishAt: (Int) -> Unit,
    onJumpToLive: () -> Unit,
    onFocusEventRender: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val isScrollFinished by remember { derivedStateOf { !lazyListState.isScrollInProgress } }
    val canAutoScroll by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex < 3 && isLive
        }
    }
    var jumpToLiveHandled by remember { mutableStateOf(true) }

    /**
     * 滚动到底部
     *
     * @param force 如果为 true，即使用户已在查看最新项目也滚动到底部。
     * 这修复了用户正在查看打字通知因此新消息到来时未发送已读回执的问题。
     */
    fun scrollToBottom(force: Boolean) {
        coroutineScope.launch {
            if (lazyListState.firstVisibleItemIndex > 10) {
                lazyListState.scrollToItem(0)
            } else if (force || lazyListState.firstVisibleItemIndex != 0) {
                lazyListState.animateScrollToItem(0)
            }
        }
    }

    /**
     * 跳转到底部
     */
    fun jumpToBottom() {
        if (isLive) {
            scrollToBottom(force = false)
        } else {
            jumpToLiveHandled = false
            onJumpToLive()
        }
    }

    LaunchedEffect(jumpToLiveHandled, isLive) {
        if (!jumpToLiveHandled && isLive) {
            lazyListState.scrollToItem(0)
            jumpToLiveHandled = true
        }
    }

    val latestOnFocusEventRender by rememberUpdatedState(onFocusEventRender)
    LaunchedEffect(focusRequestState) {
        if (focusRequestState is FocusRequestState.Success && focusRequestState.isIndexed && !focusRequestState.rendered) {
            lazyListState.animateScrollToItemCenter(focusRequestState.index)
            latestOnFocusEventRender()
        }
    }

    LaunchedEffect(canAutoScroll, newEventState) {
        val shouldScrollToBottom = isScrollFinished &&
            (canAutoScroll && newEventState == NewEventState.FromOther || newEventState == NewEventState.FromMe)
        if (shouldScrollToBottom) {
            scrollToBottom(force = true)
        }
    }

    val latestOnScrollFinishAt by rememberUpdatedState(onScrollFinishAt)
    LaunchedEffect(isScrollFinished, hasAnyEvent) {
        if (isScrollFinished && hasAnyEvent) {
            // 滚动完成时通知父Composable第一个可见项索引
            latestOnScrollFinishAt(lazyListState.firstVisibleItemIndex)
        }
    }

    /**
     * 跳转到底部按钮
     */
    JumpToBottomButton(
        // 使用 canAutoScroll 的逆值，否则我们可能会在滚动动画触发前短暂看到
        isVisible = !canAutoScroll || forceJumpToBottomVisibility || !isLive,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 24.dp, bottom = 12.dp),
        onClick = { jumpToBottom() },
    )
}

/**
 * 跳转到底部按钮
 *
 * @param isVisible 是否可见
 * @param onClick 点击事件
 * @param modifier 修饰符
 */
@Composable
private fun JumpToBottomButton(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = scaleIn(animationSpec = tween(100)),
        exit = scaleOut(animationSpec = tween(100)),
    ) {
        FloatingActionButton(
            onClick = onClick,
            elevation = FloatingActionButtonDefaults.elevation(4.dp, 4.dp, 4.dp, 4.dp),
            shape = CircleShape,
            modifier = Modifier.size(36.dp),
            containerColor = ElementTheme.colors.bgSubtleSecondary,
            contentColor = ElementTheme.colors.iconSecondary,
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .rotate(90f),
                imageVector = CompoundIcons.ArrowRight(),
                contentDescription = stringResource(id = CommonStrings.a11y_jump_to_bottom)
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun TimelineViewPreview(
    @PreviewParameter(TimelineItemEventContentProvider::class) content: TimelineItemEventContent
) = ElementPreview {
    val timelineItems = aTimelineItemList(content)
    val timelineEvents = timelineItems.filterIsInstance<TimelineItem.Event>()
    val lastEventIdFromMe = timelineEvents.firstOrNull { it.isMine }?.eventId
    val lastEventIdFromOther = timelineEvents.firstOrNull { !it.isMine }?.eventId
    CompositionLocalProvider(
        LocalTimelineItemPresenterFactories provides aFakeTimelineItemPresenterFactories(),
    ) {
        TimelineView(
            state = aTimelineState(
                timelineItems = timelineItems,
                timelineRoomInfo = aTimelineRoomInfo(
                    pinnedEventIds = listOfNotNull(lastEventIdFromMe, lastEventIdFromOther)
                ),
                focusedEventIndex = 0,
            ),
            timelineProtectionState = aTimelineProtectionState(),
            onUserDataClick = {},
            onLinkClick = {},
            onContentClick = {},
            onMessageLongClick = {},
            onSwipeToReply = {},
            onReactionClick = { _, _ -> },
            onReactionLongClick = { _, _ -> },
            onMoreReactionsClick = {},
            onReadReceiptClick = {},
            onJoinCallClick = {},
            forceJumpToBottomVisibility = true,
        )
    }
}
