/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import androidx.activity.compose.LocalActivity
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.plugin.Plugin
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import io.element.android.annotations.ContributesNode
import io.element.android.compound.theme.ElementTheme
import androidx.compose.ui.res.stringResource
import io.element.android.appconfig.R as AppConfigR
import io.element.android.features.ftue.impl.R
import io.element.android.libraries.androidutils.browser.openUrlInChromeCustomTab
import io.element.android.libraries.architecture.NodeInputs
import io.element.android.libraries.architecture.inputs
import io.element.android.libraries.designsystem.components.button.GradientButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.text.buildAnnotatedStringWithStyledPart
import io.element.android.libraries.ui.common.nodes.emptyNode

/**
 * 欢迎页面节点
 *
 * 显示应用欢迎界面，包含图标、标题、副标题和继续按钮。
 * 用户点击 Continue 按钮后调用回调完成引导步骤。
 */
@ContributesNode(AppScope::class)
@AssistedInject
class WelcomeNode(
    @Assisted buildContext: BuildContext,
    @Assisted plugins: List<Plugin>,
) : Node(buildContext, plugins = plugins) {
    /**
     * 欢迎页面回调接口
     *
     * 定义欢迎页面完成后的回调方法。
     */
    interface Callback : NodeInputs {
        /**
         * 用户点击继续按钮完成欢迎页面
         */
        fun onDone()

        /**
         * 用户点击隐私政策链接
         */
        fun onPrivacyPolicyClick()
    }

    private fun onClickPrivacyPolicy(activity: Activity, darkTheme: Boolean) {
        activity.openUrlInChromeCustomTab(null, darkTheme, "https://www.baidu.com/")
    }

    @Composable
    override fun View(modifier: Modifier) {
        val activity = LocalActivity.current
        if (activity == null) {
            // Activity 未准备好，显示空界面或处理这种情况
            return
        }
        val isDark = ElementTheme.isLightTheme.not()
        val callback = inputs<Callback>()
        WelcomeView(
            onContinueClick = { callback.onDone() },
            onPrivacyPolicyClick = { onClickPrivacyPolicy(activity, isDark) },
            modifier = modifier
        )
    }
}

/**
 * 欢迎页面视图组件
 *
 * @param onContinueClick 点击继续按钮事件
 * @param onPrivacyPolicyClick 点击隐私政策事件
 * @param modifier 修饰符
 */
@Composable
fun WelcomeView(
    onContinueClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Logo 图标 - 复用 ic_launcher_foreground
        Image(
            painter = painterResource(id = io.element.android.appicon.element.R.mipmap.ic_launcher_foreground),
            contentDescription = "NexusTalk Logo",
            modifier = Modifier.size(100.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))
        // 标题
        Text(
            text = stringResource(AppConfigR.string.app_name),
            style = ElementTheme.typography.fontHeadingLgBold.copy(
                fontSize = 30.sp
            ),
            color = ElementTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(80.dp))
        // 标题
        Text(
            text = stringResource(R.string.screen_welcome_title, " ${stringResource(AppConfigR.string.app_name)}"),
            style = ElementTheme.typography.fontHeadingLgBold.copy(
                fontSize = 28.sp
            ),
            color = ElementTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(80.dp))

        // 隐私政策提示
        Text(
            modifier = Modifier.clickable { onPrivacyPolicyClick() },
            text = stringResource(R.string.screen_welcome_privacy_notice),
            style = ElementTheme.typography.fontBodyLgRegular.copy(
                fontSize = 16.sp
            ),
            color = ElementTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = buildAnnotatedStringWithStyledPart(
                fullTextRes = R.string.screen_welcome_agreement,
                coloredTextRes = R.string.screen_welcome_privacy_policy_link,
                color = Color(0xFF0F4EAE),
                underline = false,
                bold = true,
            ),
            style = ElementTheme.typography.fontBodyLgRegular.copy(
                fontSize = 16.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable { onPrivacyPolicyClick() },
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Agree 渐变按钮
        GradientButton(
            text = stringResource(R.string.screen_welcome_agree),
            onClick = onContinueClick,
            size = ButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@PreviewsDayNight
@Composable
fun WelcomeViewPreview() {
    ElementPreview {
        WelcomeView(
            onContinueClick = {},
            onPrivacyPolicyClick = {},
        )
    }
}

