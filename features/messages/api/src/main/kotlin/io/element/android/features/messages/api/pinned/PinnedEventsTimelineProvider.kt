/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.api.pinned

import io.element.android.libraries.matrix.api.timeline.TimelineProvider

/**
 * 固定事件时间线提供器接口
 *
 * 继承自 TimelineProvider，用于提供群聊中固定（置顶）消息的时间线数据。
 * 允许应用访问和管理已固定的消息事件。
 *
 * @see TimelineProvider 时间线提供器基类
 */
interface PinnedEventsTimelineProvider : TimelineProvider
