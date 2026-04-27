/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.startchat.impl.root

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.startchat.api.ConfirmingStartDmWithMatrixUser
import io.element.android.features.startchat.impl.R
import io.element.android.features.startchat.impl.components.UserListView
import io.element.android.libraries.designsystem.components.async.AsyncActionView
import io.element.android.libraries.designsystem.components.async.AsyncActionViewDefaults
import io.element.android.libraries.designsystem.icons.CompoundDrawables
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.ListSectionHeader
import io.element.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.homeIconBackground
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.ui.components.CreateDmConfirmationBottomSheet
import io.element.android.libraries.matrix.ui.components.MatrixUserRow
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
/**
 * 渲染开始聊天主页弹层。
 */
fun StartChatView(
    state: StartChatState,
    isVisible: Boolean,
    onCloseClick: () -> Unit,
    onNewRoomClick: () -> Unit,
    onOpenDM: (RoomId) -> Unit,
    onInviteFriendsClick: () -> Unit,
    onJoinByAddressClick: () -> Unit,
    onRoomDirectorySearchClick: () -> Unit,
    onScanQrCodeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onCloseClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onCloseClick),
                    text = stringResource(io.element.android.libraries.ui.strings.R.string.action_cancel),
                    style = ElementTheme.typography.fontBodyLgRegular.copy(fontSize = 16.sp),
                )

                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(io.element.android.libraries.ui.strings.R.string.action_start_chat),
                    style = ElementTheme.typography.fontHeadingLgBold.copy(fontSize = 17.sp),
                    textAlign = TextAlign.Center,
                )

                Box(
                    modifier = Modifier
                        .weight(1f), contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .background(color = ElementTheme.colors.textPrimary)
                            .size(35.dp)
                            .padding(8.dp)
                            .clickable(onClick = onScanQrCodeClick),
                        imageVector = CompoundIcons.CameraV1(),
                        contentDescription = stringResource(R.string.screen_scan_user_qr_code_title),
                        tint = ElementTheme.colors.textOnSolidPrimary,
                    )
                }

            }
            UserListView(
                modifier = Modifier.fillMaxWidth(),
                // Do not render suggestions in this case, the suggestion will be rendered
                // by CreateRoomActionButtonsList
                state = state.userListState.copy(
                    recentDirectRooms = persistentListOf(),
                ),
                onSelectUser = {
                    state.eventSink(StartChatEvents.StartDM(it))
                },
                onDeselectUser = { },
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (!state.userListState.isSearchActive) {
                CreateRoomActionButtonsList(
                    state = state,
                    onNewRoomClick = onNewRoomClick,
                    onInvitePeopleClick = onInviteFriendsClick,
                    onJoinByAddressClick = onJoinByAddressClick,
                    onRoomDirectorySearchClick = onRoomDirectorySearchClick,
                    onDmClick = onOpenDM,
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }

    AsyncActionView(
        async = state.startDmAction,
        progressDialog = {
            AsyncActionViewDefaults.ProgressDialog(
                progressText = stringResource(CommonStrings.common_starting_chat),
            )
        },
        onSuccess = { onOpenDM(it) },
        errorMessage = { stringResource(R.string.screen_start_chat_error_starting_chat) },
        onRetry = {
            state.userListState.selectedUsers.firstOrNull()
                ?.let { state.eventSink(StartChatEvents.StartDM(it)) }
            // Cancel start DM if there is no more selected user (should not happen)
                ?: state.eventSink(StartChatEvents.CancelStartDM)
        },
        onErrorDismiss = { state.eventSink(StartChatEvents.CancelStartDM) },
        confirmationDialog = { data ->
            if (data is ConfirmingStartDmWithMatrixUser) {
                CreateDmConfirmationBottomSheet(
                    matrixUser = data.matrixUser,
                    onSendInvite = {
                        state.eventSink(StartChatEvents.StartDM(data.matrixUser))
                    },
                    onDismiss = {
                        state.eventSink(StartChatEvents.CancelStartDM)
                    },
                )
            }
        },
    )
}

@Composable
/**
 * 渲染创建房间/邀请等快捷操作列表。
 */
private fun CreateRoomActionButtonsList(
    state: StartChatState,
    onNewRoomClick: () -> Unit,
    onInvitePeopleClick: () -> Unit,
    onJoinByAddressClick: () -> Unit,
    onRoomDirectorySearchClick: () -> Unit,
    onDmClick: (RoomId) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(5.dp))
            CreateRoomActionButton(
                iconRes = CompoundDrawables.ic_compound_plus_v2,
                text = stringResource(id = R.string.screen_create_room_action_create_room),
                onClick = onNewRoomClick,
            )
        }
        if (state.isRoomDirectorySearchEnabled) {
            item {
                CreateRoomActionButton(
                    iconRes = CompoundDrawables.ic_compound_list_bulleted,
                    text = stringResource(id = R.string.screen_room_directory_search_title),
                    onClick = onRoomDirectorySearchClick,
                )
            }
        }
        item {
            CreateRoomActionButton(
                iconRes = CompoundDrawables.ic_compound_share_android_v1,
                text = stringResource(id = CommonStrings.action_invite_friends_to_app, state.applicationName),
                onClick = onInvitePeopleClick,
            )
        }
        item {
            CreateRoomActionButton(
                iconRes = CompoundDrawables.ic_compound_room_v1,
                text = stringResource(R.string.screen_start_chat_join_room_by_address_action),
                onClick = onJoinByAddressClick,
            )
            Spacer(modifier = Modifier.height(5.dp))
        }
        if (state.userListState.recentDirectRooms.isNotEmpty()) {
            item {
                ListSectionHeader(
                    title = stringResource(id = CommonStrings.common_suggestions),
                    hasDivider = false,
                )
            }
            state.userListState.recentDirectRooms.forEach { recentDirectRoom ->
                item {
                    MatrixUserRow(
                        modifier = Modifier.clickable(
                            onClick = {
                                onDmClick(recentDirectRoom.roomId)
                            }
                        ),
                        matrixUser = recentDirectRoom.matrixUser,
                    )
                }
            }
        }
    }
}

@Composable
/**
 * 渲染单个快捷操作按钮。
 */
private fun CreateRoomActionButton(
    @DrawableRes iconRes: Int,
    text: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ElementTheme.colors.bgCanvasDefault),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(31.dp),
                tint = Color.Unspecified,
                painter = painterResource(iconRes),
                contentDescription = null,
            )
            Text(
                text = text,
                style = ElementTheme.typography.fontBodyLgRegular.copy(fontSize = 16.sp),
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun StartChatViewPreview(@PreviewParameter(StartChatStateProvider::class) state: StartChatState) =
    ElementPreview {
        StartChatView(
            state = state,
            isVisible = true,
            onCloseClick = {},
            onNewRoomClick = {},
            onOpenDM = {},
            onJoinByAddressClick = {},
            onInviteFriendsClick = {},
            onRoomDirectorySearchClick = {},
            onScanQrCodeClick = {},
        )
    }
