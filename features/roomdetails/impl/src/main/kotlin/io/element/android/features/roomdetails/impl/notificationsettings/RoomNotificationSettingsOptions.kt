/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomdetails.impl.notificationsettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.element.android.libraries.matrix.api.room.RoomNotificationMode

/**
 * 通知设置选项列表组件
 *
 * Composable 函数，用于渲染通知设置选项列表。
 * 使用可选择组容器，包含所有可用的通知模式选项。
 *
 * @param selected 当前选中的通知模式
 * @param enabled 是否启用
 * @param onSelectOption 选项选择回调
 * @param displayMentionsOnlyDisclaimer 是否显示仅提及免责声明
 * @param modifier 视图修饰符
 * @see RoomNotificationMode 房间通知模式
 * @see RoomNotificationSettingsItem 通知设置项
 */
@Composable
fun RoomNotificationSettingsOptions(
    selected: RoomNotificationMode?,
    enabled: Boolean,
    onSelectOption: (RoomNotificationSettingsItem) -> Unit,
    displayMentionsOnlyDisclaimer: Boolean,
    modifier: Modifier = Modifier,
) {
    val items = roomNotificationSettingsItems()
    Column(modifier = modifier.selectableGroup()) {
        items.forEach { item ->
            RoomNotificationSettingsOption(
                roomNotificationSettingsItem = item,
                isSelected = selected == item.mode,
                onSelectOption = onSelectOption,
                displayMentionsOnlyDisclaimer = displayMentionsOnlyDisclaimer,
                enabled = enabled
            )
        }
    }
}
