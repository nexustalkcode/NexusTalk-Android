/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.roomcall.api.RoomCallState
import io.element.android.features.roomcall.api.RoomCallStateProvider
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 渲染时间线顶部的通话操作入口。
 */
@Composable
internal fun CallMenuItem(
    roomCallState: RoomCallState,
    onJoinCallClick: () -> Unit,
    onStartVoiceCallClick: () -> Unit = onJoinCallClick,
    onStartVideoCallClick: () -> Unit = onJoinCallClick,
    modifier: Modifier = Modifier,
) {
    when (roomCallState) {
        RoomCallState.Unavailable -> {
            Box(modifier)
        }
        is RoomCallState.StandBy -> {
            StandByCallMenuItem(
                roomCallState = roomCallState,
                onStartVoiceCallClick = onStartVoiceCallClick,
                onStartVideoCallClick = onStartVideoCallClick,
                modifier = modifier,
            )
        }
        is RoomCallState.OnGoing -> {
            OnGoingCallMenuItem(
                roomCallState = roomCallState,
                onJoinCallClick = onJoinCallClick,
                modifier = modifier,
            )
        }
    }
}

/**
 * 渲染待机态通话入口。
 */
@Composable
private fun StandByCallMenuItem(
    roomCallState: RoomCallState.StandBy,
    onStartVoiceCallClick: () -> Unit,
    onStartVideoCallClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // StandBy 表示房间当前没有通话，此时才需要让用户在“语音”和“视频”之间做启动选择。
    // 已有通话时继续走单一 Join 入口，避免让用户误以为可以重新选择已有会议的媒体类型。
    Row(modifier = modifier) {
        IconButton(
            onClick = onStartVideoCallClick,
            enabled = roomCallState.canStartCall,
        ) {
            Icon(
                imageVector = CompoundIcons.VideoCall(),
                contentDescription = stringResource(CommonStrings.a11y_start_video_call),
            )
        }

        IconButton(
            onClick = onStartVoiceCallClick,
            enabled = roomCallState.canStartCall,
        ) {
            Icon(
                imageVector = CompoundIcons.VoiceCall(),
                contentDescription = stringResource(CommonStrings.a11y_start_voice_call),
            )
        }
    }
}

/**
 * 渲染进行中通话的 Join 按钮。
 */
@Composable
private fun OnGoingCallMenuItem(
    roomCallState: RoomCallState.OnGoing,
    onJoinCallClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!roomCallState.isUserLocallyInTheCall) {
        Button(
            onClick = onJoinCallClick,
            colors = ButtonDefaults.buttonColors(
                contentColor = ElementTheme.colors.bgCanvasDefault,
                containerColor = ElementTheme.colors.iconAccentTertiary
            ),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
            modifier = modifier.heightIn(min = 36.dp),
            enabled = roomCallState.canJoinCall,
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = CompoundIcons.VideoCall(),
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(CommonStrings.action_join),
                style = ElementTheme.typography.fontBodyMdMedium
            )
            Spacer(Modifier.width(8.dp))
        }
    } else {
        // Else user is already in the call, hide the button.
        Box(modifier)
    }
}

@PreviewsDayNight
@Composable
internal fun CallMenuItemPreview(
    @PreviewParameter(RoomCallStateProvider::class) roomCallState: RoomCallState
) = ElementPreview {
    CallMenuItem(
        roomCallState = roomCallState,
        onJoinCallClick = {},
    )
}
