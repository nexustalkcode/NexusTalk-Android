/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.model.event

import androidx.compose.runtime.Immutable
import org.jsoup.nodes.Document

/**
 * 所有文本类时间线事件内容模型的共同接口。
 */
@Immutable
sealed interface TimelineItemTextBasedContent :
    TimelineItemEventContent,
    TimelineItemEventMutableContent {
    /** Markdown 语义上的原始正文。 */
    val body: String

    /** 已解析的富文本 HTML DOM。 */
    val htmlDocument: Document?

    /** 已转换为 Android spans 的富文本正文。 */
    val formattedBody: CharSequence

    /** 去除格式后的纯文本正文。 */
    val plainText: String

    /** 原始 HTML 正文。 */
    val htmlBody: String?
        get() = htmlDocument?.body()?.html()
}
