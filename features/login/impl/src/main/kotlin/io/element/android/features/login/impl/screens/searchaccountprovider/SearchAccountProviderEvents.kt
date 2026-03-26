/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.searchaccountprovider

/**
 * 搜索账户提供商事件接口
 *
 * 定义搜索账户提供商流程中触发的事件。
 */
sealed interface SearchAccountProviderEvents {
    /**
     * 用户输入事件
     *
     * 用户在搜索框中输入内容时触发。
     * 期望在状态中获取匹配的账户提供商结果列表。
     *
     * @property input 用户输入的搜索文本
     */
    data class UserInput(val input: String) : SearchAccountProviderEvents
}
