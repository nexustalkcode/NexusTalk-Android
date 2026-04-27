/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.call.impl.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.call.impl.R
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Surface
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.ui.strings.CommonStrings
import timber.log.Timber

private const val incomingCallOverlayTraceTag = "IncomingCallOverlayTrace"
private val IncomingCallOverlayContainerShape = RoundedCornerShape(100.dp)
private val IncomingCallOverlayContainerColor = Color(0xFF111111)
private val IncomingCallOverlaySubtitleColor = Color(0xD9FFFFFF)
private val IncomingCallOverlayAnswerColor = Color(0xFF34C759)
private val IncomingCallOverlayDeclineColor = Color(0xFFFF453A)
private val IncomingCallOverlayScrimColor = Color(0x41000000)
private val IncomingCallOverlayAvatarSize = 36.dp
private val IncomingCallOverlayScrimGradient = Brush.verticalGradient(
    colorStops = arrayOf(
        0.0f to IncomingCallOverlayScrimColor,
        0.78f to IncomingCallOverlayScrimColor,
        1.0f to Color.Transparent,
    ),
)

/**
 * 渲染顶部来电 overlay 列表。
 *
 * @param state 当前 overlay 的展示状态。
 * @param modifier 应用于根容器的修饰符。
 */
@Composable
internal fun IncomingCallOverlayView(
    state: IncomingCallOverlayState,
    modifier: Modifier = Modifier,
) {
    val callIds = state.calls.joinToString(separator = ",") { it.id }
    LaunchedEffect(state.isVisible, callIds) {
        // 只在可见性或来电集合变化时记录，避免 Compose 重组反复刷屏；callId 是事件级排查线索，不包含消息正文。
        Timber.tag(incomingCallOverlayTraceTag).w(
            "IncomingCallOverlayView state visible=%s callCount=%s callIds=%s",
            state.isVisible,
            state.calls.size,
            callIds,
        )
    }

    if (!state.isVisible) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(IncomingCallOverlayScrimGradient)
            .statusBarsPadding()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        state.calls.forEach { call ->
            IncomingCallOverlayRow(
                call = call,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * 渲染单条来电 overlay 行。
 *
 * @param call 单条来电的展示模型与交互回调。
 * @param modifier 应用于行容器的修饰符。
 */
@Composable
private fun IncomingCallOverlayRow(
    call: IncomingCallOverlayCall,
    modifier: Modifier = Modifier,
) {
    /* 中文说明：这里刻意复刻远程通知布局的密度和层级，
       保持 36dp 头像、两行文字、右侧 32dp 圆形动作按钮，
       让用户从系统通知切回 App 时仍有一致的来电识别心智。 */
    Surface(
        modifier = modifier,
        shape = IncomingCallOverlayContainerShape,
        color = IncomingCallOverlayContainerColor,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 44.dp)
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(IncomingCallOverlayAvatarSize),
            ) {
                Avatar(
                    avatarData = call.avatarData,
                    avatarType = call.avatarType,
                    // Overlay 始终使用紧凑头像；真实来电数据可能复用全屏来电的大尺寸 AvatarData。
                    // 必须在这里覆盖尺寸，否则首字母会按大字号绘制后被 36dp 圆形裁掉。
                    forcedAvatarSize = IncomingCallOverlayAvatarSize,
                    modifier = Modifier
                        .size(IncomingCallOverlayAvatarSize)
                        .clip(CircleShape),
                )
                Image(
                    painter = painterResource(R.drawable.ic_incoming_call_badge),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(12.dp),
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = call.title,
                    style = ElementTheme.typography.fontBodyXsMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = call.subtitle,
                    modifier = Modifier.padding(top = 6.dp),
                    style = ElementTheme.typography.fontBodyXsRegular,
                    color = IncomingCallOverlaySubtitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            IncomingCallOverlayActionButton(
                onClick = {
                    Timber.tag(incomingCallOverlayTraceTag).i("IncomingCallOverlayView decline clicked callId=%s", call.id)
                    call.onDeclineClick()
                },
                backgroundColor = IncomingCallOverlayDeclineColor,
                iconResId = R.drawable.ic_incoming_call_decline,
                contentDescription = stringResource(CommonStrings.action_reject),
                modifier = Modifier.padding(start = 6.dp),
            )
            IncomingCallOverlayActionButton(
                onClick = {
                    Timber.tag(incomingCallOverlayTraceTag).i("IncomingCallOverlayView answer clicked callId=%s", call.id)
                    call.onAnswerClick()
                },
                backgroundColor = IncomingCallOverlayAnswerColor,
                iconResId = R.drawable.ic_incoming_call_answer,
                contentDescription = stringResource(CommonStrings.action_accept),
                modifier = Modifier.padding(start = 5.dp),
            )
        }
    }
}

/**
 * 渲染 overlay 中的圆形操作按钮。
 *
 * @param onClick 点击按钮时触发的动作。
 * @param backgroundColor 按钮背景色。
 * @param iconResId 按钮图标资源。
 * @param contentDescription 供无障碍使用的描述文案。
 * @param modifier 应用于按钮容器的修饰符。
 */
@Composable
private fun IncomingCallOverlayActionButton(
    onClick: () -> Unit,
    backgroundColor: Color,
    iconResId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    /* 中文说明：行级回调在这里直接透传，不额外引入全局 event sink。
       多条来电并排存在时，哪一个按钮被点到，就只派发哪一条的动作。 */
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.matchParentSize(),
        )
    }
}

@PreviewsDayNight
@Composable
internal fun IncomingCallOverlayViewPreview(
    @PreviewParameter(IncomingCallOverlayStateProvider::class) state: IncomingCallOverlayState,
) = ElementPreview {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ElementTheme.colors.bgCanvasDefault),
    ) {
        IncomingCallOverlayHost(
            state = state,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
