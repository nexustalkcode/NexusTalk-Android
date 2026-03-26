/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

/**
 * 语言标签提供者接口
 *
 * 提供当前系统语言标签，用于设置 Element Call 的语言。
 *
 * @see DefaultLanguageTagProvider 默认实现
 */
interface LanguageTagProvider {
    /**
     * 获取当前系统语言标签
     *
     * @return String? 语言标签字符串，如 "zh-CN"，如果无法获取则返回 null
     */
    @Composable
    fun provideLanguageTag(): String?
}

/**
 * 语言标签提供者默认实现
 *
 * 从 Android 系统配置中获取当前语言标签。
 *
 * @see LanguageTagProvider 语言标签提供者接口
 */
@ContributesBinding(AppScope::class)
class DefaultLanguageTagProvider : LanguageTagProvider {
    /**
     * 获取当前系统语言标签
     *
     * 从系统配置中获取第一个语言设置并转换为语言标签格式。
     *
     * @return String? 语言标签字符串，如 "zh-CN"，如果无法获取则返回 null
     */
    @Composable
    override fun provideLanguageTag(): String? {
        return LocalConfiguration.current.locales.get(0)?.toLanguageTag()
    }
}
