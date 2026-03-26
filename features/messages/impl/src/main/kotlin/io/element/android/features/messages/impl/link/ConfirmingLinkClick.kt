/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.link

import io.element.android.libraries.architecture.AsyncAction
import io.element.android.wysiwyg.link.Link

/**
 * 待确认链接点击数据类
 *
 * 当链接安全性检查未通过时，使用此类表示需要用户确认的链接点击事件。
 * 继承自 AsyncAction.Confirming 接口。
 *
 * @property link 待确认的链接
 *
 * @see AsyncAction 异步操作基类
 * @see Link 链接数据类
 */
data class ConfirmingLinkClick(
    val link: Link,
) : AsyncAction.Confirming
