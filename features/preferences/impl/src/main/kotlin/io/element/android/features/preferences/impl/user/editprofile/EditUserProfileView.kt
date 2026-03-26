/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.user.editprofile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.preferences.impl.R
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.async.AsyncActionView
import io.element.android.libraries.designsystem.components.async.AsyncActionViewDefaults
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.components.dialogs.SaveChangesDialog
import io.element.android.libraries.designsystem.modifiers.clearFocusOnTap
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.CenteredTitleTopBar
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.TextField
import io.element.android.libraries.matrix.ui.components.AvatarActionBottomSheet
import io.element.android.libraries.matrix.ui.components.AvatarPickerState
import io.element.android.libraries.matrix.ui.components.AvatarPickerView
import io.element.android.libraries.permissions.api.PermissionsView
import io.element.android.libraries.ui.strings.CommonStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserProfileView(
    state: EditUserProfileState,
    onEditProfileSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val isAvatarActionsSheetVisible = remember { mutableStateOf(false) }
    val bgColor = ElementTheme.colors.bgCanvasDefault

    fun onAvatarClick() {
        focusManager.clearFocus()
        isAvatarActionsSheetVisible.value = true
    }

    fun onBackClick() {
        focusManager.clearFocus()
        state.eventSink(EditUserProfileEvent.Exit)
    }

    BackHandler(
        enabled = true,
        ::onBackClick,
    )
    Scaffold(
        modifier = modifier.clearFocusOnTap(focusManager),
        topBar = {
            CenteredTitleTopBar(
                title = stringResource(R.string.screen_edit_profile_title_new),
                onBackClick = ::onBackClick,
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 第一层背景 - 偏灰色
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ElementTheme.colors.bgSubtleSecondary)
            )
            // 第二层背景 - 圆弧向上凸
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val arcHeight = 180.dp.toPx()
                        val path = Path().apply {
                            moveTo(0f, arcHeight)
                            quadraticBezierTo(
                                size.width / 2, 60.dp.toPx(),
                                size.width, arcHeight
                            )
                            lineTo(size.width, size.height)
                            lineTo(0f, size.height)
                            close()
                        }
                        drawPath(
                            path = path,
                            color = bgColor
                        )
                    }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {

            Spacer(modifier = Modifier.height(24.dp))
            val avatarPickerState = remember(state.userAvatarUrl) {
                val size = AvatarSize.EditProfileDetails
                val type = AvatarType.User
                AvatarPickerState.Selected(
                    avatarData = AvatarData(id = state.userId.value, name = state.displayName, size = size, url = state.userAvatarUrl),
                    type = type
                )
            }
            AvatarPickerView(
                state = avatarPickerState,
                onClick = ::onAvatarClick,
                enabled = false,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                text = stringResource(R.string.screen_edit_profile_change_avatar),
                onClick = ::onAvatarClick,
                modifier = Modifier.fillMaxWidth(),
                size = ButtonSize.Large,
            )
            Spacer(modifier = Modifier.height(24.dp))
            TextField(
                label = stringResource(R.string.screen_edit_profile_display_name_new),
                value = state.displayName,
                placeholder = stringResource(CommonStrings.common_room_name_placeholder),
                singleLine = true,
                onValueChange = { state.eventSink(EditUserProfileEvent.UpdateDisplayName(it)) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                label = stringResource(R.string.screen_edit_profile_username),
                value = state.userId.value,
                readOnly = true,
                singleLine = true,
                onValueChange = {},
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                text = stringResource(CommonStrings.action_save),
                onClick = {
                    focusManager.clearFocus()
                    state.eventSink(EditUserProfileEvent.Save)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.saveButtonEnabled,
                size = ButtonSize.Large,
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        }

        AvatarActionBottomSheet(
            actions = state.avatarActions,
            isVisible = isAvatarActionsSheetVisible.value,
            onDismiss = { isAvatarActionsSheetVisible.value = false },
            onSelectAction = { state.eventSink(EditUserProfileEvent.HandleAvatarAction(it)) }
        )

        AsyncActionView(
            async = state.saveAction,
            progressDialog = {
                AsyncActionViewDefaults.ProgressDialog(
                    progressText = stringResource(R.string.screen_edit_profile_updating_details),
                )
            },
            confirmationDialog = { confirming ->
                when (confirming) {
                    is AsyncAction.ConfirmingCancellation -> {
                        SaveChangesDialog(
                            onSaveClick = { state.eventSink(EditUserProfileEvent.Save) },
                            onDiscardClick = { state.eventSink(EditUserProfileEvent.Exit) },
                            onDismiss = { state.eventSink(EditUserProfileEvent.CloseDialog) },
                        )
                    }
                }
            },
            onSuccess = { onEditProfileSuccess() },
            errorTitle = { stringResource(R.string.screen_edit_profile_error_title) },
            errorMessage = { stringResource(R.string.screen_edit_profile_error) },
            onErrorDismiss = { state.eventSink(EditUserProfileEvent.CloseDialog) },
        )
    }
    PermissionsView(
        state = state.cameraPermissionState,
    )
}

@PreviewsDayNight
@Composable
internal fun EditUserProfileViewPreview(@PreviewParameter(EditUserProfileStateProvider::class) state: EditUserProfileState) =
    ElementPreview {
        EditUserProfileView(
            onEditProfileSuccess = {},
            state = state,
        )
    }
