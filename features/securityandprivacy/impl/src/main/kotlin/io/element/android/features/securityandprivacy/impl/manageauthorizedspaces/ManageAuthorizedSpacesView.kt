/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl.manageauthorizedspaces

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.securityandprivacy.impl.R
import io.element.android.libraries.designsystem.atomic.molecules.IconTitleSubtitleMolecule
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.ListSectionHeader
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 管理授权空间视图
 *
 * 负责渲染管理授权空间的界面，包含：
 * - 页面标题和说明
 * - 可选空间列表（带复选框）
 * - 未知空间列表（无法获取详细信息的空间）
 * - 完成按钮
 *
 * Figma 设计参考: https://www.figma.com/design/kcnHxunG1LDWXsJhaNuiHz/ER-145--Spaces-on-Element-X?node-id=6361-86274&m=dev
 *
 * @param state 页面状态
 * @param modifier 样式修饰符
 * @see ManageAuthorizedSpacesState 页面状态数据类
 */
// Figma design: https://www.figma.com/design/kcnHxunG1LDWXsJhaNuiHz/ER-145--Spaces-on-Element-X?node-id=6361-86274&m=dev
@Composable
fun ManageAuthorizedSpacesView(
    state: ManageAuthorizedSpacesState,
    modifier: Modifier = Modifier,
) {
    fun onCancel() {
        state.eventSink(ManageAuthorizedSpacesEvent.Cancel)
    }

    fun onDone() {
        state.eventSink(ManageAuthorizedSpacesEvent.Done)
    }

    BackHandler(onBack = ::onCancel)

    Scaffold(
        modifier = modifier,
        topBar = {
            ManageAuthorizedSpacesTopBar(
                onBackClick = ::onCancel,
                onDoneClick = ::onDone,
                isDoneButtonEnabled = state.isDoneButtonEnabled
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            headerItem()
            item {
                ListSectionHeader(
                    title = stringResource(R.string.screen_manage_authorized_spaces_your_spaces_section_title),
                    hasDivider = false,
                )
            }
            items(items = state.selectableSpaces.toList()) { space ->
                CheckableSpaceListItem(
                    headlineText = space.displayName,
                    supportingText = space.canonicalAlias?.value,
                    avatarData = space.getAvatarData(AvatarSize.SpaceMember),
                    checked = state.selectedIds.contains(space.roomId),
                    onCheckedChange = { _ ->
                        state.eventSink(
                            ManageAuthorizedSpacesEvent.ToggleSpace(space.roomId)
                        )
                    }
                )
            }
            if (state.unknownSpaceIds.isNotEmpty()) {
                item {
                    ListSectionHeader(
                        title = stringResource(R.string.screen_manage_authorized_spaces_unknown_spaces_section_title),
                        hasDivider = true,
                    )
                }
                items(items = state.unknownSpaceIds) {
                    CheckableSpaceListItem(
                        headlineText = stringResource(R.string.screen_manage_authorized_spaces_unknown_space),
                        supportingText = it.value,
                        avatarData = null,
                        checked = state.selectedIds.contains(it),
                        onCheckedChange = { _ ->
                            state.eventSink(
                                ManageAuthorizedSpacesEvent.ToggleSpace(it)
                            )
                        }
                    )
                }
            }
        }
    }
}

private fun LazyListScope.headerItem() {
    item(key = "header", contentType = "header") {
        IconTitleSubtitleMolecule(
            modifier = Modifier.padding(
                vertical = 16.dp,
                horizontal = 24.dp
            ),
            title = stringResource(R.string.screen_manage_authorized_spaces_header),
            subTitle = null,
            iconStyle = BigIcon.Style.Default(
                vectorIcon = CompoundIcons.SpaceSolid(),
            )
        )
    }
}

/**
 * 可勾选的空间列表项组件
 *
 * 用于显示空间信息，并提供复选框供用户选择。
 *
 * @param headlineText 主标题文本（空间名称）
 * @param supportingText 辅助文本（空间别名）
 * @param avatarData 头像数据
 * @param checked 是否被选中
 * @param onCheckedChange 选中状态变化回调
 * @param modifier 样式修饰符
 * @param enabled 是否启用
 */
@Composable
private fun CheckableSpaceListItem(
    headlineText: String,
    supportingText: String?,
    avatarData: AvatarData?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    ListItem(
        headlineContent = {
            Text(text = headlineText)
        },
        supportingContent = supportingText?.let {
            @Composable {
                Text(text = supportingText)
            }
        },
        leadingContent = avatarData?.let {
            ListItemContent.Custom {
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.Space(),
                )
            }
        },
        trailingContent = ListItemContent.Checkbox(
            checked = checked,
            enabled = enabled,
        ),
        enabled = enabled,
        onClick = { onCheckedChange(!checked) },
        modifier = modifier,
    )
}

/**
 * 管理授权空间页面顶部导航栏
 *
 * 包含页面标题、返回按钮和完成按钮。
 *
 * @param isDoneButtonEnabled 完成按钮是否可用
 * @param onBackClick 返回按钮点击事件处理
 * @param onDoneClick 完成按钮点击事件处理
 * @param modifier 样式修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageAuthorizedSpacesTopBar(
    isDoneButtonEnabled: Boolean,
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        titleStr = stringResource(R.string.screen_manage_authorized_spaces_title),
        navigationIcon = { BackButton(onClick = onBackClick) },
        actions = {
            TextButton(
                enabled = isDoneButtonEnabled,
                text = stringResource(CommonStrings.action_done),
                onClick = onDoneClick,
            )
        }
    )
}

@PreviewsDayNight
@Composable
internal fun ManageAuthorizedSpacesViewPreview(
    @PreviewParameter(ManageAuthorizedSpacesStateProvider::class) state: ManageAuthorizedSpacesState
) = ElementPreview {
    ManageAuthorizedSpacesView(state = state)
}
