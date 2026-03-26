/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.link

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.wysiwyg.link.Link

/**
 * 链接Presenter类
 *
 * 负责管理链接点击处理和安全性检查的业务逻辑。
 * 使用 @Inject 注解实现依赖注入。
 *
 * @property linkChecker 链接检查器
 *
 * @see Presenter Presenter基类
 * @see LinkState 链接状态
 * @see LinkChecker 链接检查器
 */
/**
 * 链接Presenter
 *
 * 负责管理链接点击处理和安全性检查的业务逻辑。
 * 当用户点击链接时，使用LinkChecker检查链接是否安全：
 * - 如果安全，直接执行链接
 * - 如果不安全，显示确认对话框让用户决定是否继续
 *
 * @property linkChecker 链接检查器，用于验证链接安全性
 *
 * @see LinkState 链接状态
 * @see LinkChecker 链接检查器
 * @see LinkEvents 链接事件
 */
@Inject
class LinkPresenter(
    private val linkChecker: LinkChecker,
) : Presenter<LinkState> {
    @Composable
    override fun present(): LinkState {
        val linkClick: MutableState<AsyncAction<Link>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        fun handleEvent(event: LinkEvents) {
            when (event) {
                is LinkEvents.OnLinkClick -> {
                    linkClick.value = AsyncAction.Loading
                    val result = linkChecker.isSafe(event.link)
                    if (result) {
                        linkClick.value = AsyncAction.Success(event.link)
                    } else {
                        // Confirm first
                        linkClick.value = ConfirmingLinkClick(event.link)
                    }
                }
                LinkEvents.Confirm -> {
                    linkClick.value = (linkClick.value as? ConfirmingLinkClick)
                        ?.let { AsyncAction.Success(it.link) }
                        ?: AsyncAction.Uninitialized
                }
                LinkEvents.Cancel -> {
                    linkClick.value = AsyncAction.Uninitialized
                }
            }
        }
        return LinkState(
            linkClick = linkClick.value,
            eventSink = ::handleEvent,
        )
    }
}
