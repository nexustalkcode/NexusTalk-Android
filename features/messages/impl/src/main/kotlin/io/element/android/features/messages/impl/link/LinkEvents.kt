/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.link

import io.element.android.wysiwyg.link.Link

/**
 * 链接事件密封接口
 *
 * 定义链接处理过程中的各种事件类型。
 *
 * @see Link 链接数据类
 */
sealed interface LinkEvents {
    /**
     * 链接点击事件
     *
     * @property link 被点击的链接
     */
    data class OnLinkClick(val link: Link) : LinkEvents

    /** 确认事件 */
    data object Confirm : LinkEvents

    /** 取消事件 */
    data object Cancel : LinkEvents
}
