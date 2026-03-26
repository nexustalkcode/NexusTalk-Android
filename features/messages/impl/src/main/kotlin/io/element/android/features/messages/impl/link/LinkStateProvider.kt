/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.link

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.wysiwyg.link.Link

/**
 * 链接状态提供者
 *
 * 用于预览功能的参数提供者，提供各种链接状态的示例。
 * 继承自PreviewParameterProvider，用于Compose预览功能。
 */
open class LinkStateProvider : PreviewParameterProvider<LinkState> {
    override val values: Sequence<LinkState>
        get() = sequenceOf(
            aLinkState(),
            aLinkState(
                linkClick = ConfirmingLinkClick(
                    Link(
                        url = "https://evil.io",
                        text = "https://element.io"
                    ),
                ),
            ),
        )
}

/**
 * 创建链接状态的辅助函数
 *
 * 用于在测试和预览中快速创建LinkState实例。
 *
 * @param linkClick 链接点击的异步操作状态，默认为未初始化
 * @param eventSink 事件处理函数，默认为空函数
 * @return 新的LinkState实例
 */
fun aLinkState(
    linkClick: AsyncAction<Link> = AsyncAction.Uninitialized,
    eventSink: (LinkEvents) -> Unit = {},
) = LinkState(
    linkClick = linkClick,
    eventSink = eventSink,
)
