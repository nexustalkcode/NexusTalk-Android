/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl.root

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.appconfig.LearnMoreConfig
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.securityandprivacy.impl.R
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.coverage.ExcludeFromCoverage
import io.element.android.libraries.designsystem.components.async.AsyncActionView
import io.element.android.libraries.designsystem.components.async.AsyncActionViewDefaults
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import io.element.android.libraries.designsystem.components.dialogs.SaveChangesDialog
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.preview.ElementPreviewDark
import io.element.android.libraries.designsystem.preview.ElementPreviewLight
import io.element.android.libraries.designsystem.preview.PreviewWithLargeHeight
import io.element.android.libraries.designsystem.text.stringWithLink
import io.element.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList

/**
 * 安全与隐私视图
 *
 * 负责渲染安全与隐私设置的完整界面，包含：
 * - 房间访问权限设置区块
 * - 房间可见性设置区块（包括房间地址和目录可见性）
 * - 加密设置区块
 * - 历史可见性设置区块
 * - 保存/取消操作的对话框
 *
 * @param state 页面状态
 * @param onLinkClick 链接点击事件处理（用于"了解更多"链接）
 * @param modifier 样式修饰符
 * @see SecurityAndPrivacyState 页面状态数据类
 */
@Composable
fun SecurityAndPrivacyView(
    state: SecurityAndPrivacyState,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler {
        state.eventSink(SecurityAndPrivacyEvent.Exit)
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            SecurityAndPrivacyToolbar(
                isSaveActionEnabled = state.canBeSaved,
                onBackClick = {
                    state.eventSink(SecurityAndPrivacyEvent.Exit)
                },
                onSaveClick = {
                    state.eventSink(SecurityAndPrivacyEvent.Save)
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .consumeWindowInsets(padding),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            if (state.showRoomAccessSection) {
                RoomAccessSection(
                    state = state,
                    modifier = Modifier.padding(top = 24.dp),
                )
            }
            if (state.showRoomVisibilitySections) {
                RoomVisibilitySection(state.homeserverName)
                RoomAddressSection(
                    roomAddress = state.editedSettings.address,
                    homeserverName = state.homeserverName,
                    onRoomAddressClick = { state.eventSink(SecurityAndPrivacyEvent.EditRoomAddress) },
                    isVisibleInRoomDirectory = state.editedSettings.isVisibleInRoomDirectory,
                    onVisibilityChange = {
                        state.eventSink(SecurityAndPrivacyEvent.ToggleRoomVisibility)
                    },
                )
            }
            if (state.showEncryptionSection) {
                EncryptionSection(
                    isRoomEncrypted = state.editedSettings.isEncrypted,
                    // encryption can't be disabled once enabled
                    canToggleEncryption = !state.savedSettings.isEncrypted,
                    onToggleEncryption = { state.eventSink(SecurityAndPrivacyEvent.ToggleEncryptionState) },
                    showConfirmation = state.showEnableEncryptionConfirmation,
                    onDismissConfirmation = { state.eventSink(SecurityAndPrivacyEvent.CancelEnableEncryption) },
                    onConfirmEncryption = { state.eventSink(SecurityAndPrivacyEvent.ConfirmEnableEncryption) },
                )
            }
            if (state.showHistoryVisibilitySection) {
                HistoryVisibilitySection(
                    editedOption = state.editedSettings.historyVisibility,
                    savedOptions = state.savedSettings.historyVisibility,
                    availableOptions = state.availableHistoryVisibilities,
                    onSelectOption = { state.eventSink(SecurityAndPrivacyEvent.ChangeHistoryVisibility(it)) },
                    onLinkClick = onLinkClick,
                )
            }
        }
    }
    AsyncActionView(
        async = state.saveAction,
        onSuccess = { },
        onErrorDismiss = { state.eventSink(SecurityAndPrivacyEvent.DismissSaveError) },
        confirmationDialog = { confirming ->
            when (confirming) {
                is AsyncAction.ConfirmingCancellation ->
                    SaveChangesDialog(
                        onSaveClick = { state.eventSink(SecurityAndPrivacyEvent.Save) },
                        onDiscardClick = { state.eventSink(SecurityAndPrivacyEvent.Exit) },
                        onDismiss = { state.eventSink(SecurityAndPrivacyEvent.DismissExitConfirmation) }
                    )
            }
        },
        errorMessage = { stringResource(CommonStrings.error_unknown) },
        progressDialog = {
            AsyncActionViewDefaults.ProgressDialog(
                progressText = stringResource(CommonStrings.common_saving),
            )
        },
        onRetry = { state.eventSink(SecurityAndPrivacyEvent.Save) },
    )
}

/**
 * 安全与隐私页面顶部导航栏
 *
 * 包含页面标题、返回按钮和保存按钮。
 *
 * @param isSaveActionEnabled 保存按钮是否可用
 * @param onBackClick 返回按钮点击事件处理
 * @param onSaveClick 保存按钮点击事件处理
 * @param modifier 样式修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecurityAndPrivacyToolbar(
    isSaveActionEnabled: Boolean,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        titleStr = stringResource(R.string.screen_security_and_privacy_title),
        navigationIcon = { BackButton(onClick = onBackClick) },
        actions = {
            TextButton(
                text = stringResource(CommonStrings.action_save),
                enabled = isSaveActionEnabled,
                onClick = onSaveClick,
            )
        }
    )
}

/**
 * 安全与隐私区块通用组件
 *
 * 用于包装页面中的各个设置区块，提供统一的标题和样式。
 *
 * @param title 区块标题
 * @param modifier 样式修饰符
 * @param subtitle 区块副标题（可选）
 * @param content 区块内容
 */
@Composable
private fun SecurityAndPrivacySection(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: AnnotatedString? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.selectableGroup()
    ) {
        Text(
            text = title,
            style = ElementTheme.typography.fontBodyLgMedium,
            color = ElementTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        if (subtitle != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        content()
    }
}

/**
 * 房间访问权限设置区块
 *
 * 允许用户配置房间的访问权限，包括：
 * - 任何人可加入
 * - 仅空间成员可加入
 * - 需要申请加入
 * - 仅限邀请
 *
 * @param state 页面状态
 * @param modifier 样式修饰符
 */
@Composable
private fun RoomAccessSection(
    state: SecurityAndPrivacyState,
    modifier: Modifier = Modifier,
) {
    val edited = state.editedSettings.roomAccess

    fun onSelectOption(option: SecurityAndPrivacyRoomAccess) {
        state.eventSink(SecurityAndPrivacyEvent.ChangeRoomAccess(option))
    }

    fun onSpaceMemberAccessClick() {
        state.eventSink(SecurityAndPrivacyEvent.SelectSpaceMemberAccess)
    }

    fun onAskToJoinWithSpaceMembersClick() {
        state.eventSink(SecurityAndPrivacyEvent.SelectAskToJoinWithSpaceMembersAccess)
    }

    fun onManageSpacesClick() {
        state.eventSink(SecurityAndPrivacyEvent.ManageAuthorizedSpaces)
    }

    SecurityAndPrivacySection(
        title = stringResource(R.string.screen_security_and_privacy_room_access_section_header),
        modifier = modifier,
    ) {
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.screen_security_and_privacy_room_access_anyone_option_title)) },
            supportingContent = { Text(text = stringResource(R.string.screen_security_and_privacy_room_access_anyone_option_description)) },
            trailingContent = ListItemContent.RadioCheckbox(selected = edited == SecurityAndPrivacyRoomAccess.Anyone),
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Public())),
            onClick = { onSelectOption(SecurityAndPrivacyRoomAccess.Anyone) },
        )
        if (state.showSpaceMemberOption) {
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.screen_security_and_privacy_room_access_space_members_option_title)) },
                supportingContent = {
                    Text(text = state.spaceMemberDescription())
                },
                trailingContent = ListItemContent.RadioCheckbox(selected = state.editedSettings.roomAccess is SecurityAndPrivacyRoomAccess.SpaceMember),
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Space())),
                onClick = ::onSpaceMemberAccessClick,
                enabled = state.isSpaceMemberSelectable,
            )
        }
        if (state.showAskToJoinOption) {
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.screen_security_and_privacy_ask_to_join_option_title)) },
                supportingContent = { Text(text = stringResource(R.string.screen_security_and_privacy_ask_to_join_option_description)) },
                trailingContent = ListItemContent.RadioCheckbox(selected = edited == SecurityAndPrivacyRoomAccess.AskToJoin),
                onClick = { onSelectOption(SecurityAndPrivacyRoomAccess.AskToJoin) },
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.UserAdd())),
                enabled = state.isAskToJoinSelectable,
            )
        }
        if (state.showAskToJoinWithSpaceMemberOption) {
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.screen_security_and_privacy_ask_to_join_option_title)) },
                supportingContent = { Text(text = state.askToJoinWithSpaceMembersDescription()) },
                trailingContent = ListItemContent.RadioCheckbox(selected = edited is SecurityAndPrivacyRoomAccess.AskToJoinWithSpaceMember),
                onClick = ::onAskToJoinWithSpaceMembersClick,
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.UserAdd())),
                enabled = state.isAskToJoinWithSpaceMembersSelectable,
            )
        }
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.screen_security_and_privacy_room_access_invite_only_option_title)) },
            supportingContent = { Text(text = stringResource(R.string.screen_security_and_privacy_room_access_invite_only_option_description)) },
            trailingContent = ListItemContent.RadioCheckbox(selected = edited == SecurityAndPrivacyRoomAccess.InviteOnly),
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Lock())),
            onClick = { onSelectOption(SecurityAndPrivacyRoomAccess.InviteOnly) },
        )
        if (state.showManageSpaceFooter) {
            val footerText = stringWithLink(
                textRes = R.string.screen_security_and_privacy_room_access_footer,
                url = "",
                linkTextRes = R.string.screen_security_and_privacy_room_access_footer_manage_spaces_action,
                onLinkClick = { onManageSpacesClick() },
            )
            Text(
                text = footerText,
                style = ElementTheme.typography.fontBodySmRegular,
                color = ElementTheme.colors.textSecondary,
                modifier = Modifier.padding(bottom = 12.dp, start = 56.dp, end = 24.dp)
            )
        }
    }
}

/**
 * 房间可见性设置区块
 *
 * 显示房间可见性设置的说明信息。
 *
 * @param homeserverName 服务器名称，用于显示在说明文本中
 * @param modifier 样式修饰符
 */
@Composable
private fun RoomVisibilitySection(
    homeserverName: String,
    modifier: Modifier = Modifier,
) {
    SecurityAndPrivacySection(
        title = stringResource(R.string.screen_security_and_privacy_room_visibility_section_header),
        modifier = modifier,
    ) {
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.screen_security_and_privacy_room_visibility_section_footer, homeserverName),
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

/**
 * 房间地址和目录可见性设置区块
 *
 * 允许用户：
 * - 设置或编辑房间地址
 * - 切换房间在房间目录中的可见性
 *
 * @param roomAddress 当前房间地址（null 表示未设置）
 * @param homeserverName 服务器名称
 * @param isVisibleInRoomDirectory 目录可见性的异步状态
 * @param onRoomAddressClick 房间地址点击事件处理
 * @param onVisibilityChange 可见性切换事件处理
 * @param modifier 样式修饰符
 */
@Composable
private fun RoomAddressSection(
    roomAddress: String?,
    homeserverName: String,
    isVisibleInRoomDirectory: AsyncData<Boolean>,
    onRoomAddressClick: () -> Unit,
    onVisibilityChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SecurityAndPrivacySection(
        title = stringResource(R.string.screen_security_and_privacy_room_address_section_header),
        modifier = modifier,
    ) {
        ListItem(
            headlineContent = {
                Text(text = roomAddress ?: stringResource(R.string.screen_security_and_privacy_add_room_address_action))
            },
            trailingContent = if (roomAddress.isNullOrEmpty()) ListItemContent.Icon(IconSource.Vector(CompoundIcons.Plus())) else null,
            supportingContent = { Text(text = stringResource(R.string.screen_security_and_privacy_room_address_section_footer)) },
            onClick = onRoomAddressClick,
            colors = ListItemDefaults.colors(trailingIconColor = ElementTheme.colors.iconAccentPrimary),
        )

        ListItem(
            headlineContent = { Text(text = stringResource(R.string.screen_security_and_privacy_room_directory_visibility_toggle_title)) },
            supportingContent = {
                Text(text = stringResource(R.string.screen_security_and_privacy_room_directory_visibility_toggle_description, homeserverName))
            },
            onClick = if (isVisibleInRoomDirectory.isSuccess()) onVisibilityChange else null,
            trailingContent = when (isVisibleInRoomDirectory) {
                is AsyncData.Uninitialized, is AsyncData.Loading -> {
                    ListItemContent.Custom {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .progressSemantics()
                                .size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
                is AsyncData.Failure -> {
                    ListItemContent.Switch(
                        checked = false,
                        enabled = false,
                    )
                }
                is AsyncData.Success -> {
                    ListItemContent.Switch(
                        checked = isVisibleInRoomDirectory.data,
                    )
                }
            }
        )
    }
}

/**
 * 加密设置区块
 *
 * 允许用户启用或查看房间加密状态。
 * 注意：一旦启用加密，将无法禁用。
 * 启用加密前会显示确认对话框。
 *
 * @param isRoomEncrypted 当前是否已加密
 * @param canToggleEncryption 是否可以切换加密状态（加密后无法切换）
 * @param showConfirmation 是否显示确认对话框
 * @param onToggleEncryption 切换加密状态事件处理
 * @param onConfirmEncryption 确认启用加密事件处理
 * @param onDismissConfirmation 关闭确认对话框事件处理
 * @param modifier 样式修饰符
 */
@Composable
private fun EncryptionSection(
    isRoomEncrypted: Boolean,
    canToggleEncryption: Boolean,
    showConfirmation: Boolean,
    onToggleEncryption: () -> Unit,
    onConfirmEncryption: () -> Unit,
    onDismissConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SecurityAndPrivacySection(
        title = stringResource(R.string.screen_security_and_privacy_encryption_section_header),
        modifier = modifier,
    ) {
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.screen_security_and_privacy_encryption_toggle_title)) },
            supportingContent = { Text(text = stringResource(R.string.screen_security_and_privacy_encryption_section_footer)) },
            trailingContent = ListItemContent.Switch(
                checked = isRoomEncrypted,
                enabled = canToggleEncryption,
            ),
            onClick = if (canToggleEncryption) onToggleEncryption else null
        )
    }
    if (showConfirmation) {
        ConfirmationDialog(
            title = stringResource(R.string.screen_security_and_privacy_enable_encryption_alert_title),
            content = stringResource(R.string.screen_security_and_privacy_enable_encryption_alert_description),
            submitText = stringResource(R.string.screen_security_and_privacy_enable_encryption_alert_confirm_button_title),
            onSubmitClick = onConfirmEncryption,
            onDismiss = onDismissConfirmation,
        )
    }
}

/**
 * 历史可见性设置区块
 *
 * 允许用户配置房间消息的历史可见性。
 * 可选值包括：自邀请起可见、选择后可见、所有人可见。
 *
 * @param editedOption 当前编辑的历史可见性选项
 * @param savedOptions 已保存的历史可见性选项
 * @param availableOptions 可用的历史可见性选项列表
 * @param onSelectOption 选择选项事件处理
 * @param onLinkClick 链接点击事件处理（"了解更多"链接）
 * @param modifier 样式修饰符
 */
@Composable
private fun HistoryVisibilitySection(
    editedOption: SecurityAndPrivacyHistoryVisibility?,
    savedOptions: SecurityAndPrivacyHistoryVisibility?,
    availableOptions: ImmutableList<SecurityAndPrivacyHistoryVisibility>,
    onSelectOption: (SecurityAndPrivacyHistoryVisibility) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SecurityAndPrivacySection(
        title = stringResource(R.string.screen_security_and_privacy_room_history_section_header),
        subtitle = stringWithLink(
            textRes = R.string.screen_security_and_privacy_room_history_section_footer,
            url = LearnMoreConfig.HISTORY_VISIBLE_URL,
            onLinkClick = onLinkClick,
        ),
        modifier = modifier,
    ) {
        for (availableOption in availableOptions) {
            val isSelected = availableOption == editedOption
            HistoryVisibilityItem(
                option = availableOption,
                isSelected = isSelected,
                onSelectOption = onSelectOption,
            )
        }
        // Also show the saved option if it's not in the available options, but disabled
        if (savedOptions != null && !availableOptions.contains(savedOptions)) {
            HistoryVisibilityItem(
                option = savedOptions,
                isSelected = true,
                isEnabled = false,
                onSelectOption = {},
            )
        }
    }
}

/**
 * 历史可见性选项组件
 *
 * 用于显示单个历史可见性选项，包含单选按钮。
 *
 * @param option 历史可见性选项
 * @param isSelected 是否被选中
 * @param onSelectOption 选择选项事件处理
 * @param modifier 样式修饰符
 * @param isEnabled 是否启用
 */
@Composable
private fun HistoryVisibilityItem(
    option: SecurityAndPrivacyHistoryVisibility,
    isSelected: Boolean,
    onSelectOption: (SecurityAndPrivacyHistoryVisibility) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    val headlineText = when (option) {
        SecurityAndPrivacyHistoryVisibility.Invited -> stringResource(R.string.screen_security_and_privacy_room_history_since_invite_option_title)
        SecurityAndPrivacyHistoryVisibility.Shared -> stringResource(R.string.screen_security_and_privacy_room_history_since_selecting_option_title)
        SecurityAndPrivacyHistoryVisibility.WorldReadable -> stringResource(R.string.screen_security_and_privacy_room_history_anyone_option_title)
    }
    ListItem(
        headlineContent = { Text(text = headlineText) },
        trailingContent = ListItemContent.RadioCheckbox(selected = isSelected, enabled = isEnabled),
        onClick = { onSelectOption(option) },
        enabled = isEnabled,
        modifier = modifier,
    )
}

@PreviewWithLargeHeight
@Composable
internal fun SecurityAndPrivacyViewLightPreview(@PreviewParameter(SecurityAndPrivacyStateProvider::class) state: SecurityAndPrivacyState) =
    ElementPreviewLight { ContentToPreview(state) }

@PreviewWithLargeHeight
@Composable
internal fun SecurityAndPrivacyViewDarkPreview(@PreviewParameter(SecurityAndPrivacyStateProvider::class) state: SecurityAndPrivacyState) =
    ElementPreviewDark { ContentToPreview(state) }

@ExcludeFromCoverage
@Composable
private fun ContentToPreview(state: SecurityAndPrivacyState) {
    SecurityAndPrivacyView(
        state = state,
        onLinkClick = {},
    )
}
