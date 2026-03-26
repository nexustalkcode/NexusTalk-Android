/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.analytics.api.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.features.analytics.api.AnalyticsOptInEvents
import io.element.android.features.analytics.api.R
import io.element.android.libraries.designsystem.components.LINK_TAG
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.text.buildAnnotatedStringWithStyledPart
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.ListSupportingText
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * 分析偏好设置视图组件
 *
 * 使用 Jetpack Compose 实现分析偏好设置的用户界面。
 * 展示分析功能的开关和隐私政策链接。
 *
 * @param state 当前视图状态
 * @param modifier 修饰符
 */
@Composable
fun AnalyticsPreferencesView(
    state: AnalyticsPreferencesState,
    modifier: Modifier = Modifier,
) {
    fun onEnabledChanged(isEnabled: Boolean) {
        state.eventSink(AnalyticsOptInEvents.EnableAnalytics(isEnabled = isEnabled))
    }

    val supportingText = stringResource(
        id = R.string.screen_analytics_settings_help_us_improve,
        state.applicationName
    )
    Column(modifier) {
        ListItem(
            headlineContent = {
                Text(stringResource(id = R.string.screen_analytics_settings_share_data))
            },
            supportingContent = {
                Text(supportingText)
            },
            leadingContent = null,
            trailingContent = ListItemContent.Switch(
                checked = state.isEnabled,
            ),
            onClick = {
                onEnabledChanged(!state.isEnabled)
            }
        )
        if (state.policyUrl.isNotEmpty()) {
            val linkText = buildAnnotatedStringWithStyledPart(
                R.string.screen_analytics_settings_read_terms,
                R.string.screen_analytics_settings_read_terms_content_link,
                tagAndLink = LINK_TAG to state.policyUrl,
            )
            ListSupportingText(annotatedString = linkText)
        }
    }
}

/**
 * 分析偏好设置视图预览组件
 *
 * 用于在 Android Studio 预览中展示偏好设置视图 UI。
 *
 * @param state 预览状态
 */
@PreviewsDayNight
@Composable
internal fun AnalyticsPreferencesViewPreview(@PreviewParameter(AnalyticsPreferencesStateProvider::class) state: AnalyticsPreferencesState) =
    ElementPreview {
        AnalyticsPreferencesView(
            state = state,
        )
    }
