/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.notificationsettings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.element.android.features.roomdetails.impl.R
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.matrix.api.room.RoomNotificationMode

/**
 * 通知设置选项组件
 *
 * Composable 函数，用于渲染单个通知设置选项。
 * 显示选项标题、副标题（可选）和单选按钮。
 *
 * @param roomNotificationSettingsItem 通知设置项
 * @param onSelectOption 选项选择回调
 * @param displayMentionsOnlyDisclaimer 是否显示仅提及免责声明
 * @param modifier 视图修饰符
 * @param enabled 是否启用
 * @param isSelected 是否已选中
 * @see RoomNotificationSettingsItem 通知设置项
 */
@Composable
fun RoomNotificationSettingsOption(
    roomNotificationSettingsItem: RoomNotificationSettingsItem,
    onSelectOption: (RoomNotificationSettingsItem) -> Unit,
    displayMentionsOnlyDisclaimer: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSelected: Boolean = false,
) {
    val mode = roomNotificationSettingsItem.mode
    val title = roomNotificationSettingsItem.title
    val subtitle = when {
        mode == RoomNotificationMode.MENTIONS_AND_KEYWORDS_ONLY && displayMentionsOnlyDisclaimer -> {
            stringResource(id = R.string.screen_notification_settings_mentions_only_disclaimer)
        }
        else -> null
    }
    ListItem(
        modifier = modifier,
        enabled = enabled,
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let { { Text(it) } },
        trailingContent = ListItemContent.RadioCheckbox(selected = isSelected),
        onClick = { onSelectOption(roomNotificationSettingsItem) },
    )
}

@PreviewsDayNight
@Composable
internal fun RoomNotificationSettingsOptionPreview() = ElementPreview {
    Column {
        for ((index, item) in roomNotificationSettingsItems().withIndex()) {
            RoomNotificationSettingsOption(
                roomNotificationSettingsItem = item,
                onSelectOption = {},
                isSelected = index == 0,
                enabled = index != 2,
                displayMentionsOnlyDisclaimer = index == 1,
            )
        }
    }
}
