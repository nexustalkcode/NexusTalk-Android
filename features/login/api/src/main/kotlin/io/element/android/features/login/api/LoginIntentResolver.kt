/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.api

/**
 * 登录意图解析器接口
 *
 * 定义解析登录 URI 字符串的功能接口。
 * 用于将 deep link 或自定义 URI 转换为登录参数。
 *
 * @see LoginParams 登录参数
 */
interface LoginIntentResolver {
    /**
     * 解析 URI 字符串为登录参数
     *
     * @param uriString URI 字符串
     * @return 解析后的登录参数，如果无法解析则返回 null
     */
    fun parse(uriString: String): LoginParams?
}
