/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.notifications

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.ftue.impl.R
import io.element.android.libraries.designsystem.atomic.molecules.ButtonColumnMolecule
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.atomic.pages.HeaderFooterPage
import io.element.android.libraries.designsystem.background.OnboardingBackground
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.Surface
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 通知权限选择视图组件
 *
 * 这是通知权限请求页面的 Compose UI 组件。
 * 展示了为什么要开启通知的理由，并提供"启用"和"暂时不"两个选项供用户选择。
 *
 * @param state 通知权限选择状态
 * @param onBack 返回/关闭回调
 * @param modifier 修饰符
 */
@Composable
fun NotificationsOptInView(
    state: NotificationsOptInState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    HeaderFooterPage(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize(),
        background = { OnboardingBackground() },
        header = { NotificationsOptInHeader(modifier = Modifier.padding(top = 60.dp, bottom = 28.dp)) },
        footer = { NotificationsOptInFooter(state) },
    ) {
        NotificationsOptInContent()
    }
}

/**
 * 通知权限选择页面头部组件
 *
 * 显示通知权限请求页面的标题和副标题。
 *
 * @param modifier 修饰符
 */
@Composable
private fun NotificationsOptInHeader(
    modifier: Modifier = Modifier,
) {
    IconTitleSubtitleMolecule(
        modifier = modifier,
        title = stringResource(R.string.screen_notification_optin_title),
        subTitle = stringResource(R.string.screen_notification_optin_subtitle),
        iconStyle = BigIcon.Style.Default(CompoundIcons.NotificationsSolid()),
    )
}

/**
 * 通知权限选择页面底部组件
 *
 * 显示"启用通知"和"暂时不"两个操作按钮。
 *
 * @param state 通知权限选择状态
 */
@Composable
private fun NotificationsOptInFooter(state: NotificationsOptInState) {
    ButtonColumnMolecule {
        Button(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(CommonStrings.action_ok),
            onClick = {
                state.eventSink(NotificationsOptInEvents.ContinueClicked)
            }
        )
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(CommonStrings.action_not_now),
            onClick = {
                state.eventSink(NotificationsOptInEvents.NotNowClicked)
            }
        )
    }
}

/**
 * 通知权限选择页面内容组件
 *
 * 展示模拟的通知消息界面，帮助用户理解开启通知后可以收到哪些消息。
 * 显示三个模拟的消息气泡，每个气泡包含头像和不同长度的消息预览。
 */
@Composable
private fun NotificationsOptInContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                16.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            NotificationRow(
                avatarLetter = "M",
                avatarColorsId = "5",
                firstRowPercent = 1f,
                secondRowPercent = 0.4f
            )

            NotificationRow(
                avatarLetter = "A",
                avatarColorsId = "1",
                firstRowPercent = 1f,
                secondRowPercent = 1f
            )

            NotificationRow(
                avatarLetter = "T",
                avatarColorsId = "4",
                firstRowPercent = 0.65f,
                secondRowPercent = 0f
            )
        }
    }
}

/**
 * 模拟通知消息行组件
 *
 * 展示一个模拟的通知消息，包含用户头像和消息预览内容。
 *
 * @param avatarLetter 头像上的字母
 * @param avatarColorsId 头像颜色 ID
 * @param firstRowPercent 第一行消息内容的显示百分比（用于模拟不同长度的消息）
 * @param secondRowPercent 第二行消息内容的显示百分比（用于模拟多行消息）
 */
@Composable
private fun NotificationRow(
    avatarLetter: String,
    avatarColorsId: String,
    firstRowPercent: Float,
    secondRowPercent: Float,
) {
    Surface(
        color = ElementTheme.colors.bgCanvasDisabled,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                avatarData = AvatarData(id = avatarColorsId, name = avatarLetter, size = AvatarSize.NotificationsOptIn),
                avatarType = AvatarType.User,
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxWidth(firstRowPercent)
                        .height(10.dp)
                        .background(ElementTheme.colors.borderInteractiveSecondary)
                )
                if (secondRowPercent > 0f) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .fillMaxWidth(secondRowPercent)
                            .height(10.dp)
                            .background(ElementTheme.colors.borderInteractiveSecondary)
                    )
                }
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun NotificationsOptInViewPreview(
    @PreviewParameter(NotificationsOptInStateProvider::class) state: NotificationsOptInState
) {
    ElementPreview {
        NotificationsOptInView(
            onBack = {},
            state = state,
        )
    }
}
