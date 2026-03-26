/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.pinned.banner

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.matrix.api.core.EventId
import kotlin.random.Random

/**
 * 固定消息横幅状态提供器
 *
 * 用于预览（Preview）功能的参数提供器。
 * 生成多种固定消息横幅状态的示例数据，用于UI预览和测试。
 *
 * @see PreviewParameterProvider Compose预览参数提供器接口
 * @see PinnedMessagesBannerState 固定消息横幅状态
 */
internal class PinnedMessagesBannerStateProvider : PreviewParameterProvider<PinnedMessagesBannerState> {
    /**
     * 生成状态序列
     *
     * 提供多种不同状态的示例，用于预览和测试：
     * - 隐藏状态
     * - 加载中状态（1条和5条消息）
     * - 已加载状态（不同消息数量和索引）
     *
     * @return Sequence<PinnedMessagesBannerState> 状态序列
     */
    override val values: Sequence<PinnedMessagesBannerState>
        get() = sequenceOf(
            aHiddenPinnedMessagesBannerState(),
            aLoadingPinnedMessagesBannerState(knownPinnedMessagesCount = 1),
            aLoadingPinnedMessagesBannerState(knownPinnedMessagesCount = 5),
            aLoadedPinnedMessagesBannerState(knownPinnedMessagesCount = 1, currentPinnedMessageIndex = 0),
            aLoadedPinnedMessagesBannerState(
                knownPinnedMessagesCount = 2,
                currentPinnedMessageIndex = 0,
                message = "This is a pinned long message to check the wrapping behavior",
            ),
            aLoadedPinnedMessagesBannerState(knownPinnedMessagesCount = 3, currentPinnedMessageIndex = 0),
            aLoadedPinnedMessagesBannerState(knownPinnedMessagesCount = 5, currentPinnedMessageIndex = 0),
            aLoadedPinnedMessagesBannerState(knownPinnedMessagesCount = 5, currentPinnedMessageIndex = 1),
            aLoadedPinnedMessagesBannerState(knownPinnedMessagesCount = 5, currentPinnedMessageIndex = 2),
            aLoadedPinnedMessagesBannerState(knownPinnedMessagesCount = 5, currentPinnedMessageIndex = 3),
            aLoadedPinnedMessagesBannerState(knownPinnedMessagesCount = 5, currentPinnedMessageIndex = 4),
        )
}

/**
 * 创建隐藏状态的固定消息横幅
 *
 * @return PinnedMessagesBannerState.Hidden 隐藏状态
 */
internal fun aHiddenPinnedMessagesBannerState() = PinnedMessagesBannerState.Hidden

/**
 * 创建加载状态的固定消息横幅
 *
 * @param knownPinnedMessagesCount 已知的置顶消息数量，默认值为4
 * @return PinnedMessagesBannerState.Loading 加载状态，包含预期消息数量
 */
internal fun aLoadingPinnedMessagesBannerState(
    knownPinnedMessagesCount: Int = 4
) = PinnedMessagesBannerState.Loading(
    expectedPinnedMessagesCount = knownPinnedMessagesCount
)

/**
 * 创建已加载状态的固定消息横幅
 *
 * @param currentPinnedMessageIndex 当前显示的消息索引，默认值为0
 * @param knownPinnedMessagesCount 已知的消息总数，默认值为1
 * @param message 消息内容，默认值为"This is a pinned message"
 * @param currentPinnedMessage 当前显示的置顶消息项目
 * @param eventSink 事件处理函数，默认空实现
 * @return PinnedMessagesBannerState.Loaded 已加载状态
 */
internal fun aLoadedPinnedMessagesBannerState(
    currentPinnedMessageIndex: Int = 0,
    knownPinnedMessagesCount: Int = 1,
    message: String = "This is a pinned message",
    currentPinnedMessage: PinnedMessagesBannerItem = PinnedMessagesBannerItem(
        eventId = EventId("\$" + Random.nextInt().toString()),
        formatted = AnnotatedString(message)
    ),
    eventSink: (PinnedMessagesBannerEvents) -> Unit = {}
) = PinnedMessagesBannerState.Loaded(
    currentPinnedMessage = currentPinnedMessage,
    currentPinnedMessageIndex = currentPinnedMessageIndex,
    loadedPinnedMessagesCount = knownPinnedMessagesCount,
    eventSink = eventSink
)
