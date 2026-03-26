/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.messagecomposer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.messages.impl.R
import io.element.android.libraries.androidutils.ui.hideKeyboard
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.ListItemStyle
import io.element.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * 附件底部弹出面板
 *
 * 显示消息编辑器中的附件来源选择菜单，允许用户选择从相机、相册、文件、位置或创建投票等方
 * 式添加附件。
 *
 * @param state 消息编辑器状态
 * @param onSendLocationClick 点击位置分享的回调
 * @param onCreatePollClick 点击创建投票的回调
 * @param enableTextFormatting 是否启用文本格式化
 * @param modifier 修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AttachmentsBottomSheet(
    state: MessageComposerState,
    onSendLocationClick: () -> Unit,
    onCreatePollClick: () -> Unit,
    enableTextFormatting: Boolean,
    modifier: Modifier = Modifier,
) {
    val localView = LocalView.current
    var isVisible by rememberSaveable { mutableStateOf(state.showAttachmentSourcePicker) }

    BackHandler(enabled = isVisible) {
        isVisible = false
    }

    LaunchedEffect(state.showAttachmentSourcePicker) {
        isVisible = if (state.showAttachmentSourcePicker) {
            // We need to use this instead of `LocalFocusManager.clearFocus()` to hide the keyboard when focus is on an Android View
            localView.hideKeyboard()
            true
        } else {
            false
        }
    }
    // Send 'DismissAttachmentMenu' event when the bottomsheet was just hidden
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            state.eventSink(MessageComposerEvent.DismissAttachmentMenu)
        }
    }

    if (isVisible) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            onDismissRequest = { isVisible = false }
        ) {
            AttachmentSourcePickerMenu(
                state = state,
                enableTextFormatting = enableTextFormatting,
                onSendLocationClick = onSendLocationClick,
                onCreatePollClick = onCreatePollClick,
            )
        }
    }
}

/**
 * 附件来源选择菜单
 *
 * 显示可用的附件来源选项列表，包括相机拍照、录像、相册、文件、位置和投票等选项。
 *
 * @param state 消息编辑器状态
 * @param onSendLocationClick 点击位置分享的回调
 * @param onCreatePollClick 点击创建投票的回调
 * @param enableTextFormatting 是否启用文本格式化
 */
@Composable
private fun AttachmentSourcePickerMenu(
    state: MessageComposerState,
    onSendLocationClick: () -> Unit,
    onCreatePollClick: () -> Unit,
    enableTextFormatting: Boolean,
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
    ) {
        ListItem(
            modifier = Modifier.clickable { state.eventSink(MessageComposerEvent.PickAttachmentSource.PhotoFromCamera) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.TakePhoto())),
            headlineContent = { Text(stringResource(R.string.screen_room_attachment_source_camera_photo)) },
            style = ListItemStyle.Primary,
        )
        ListItem(
            modifier = Modifier.clickable { state.eventSink(MessageComposerEvent.PickAttachmentSource.VideoFromCamera) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.VideoCall())),
            headlineContent = { Text(stringResource(R.string.screen_room_attachment_source_camera_video)) },
            style = ListItemStyle.Primary,
        )
        ListItem(
            modifier = Modifier.clickable { state.eventSink(MessageComposerEvent.PickAttachmentSource.FromGallery) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Image())),
            headlineContent = { Text(stringResource(R.string.screen_room_attachment_source_gallery)) },
            style = ListItemStyle.Primary,
        )
        ListItem(
            modifier = Modifier.clickable { state.eventSink(MessageComposerEvent.PickAttachmentSource.FromFiles) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Attachment())),
            headlineContent = { Text(stringResource(R.string.screen_room_attachment_source_files)) },
            style = ListItemStyle.Primary,
        )
        if (state.canShareLocation) {
            ListItem(
                modifier = Modifier.clickable {
                    state.eventSink(MessageComposerEvent.PickAttachmentSource.Location)
                    onSendLocationClick()
                },
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.LocationPin())),
                headlineContent = { Text(stringResource(R.string.screen_room_attachment_source_location)) },
                style = ListItemStyle.Primary,
            )
        }
        ListItem(
            modifier = Modifier.clickable {
                state.eventSink(MessageComposerEvent.PickAttachmentSource.Poll)
                onCreatePollClick()
            },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Polls())),
            headlineContent = { Text(stringResource(R.string.screen_room_attachment_source_poll)) },
            style = ListItemStyle.Primary,
        )
        if (enableTextFormatting) {
            ListItem(
                modifier = Modifier.clickable { state.eventSink(MessageComposerEvent.ToggleTextFormatting(enabled = true)) },
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.TextFormatting())),
                headlineContent = { Text(stringResource(R.string.screen_room_attachment_text_formatting)) },
                style = ListItemStyle.Primary,
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun AttachmentSourcePickerMenuPreview() = ElementPreview {
    AttachmentSourcePickerMenu(
        state = aMessageComposerState(
            canShareLocation = true,
        ),
        onSendLocationClick = {},
        onCreatePollClick = {},
        enableTextFormatting = true,
    )
}
