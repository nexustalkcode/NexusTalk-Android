/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.link

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.libraries.core.data.tryOrNull
import io.element.android.libraries.core.extensions.containsRtLOverride
import io.element.android.wysiwyg.link.Link
import java.net.URI

/**
 * 链接检查器接口
 *
 * 定义链接安全性检查操作接口。
 * 用于检测恶意链接或钓鱼链接。
 *
 * @see Link 链接数据类
 */
interface LinkChecker {
    /**
     * 检查链接是否安全
     *
     * @param link 要检查的链接
     * @return 是否安全
     */
    fun isSafe(link: Link): Boolean
}

/**
 * 默认链接检查器实现类
 *
 * 实现 LinkChecker 接口，通过以下方式检查链接安全性：
 * 1. 检查是否包含 RTL 覆盖字符
 * 2. 验证链接文本和URL的主机名是否匹配
 *
 * 使用 @ContributesBinding 注解绑定到 AppScope。
 *
 * @see LinkChecker 链接检查器接口
 */
@ContributesBinding(AppScope::class)
class DefaultLinkChecker : LinkChecker {
    override fun isSafe(link: Link): Boolean {
        return if (link.url.containsRtLOverride()) {
            false
        } else {
            val textUrl = tryOrNull { URI(link.text).toURL() }
            val urlUrl = tryOrNull { URI(link.url).toURL() }
            if (textUrl == null || urlUrl == null) {
                // The text is not a Url, or the url is not valid
                true
            } else {
                // the hosts must match
                textUrl.host == urlUrl.host
            }
        }
    }
}
