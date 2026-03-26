/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.impl.configureroom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.createroom.impl.R
import io.element.android.libraries.architecture.coverage.ExcludeFromCoverage
import io.element.android.libraries.designsystem.components.async.AsyncActionView
import io.element.android.libraries.designsystem.components.async.AsyncActionViewDefaults
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.modifiers.clearFocusOnTap
import io.element.android.libraries.designsystem.preview.ElementPreviewDark
import io.element.android.libraries.designsystem.preview.ElementPreviewLight
import io.element.android.libraries.designsystem.preview.PreviewWithLargeHeight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.ListSectionHeader
import io.element.android.libraries.designsystem.theme.components.RadioCheckbox
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.designsystem.theme.components.CapsuleTextField
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.TextField
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.spaces.SpaceRoom
import io.element.android.libraries.matrix.ui.components.AvatarActionBottomSheet
import io.element.android.libraries.matrix.ui.components.AvatarPickerState
import io.element.android.libraries.matrix.ui.components.AvatarPickerView
import io.element.android.libraries.matrix.ui.room.address.RoomAddressField
import io.element.android.libraries.permissions.api.PermissionsView
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import kotlin.jvm.optionals.getOrNull

/**
 * 配置房间视图
 *
 * 创建房间流程中"配置房间"步骤的 Compose 视图。
 * 展示房间配置界面，包含名称、主题、头像、可见性等设置选项。
 *
 * @param state 配置房间状态，包含所有配置信息
 * @param onBackClick 返回按钮点击回调
 * @param onCreateRoomSuccess 房间创建成功回调
 * @param modifier 视图修饰符
 */
@Composable
fun ConfigureRoomView(
    state: ConfigureRoomState,
    onBackClick: () -> Unit,
    onCreateRoomSuccess: (RoomId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSpace = state.config.isSpace
    val focusManager = LocalFocusManager.current
    val isAvatarActionsSheetVisible = remember { mutableStateOf(false) }

    fun onAvatarClick() {
        focusManager.clearFocus()
        isAvatarActionsSheetVisible.value = true
    }

    Column(
        modifier = modifier
            .clearFocusOnTap(focusManager)
            .fillMaxWidth()
    ) {
        // Top bar with Cancel, Title, Create
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = onBackClick,
            ) {
                Text(
                    text = stringResource(CommonStrings.action_cancel),
                    color = ElementTheme.colors.textPrimary,
                )
            }

            Text(
                text = stringResource(R.string.screen_create_room_new_room_title),
                style = ElementTheme.typography.fontBodyLgMedium.copy(fontSize = 18.sp),
                color = ElementTheme.colors.textPrimary,
            )

            TextButton(
                onClick = {
                    focusManager.clearFocus()
                    state.eventSink(ConfigureRoomEvents.CreateRoom)
                },
                enabled = state.isValid,
            ) {
                Text(
                    text = stringResource(CommonStrings.action_create),
                    color = if (state.isValid) ElementTheme.colors.textPrimary else ElementTheme.colors.textDisabled,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            Spacer(modifier = Modifier.height(30.dp))
            RoomNameWithAvatar(
                isSpace = isSpace,
                avatarUri = state.config.avatarUri,
                roomName = state.config.roomName.orEmpty(),
                onAvatarClick = ::onAvatarClick,
                onChangeRoomName = { state.eventSink(ConfigureRoomEvents.RoomNameChanged(it)) },
            )

            Spacer(modifier = Modifier.height(20.dp))
            RoomTopic(
                topic = state.config.topic.orEmpty(),
                onTopicChange = { state.eventSink(ConfigureRoomEvents.TopicChanged(it)) },
            )

            RoomJoinRuleOptions(
                options = state.availableJoinRules,
                selected = state.config.visibilityState.joinRuleItem,
                parentSpace = state.config.parentSpace,
                onOptionClick = {
                    focusManager.clearFocus()
                    state.eventSink(ConfigureRoomEvents.JoinRuleChanged(it))
                },
            )

            if (!state.config.isSpace && state.spaces.isNotEmpty()) {
                SelectParentSpaceOptions(
                    spaces = state.spaces,
                    selectedSpace = state.config.parentSpace,
                    onSelectSpace = { state.eventSink(ConfigureRoomEvents.SetParentSpace(it)) },
                )
            }

            if (state.config.visibilityState !is RoomVisibilityState.Private) {
                Column(modifier = Modifier.padding(horizontal = 24.dp),) {
                    Text(
                        text = stringResource(R.string.screen_create_room_room_address_section_title),
                        style = ElementTheme.typography.fontBodyLgRegular.copy(fontSize = 14.sp),
                        color = ElementTheme.colors.textSecondary,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    RoomAddressField(
                        address = state.config.visibilityState.roomAddress().getOrNull().orEmpty(),
                        homeserverName = state.homeserverName,
                        addressValidity = state.roomAddressValidity,
                        onAddressChange = { state.eventSink(ConfigureRoomEvents.RoomAddressChanged(it)) },
                        label = null,
                        supportingText = stringResource(R.string.screen_create_room_room_address_section_footer),
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
        AvatarActionBottomSheet(
            actions = state.avatarActions,
            isVisible = isAvatarActionsSheetVisible.value,
            onDismiss = { isAvatarActionsSheetVisible.value = false },
            onSelectAction = { state.eventSink(ConfigureRoomEvents.HandleAvatarAction(it)) }
        )

        AsyncActionView(
            async = state.createRoomAction,
            progressDialog = {
                AsyncActionViewDefaults.ProgressDialog(
                    progressText = stringResource(if (isSpace) CommonStrings.common_creating_space else CommonStrings.common_creating_room),
                )
            },
            onSuccess = { onCreateRoomSuccess(it) },
            errorMessage = { stringResource(if (isSpace) R.string.screen_create_room_error_creating_space else R.string.screen_create_room_error_creating_room) },
            onRetry = { state.eventSink(ConfigureRoomEvents.CreateRoom) },
            onErrorDismiss = { state.eventSink(ConfigureRoomEvents.CancelCreateRoom) },
        )

        PermissionsView(
            state = state.cameraPermissionState,
        )
    }
}

/**
 * 房间名称与头像组件
 *
 * 显示房间名称输入框和头像选择器。
 * 头像可以点击进行更换或移除。
 *
 * @param isSpace 是否为空间
 * @param avatarUri 头像 URI
 * @param roomName 房间名称
 * @param onAvatarClick 头像点击回调
 * @param onChangeRoomName 房间名称变更回调
 * @param modifier 组件修饰符
 */
@Composable
private fun RoomNameWithAvatar(
    isSpace: Boolean,
    avatarUri: String?,
    roomName: String,
    onAvatarClick: () -> Unit,
    onChangeRoomName: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                contentAlignment = Alignment.Center,
            ) {
                val avatarState = remember(avatarUri) {
                    if (avatarUri != null) {
                        AvatarPickerState.Selected(
                            avatarData = AvatarData(id = "#", name = null, url = avatarUri, size = AvatarSize.EditRoomDetails),
                            type = if (isSpace) AvatarType.Space() else AvatarType.Room(),
                        )
                    } else {
                        val containerSize = 68.dp
                        val padding = PaddingValues((AvatarSize.EditRoomDetails.dp - containerSize) / 2)
                        AvatarPickerState.Pick(buttonSize = 68.dp, iconSize = 24.dp, externalPadding = padding)
                    }
                }
                AvatarPickerView(
                    state = avatarState,
                    onClick = onAvatarClick,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.screen_create_room_avatar_photo),
                style = ElementTheme.typography.fontBodySmRegular,
                color = Color(0xFF007AFF),
            )
        }

        CapsuleTextField(
            value = roomName,
            onValueChange = onChangeRoomName,
            label = stringResource(R.string.screen_create_room_name_label),
            labelStyle = ElementTheme.typography.fontBodyLgRegular.copy(fontSize = 14.sp),
            placeholder = stringResource(R.string.screen_create_room_name_placeholder),
            elevation = 3.dp,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * 房间主题组件
 *
 * 显示房间主题/描述的输入区域。
 *
 * @param topic 当前主题内容
 * @param onTopicChange 主题变更回调
 * @param modifier 组件修饰符
 */
@Composable
private fun RoomTopic(
    topic: String,
    onTopicChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = stringResource(R.string.screen_create_room_topic_label),
            style = ElementTheme.typography.fontBodyLgRegular.copy(fontSize = 14.sp),
            color = ElementTheme.colors.textSecondary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        HorizontalDivider()
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp),
            color = ElementTheme.colors.bgSubtleSecondary,
        ) {
            BasicTextField(
                value = topic,
                onValueChange = onTopicChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textStyle = ElementTheme.typography.fontBodyLgRegular.copy(
                    color = ElementTheme.colors.textPrimary
                ),
                maxLines = 3,
                cursorBrush = SolidColor(ElementTheme.colors.textPrimary),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
            ) { innerTextField ->
                Box {
                    if (topic.isEmpty()) {
                        Text(
                            text = stringResource(R.string.screen_create_room_topic_placeholder),
                            style = ElementTheme.typography.fontBodyLgRegular,
                            color = ElementTheme.colors.textSecondary,
                        )
                    }
                    innerTextField()
                }
            }
        }
    }
}

/**
 * 配置房间选项容器
 *
 * 用于包裹配置选项的通用容器组件，提供可组合的内容区域。
 *
 * @param title 选项标题
 * @param modifier 组件修饰符
 * @param content 选项内容 Composable
 */
@Composable
internal fun ConfigureRoomOptions(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.selectableGroup()
    ) {
        content()
    }
}

/**
 * 房间加入规则选项组件
 *
 * 显示房间加入规则的可选项列表，包括公开、私有、受限等选项。
 * 用户可以选择其中一种加入规则。
 *
 * @param options 可用的加入规则选项列表
 * @param selected 当前选中的加入规则
 * @param onOptionClick 选项点击回调
 * @param parentSpace 父空间（如果有），用于受限规则的描述
 * @param modifier 组件修饰符
 */
@Composable
private fun RoomJoinRuleOptions(
    options: ImmutableList<JoinRuleItem>,
    selected: JoinRuleItem,
    onOptionClick: (JoinRuleItem) -> Unit,
    parentSpace: SpaceRoom?,
    modifier: Modifier = Modifier,
) {
    ConfigureRoomOptions(
        title = stringResource(R.string.screen_create_room_room_access_section_title),
        modifier = modifier.padding(horizontal = 9.dp),
    ) {
        options.forEach { item ->
            val isSelected = item == selected
            ListItem(
                headlineContent = {
                    val title = when (item) {
                        JoinRuleItem.PublicVisibility.Public -> stringResource(R.string.screen_create_room_room_access_section_public_option_title)
                        is JoinRuleItem.PublicVisibility.Restricted -> stringResource(R.string.screen_create_room_room_access_section_restricted_option_title)
                        JoinRuleItem.PublicVisibility.AskToJoin -> stringResource(R.string.screen_create_room_room_access_section_knocking_option_title)
                        is JoinRuleItem.PublicVisibility.AskToJoinRestricted -> stringResource(
                            R.string.screen_create_room_room_access_section_knocking_restricted_option_title
                        )
                        JoinRuleItem.Private -> stringResource(R.string.screen_create_room_room_access_section_private_option_title)
                    }
                    Text(text = title)
                },
                supportingContent = {
                    val description = when (item) {
                        JoinRuleItem.PublicVisibility.Public -> stringResource(R.string.screen_create_room_room_access_section_public_option_description)
                        is JoinRuleItem.PublicVisibility.Restricted -> stringResource(
                            R.string.screen_create_room_room_access_section_restricted_option_description,
                            parentSpace?.displayName.orEmpty()
                        )
                        JoinRuleItem.PublicVisibility.AskToJoin -> stringResource(R.string.screen_create_room_room_access_section_knocking_option_description)
                        is JoinRuleItem.PublicVisibility.AskToJoinRestricted -> stringResource(
                            R.string.screen_create_room_room_access_section_knocking_restricted_option_description,
                            parentSpace?.displayName.orEmpty()
                        )
                        JoinRuleItem.Private -> stringResource(R.string.screen_create_room_room_access_section_private_option_description)
                    }
                    Text(text = description)
                },
                trailingContent = ListItemContent.Custom { enabled ->
                    RadioCheckbox(
                        selected = isSelected,
                        enabled = enabled,
                        onClick = { onOptionClick(item) },
                    )
                },
                onClick = { onOptionClick(item) },
            )
        }
    }
}

/**
 * 配置房间视图 - 浅色主题预览
 *
 * @param state 配置房间状态，提供预览数据
 */
@PreviewWithLargeHeight
@Composable
internal fun ConfigureRoomViewLightPreview(@PreviewParameter(ConfigureRoomStateProvider::class) state: ConfigureRoomState) =
    ElementPreviewLight { ContentToPreview(state) }

/**
 * 配置房间视图 - 深色主题预览
 *
 * @param state 配置房间状态，提供预览数据
 */
@PreviewWithLargeHeight
@Composable
internal fun ConfigureRoomViewDarkPreview(@PreviewParameter(ConfigureRoomStateProvider::class) state: ConfigureRoomState) =
    ElementPreviewDark { ContentToPreview(state) }

/**
 * 预览内容组件
 *
 * 用于预览的配置房间视图内容
 *
 * @param state 配置房间状态
 */
@ExcludeFromCoverage
@Composable
private fun ContentToPreview(state: ConfigureRoomState) {
    ConfigureRoomView(
        state = state,
        onBackClick = {},
        onCreateRoomSuccess = {},
    )
}
