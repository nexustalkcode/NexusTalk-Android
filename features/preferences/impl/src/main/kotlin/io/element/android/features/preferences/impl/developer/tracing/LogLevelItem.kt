/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.developer.tracing

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.element.android.features.preferences.impl.R
import io.element.android.libraries.designsystem.components.preferences.DropdownOption

/**
 * 日志级别枚举
 *
 * 定义追踪日志的不同级别，按严重程度从低到高排列。
 */
enum class LogLevelItem : DropdownOption {
    /** 错误级别 - 仅记录错误信息 */
    ERROR {
        @Composable
        override fun getText(): String = stringResource(R.string.screen_developer_settings_tracing_log_level_error)
    },
    /** 警告级别 - 记录警告和错误信息 */
    WARN {
        @Composable
        override fun getText(): String = stringResource(R.string.screen_developer_settings_tracing_log_level_warn)
    },
    /** 信息级别 - 记录一般信息、警告和错误 */
    INFO {
        @Composable
        override fun getText(): String = stringResource(R.string.screen_developer_settings_tracing_log_level_info)
    },
    /** 调试级别 - 记录调试信息及以上所有级别 */
    DEBUG {
        @Composable
        override fun getText(): String = stringResource(R.string.screen_developer_settings_tracing_log_level_debug)
    },
    /** 追踪级别 - 记录最详细的追踪信息 */
    TRACE {
        @Composable
        override fun getText(): String = stringResource(R.string.screen_developer_settings_tracing_log_level_trace)
    }
}
