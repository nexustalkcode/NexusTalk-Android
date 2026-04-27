/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.analytics.impl

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.appconfig.AnalyticsConfig
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.analytics.api.AnalyticsOptInEvents
import io.element.android.features.analytics.api.R
import io.element.android.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.atomic.organisms.InfoListItem
import io.element.android.libraries.designsystem.atomic.organisms.InfoListOrganism
import io.element.android.libraries.designsystem.atomic.pages.HeaderFooterPage
import io.element.android.libraries.designsystem.background.OnboardingBackground
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.components.ClickableLinkText
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.text.buildAnnotatedStringWithStyledPart
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.persistentListOf

/**
 * 分析功能选择视图组件
 *
 * 使用 Jetpack Compose 实现分析功能选择的用户界面。
 * 展示分析功能的介绍、隐私政策和接受/拒绝按钮。
 * 按返回键会拒绝分析。
 *
 * @param state 当前视图状态
 * @param onClickTerms 点击隐私政策链接回调
 * @param modifier 修饰符
 */
@Composable
fun AnalyticsOptInView(
    state: AnalyticsOptInState,
    onClickTerms: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val eventSink = state.eventSink

    fun onAcceptTerms() {
        eventSink(AnalyticsOptInEvents.EnableAnalytics(true))
    }

    fun onDeclineTerms() {
        eventSink(AnalyticsOptInEvents.EnableAnalytics(false))
    }

    BackHandler(onBack = ::onDeclineTerms)
    HeaderFooterPage(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        background = { OnboardingBackground() },
        header = { AnalyticsOptInHeader(state, onClickTerms) },
        content = { AnalyticsOptInContent() },
        footer = {
            AnalyticsOptInFooter(
                onAcceptTerms = ::onAcceptTerms,
                onDeclineTerms = ::onDeclineTerms,
            )
        }
    )
}

/** 链接标签常量 */
private const val LINK_TAG = "link"

/**
 * 分析功能选择视图头部组件
 *
 * 展示应用名称、介绍文字和隐私政策链接。
 *
 * @param state 当前视图状态
 * @param onClickTerms 点击隐私政策链接回调
 */
@Composable
private fun AnalyticsOptInHeader(
    state: AnalyticsOptInState,
    onClickTerms: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconTitleSubtitleMolecule(
            modifier = Modifier.padding(top = 60.dp, bottom = 28.dp),
            title = stringResource(id = R.string.screen_analytics_prompt_title, state.applicationName),
            subTitle = stringResource(id = R.string.screen_analytics_prompt_help_us_improve),
            iconStyle = BigIcon.Style.Default(CompoundIcons.Chart())
        )
        if (state.hasPolicyLink) {
            val text = buildAnnotatedStringWithStyledPart(
                R.string.screen_analytics_prompt_read_terms,
                R.string.screen_analytics_prompt_read_terms_content_link,
                color = Color.Unspecified,
                underline = false,
                bold = true,
                tagAndLink = LINK_TAG to AnalyticsConfig.POLICY_LINK,
            )
            ClickableLinkText(
                annotatedString = text,
                onClick = { onClickTerms() },
                modifier = Modifier
                    .padding(8.dp),
                style = ElementTheme.typography.fontBodyMdRegular
                    .copy(
                        color = ElementTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                    )
            )
        }
    }
}

/**
 * 分析功能选择视图内容组件
 *
 * 展示分析功能的数据使用说明列表。
 */
@Composable
private fun AnalyticsOptInContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = BiasAlignment(
            horizontalBias = 0f,
            verticalBias = -0.4f
        )
    ) {
        InfoListOrganism(
            items = persistentListOf(
                InfoListItem(
                    message = stringResource(id = R.string.screen_analytics_prompt_data_usage),
                    iconVector = CompoundIcons.CheckCircle(),
                ),
                InfoListItem(
                    message = stringResource(id = R.string.screen_analytics_prompt_third_party_sharing),
                    iconVector = CompoundIcons.CheckCircle(),
                ),
                InfoListItem(
                    message = stringResource(id = R.string.screen_analytics_prompt_settings),
                    iconVector = CompoundIcons.CheckCircle(),
                ),
            ),
            textStyle = ElementTheme.typography.fontBodyLgMedium,
            iconTint = ElementTheme.colors.iconSuccessPrimary,
        )
    }
}

/**
 * 分析功能选择视图底部组件
 *
 * 展示接受和拒绝按钮。
 *
 * @param onAcceptTerms 接受分析按钮点击回调
 * @param onDeclineTerms 拒绝分析按钮点击回调
 */
@Composable
private fun AnalyticsOptInFooter(
    onAcceptTerms: () -> Unit,
    onDeclineTerms: () -> Unit,
) {
    ButtonColumnMolecule {
        Button(
            text = stringResource(id = CommonStrings.action_ok),
            onClick = onAcceptTerms,
            modifier = Modifier.fillMaxWidth(),
        )
        TextButton(
            text = stringResource(id = CommonStrings.action_not_now),
            size = ButtonSize.Medium,
            onClick = onDeclineTerms,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * 分析功能选择视图预览组件
 *
 * 用于在 Android Studio 预览中展示分析选择视图 UI。
 *
 * @param state 预览状态
 */
@PreviewsDayNight
@Composable
internal fun AnalyticsOptInViewPreview(@PreviewParameter(AnalyticsOptInStateProvider::class) state: AnalyticsOptInState) = ElementPreview {
    AnalyticsOptInView(
        state = state,
        onClickTerms = {},
    )
}
