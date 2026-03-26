/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.pinned.banner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import im.vector.app.features.analytics.plan.Interaction
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.designsystem.theme.pinnedMessageBannerBorder
import io.element.android.libraries.designsystem.theme.pinnedMessageBannerIndicator
import io.element.android.libraries.designsystem.utils.annotatedTextWithBold
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.services.analytics.compose.LocalAnalyticsService
import io.element.android.services.analyticsproviders.api.trackers.captureInteraction

/**
 * 固定消息横幅视图
 *
 * Compose Composable函数，用于渲染固定消息横幅界面。
 * 根据当前状态显示或隐藏横幅，并处理用户交互。
 *
 * @param state 固定消息横幅的当前状态
 * @param onClick 点击横幅时的回调，参数为事件ID
 * @param onViewAllClick 点击"查看全部"按钮时的回调
 * @param modifier Compose修饰符，用于自定义样式和布局
 *
 * @see PinnedMessagesBannerState 固定消息横幅状态
 * @see EventId 事件ID
 */
@Composable
fun PinnedMessagesBannerView(
    state: PinnedMessagesBannerState,
    onClick: (EventId) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        // 隐藏状态，不渲染任何内容
        PinnedMessagesBannerState.Hidden -> Unit
        // 可见状态，显示横幅内容
        is PinnedMessagesBannerState.Visible -> {
            PinnedMessagesBannerRow(
                state = state,
                onClick = onClick,
                onViewAllClick = onViewAllClick,
                modifier = modifier,
            )
        }
    }
}

/**
 * 固定消息横幅行组件
 *
 * 渲染固定消息横幅的主要行布局，包含：
 * - 位置指示器（显示当前消息在列表中的位置）
 * - 图钉图标
 * - 消息内容
 * - "查看全部"按钮
 *
 * @param state 可见状态下的固定消息横幅状态
 * @param onClick 点击事件回调，参数为当前消息的事件ID
 * @param onViewAllClick 点击"查看全部"按钮回调
 * @param modifier Compose修饰符
 */
@Composable
private fun PinnedMessagesBannerRow(
    state: PinnedMessagesBannerState.Visible,
    onClick: (EventId) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val analyticsService = LocalAnalyticsService.current
    val borderColor = ElementTheme.colors.pinnedMessageBannerBorder
    Row(
        modifier = modifier
            .background(color = ElementTheme.colors.bgCanvasDefault)
            .fillMaxWidth()
            .drawBorder(borderColor)
            .heightIn(min = 64.dp)
            .clickable {
                if (state is PinnedMessagesBannerState.Loaded) {
                    analyticsService.captureInteraction(Interaction.Name.PinnedMessageBannerClick)
                    onClick(state.currentPinnedMessage.eventId)
                    state.eventSink(PinnedMessagesBannerEvents.MoveToNextPinned)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(26.dp))
        PinIndicators(
            pinIndex = state.currentPinnedMessageIndex(),
            pinsCount = state.pinnedMessagesCount(),
        )
        Icon(
            imageVector = CompoundIcons.PinSolid(),
            contentDescription = null,
            tint = ElementTheme.colors.iconSecondary,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(20.dp)
        )
        PinnedMessageItem(
            index = state.currentPinnedMessageIndex(),
            totalCount = state.pinnedMessagesCount(),
            message = state.formattedMessage(),
            modifier = Modifier.weight(1f)
        )
        ViewAllButton(
            state = state,
            onViewAllClick = {
                onViewAllClick()
                analyticsService.captureInteraction(Interaction.Name.PinnedMessageBannerViewAllButton)
            },
        )
    }
}

/**
 * "查看全部"按钮组件
 *
 * 渲染"查看全部"按钮，用于跳转到完整的置顶消息列表。
 * 按钮文字和加载状态根据当前状态动态显示。
 *
 * @param state 固定消息横幅状态
 * @param onViewAllClick 点击按钮回调
 * @param modifier Compose修饰符
 */
@Composable
private fun ViewAllButton(
    state: PinnedMessagesBannerState,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val text = if (state is PinnedMessagesBannerState.Loaded) {
        stringResource(id = CommonStrings.screen_room_pinned_banner_view_all_button_title)
    } else {
        ""
    }
    TextButton(
        text = text,
        showProgress = state is PinnedMessagesBannerState.Loading,
        onClick = onViewAllClick,
        modifier = modifier,
    )
}

private fun Modifier.drawBorder(borderColor: Color): Modifier {
    return this
        .drawBehind {
            val strokeWidth = 0.5.dp.toPx()
            val y = size.height - strokeWidth / 2
            drawLine(
                borderColor,
                Offset(0f, y),
                Offset(size.width, y),
                strokeWidth
            )
            drawLine(
                borderColor,
                Offset(0f, 0f),
                Offset(size.width, 0f),
                strokeWidth
            )
        }
        .shadow(elevation = 5.dp, spotColor = Color.Transparent)
}

/**
 * 位置指示器组件
 *
 * 渲染一列小圆点指示器，用于显示当前查看的置顶消息位置。
 * 指示器的数量和高度根据总消息数动态调整：
 * - 1条消息：显示1个较高的指示器
 * - 2条消息：显示2个中等高度的指示器
 * - 3条及以上：显示3个较矮的指示器
 *
 * @param pinIndex 当前查看的消息索引（0-based）
 * @param pinsCount 置顶消息总数
 * @param modifier Compose修饰符
 */
@Composable
private fun PinIndicators(
    pinIndex: Int,
    pinsCount: Int,
    modifier: Modifier = Modifier,
) {
    val indicatorHeight = remember(pinsCount) {
        when (pinsCount) {
            0 -> 0
            1 -> 32
            2 -> 18
            else -> 11
        }
    }
    val activeIndex = remember(pinIndex) {
        pinIndex % 3
    }
    val shownIndicators = remember(pinsCount, pinIndex) {
        if (pinsCount <= 3) {
            pinsCount
        } else {
            val isLastPage = pinIndex >= pinsCount - pinsCount % 3
            if (isLastPage) {
                pinsCount % 3
            } else {
                3
            }
        }
    }
    val indicatorsCount = pinsCount.coerceAtMost(3)

    Column(
        modifier = modifier,
        verticalArrangement = spacedBy(2.dp)
    ) {
        for (index in 0 until indicatorsCount) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(indicatorHeight.dp)
                    .background(
                        color = if (index == activeIndex) {
                            ElementTheme.colors.iconAccentPrimary
                        } else if (index < shownIndicators) {
                            ElementTheme.colors.pinnedMessageBannerIndicator
                        } else {
                            Color.Transparent
                        }
                    ),
            )
        }
    }
}

/**
 * 置顶消息项目组件
 *
 * 渲染单个置顶消息的内容，包括：
 * - 位置信息（如 "1 / 5"），仅在有多条消息时显示
 * - 消息文本内容
 *
 * @param index 当前消息索引（0-based）
 * @param totalCount 消息总数
 * @param message 格式化后的消息内容
 * @param modifier Compose修饰符
 */
@Composable
private fun PinnedMessageItem(
    index: Int,
    totalCount: Int,
    message: AnnotatedString?,
    modifier: Modifier = Modifier,
) {
    val countMessage = stringResource(id = CommonStrings.screen_room_pinned_banner_indicator, index + 1, totalCount)
    val fullCountMessage = stringResource(id = CommonStrings.screen_room_pinned_banner_indicator_description, countMessage)
    Column(modifier = modifier) {
        AnimatedVisibility(totalCount > 1) {
            Text(
                text = annotatedTextWithBold(
                    text = fullCountMessage,
                    boldText = countMessage,
                ),
                style = ElementTheme.typography.fontBodySmMedium,
                color = ElementTheme.colors.textActionAccent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (message != null) {
            Text(
                text = message,
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textPrimary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
}

/**
 * 固定消息横幅滚动行为接口
 *
 * 定义横幅在滚动时的可见性行为。
 * 用于实现滚动时自动隐藏/显示横幅的效果。
 *
 * @property isVisible 横幅当前是否可见
 * @property nestedScrollConnection 嵌套滚动连接，用于处理滚动事件
 *
 * @see NestedScrollConnection 嵌套滚动连接
 */
@Stable
internal interface PinnedMessagesBannerViewScrollBehavior {
    val isVisible: Boolean
    val nestedScrollConnection: NestedScrollConnection
}

/**
 * 固定消息横幅视图默认值对象
 *
 * 提供创建滚动行为的默认方法。
 */
internal object PinnedMessagesBannerViewDefaults {
    /**
     * 创建并记住滚动行为
     *
     * @param pinnedMessagesCount 置顶消息数量
     * @return PinnedMessagesBannerViewScrollBehavior 滚动行为实例
     */
    @Composable
    fun rememberScrollBehavior(pinnedMessagesCount: Int): PinnedMessagesBannerViewScrollBehavior = remember(pinnedMessagesCount) {
        ExitOnScrollBehavior()
    }
}

/**
 * 滚动退出行为实现类
 *
 * 实现滚动时自动隐藏横幅的行为：
 * - 向上滚动时显示横幅
 * - 向下滚动时隐藏横幅
 *
 * @see PinnedMessagesBannerViewScrollBehavior 滚动行为接口
 */
private class ExitOnScrollBehavior : PinnedMessagesBannerViewScrollBehavior {
    override var isVisible by mutableStateOf(true)
    override val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y < -1) {
                isVisible = true
            }
            if (available.y > 1) {
                isVisible = false
            }
            return Offset.Zero
        }
    }
}

@PreviewsDayNight
@Composable
internal fun PinnedMessagesBannerViewPreview(@PreviewParameter(PinnedMessagesBannerStateProvider::class) state: PinnedMessagesBannerState) = ElementPreview {
    PinnedMessagesBannerView(
        state = state,
        onClick = {},
        onViewAllClick = {},
    )
}
