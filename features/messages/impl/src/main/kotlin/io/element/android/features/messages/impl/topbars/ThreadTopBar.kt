/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.topbars

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.components.avatar.anAvatarData
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.matrix.ui.components.aMatrixUserList
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * 线程页面顶部栏组件
 *
 * 线程消息页面的顶部栏，显示：
 * - 返回按钮
 * - 房间头像和名称
 * - 固定显示"线程"标签
 *
 * @param roomName 房间名称，如果为null则显示"无房间名称"
 * @param roomAvatarData 房间头像数据
 * @param heroes 房间中的重要成员头像列表（用于群组头像显示）
 * @param isTombstoned 是否为已废弃的房间
 * @param onBackClick 点击返回按钮时的回调
 * @param modifier 视图修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThreadTopBar(
    roomName: String?,
    roomAvatarData: AvatarData,
    heroes: ImmutableList<AvatarData>,
    isTombstoned: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            BackButton(onClick = onBackClick)
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(
                    avatarData = roomAvatarData,
                    avatarType = AvatarType.Room(
                        heroes = heroes,
                        isTombstoned = isTombstoned,
                    ),
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .semantics {
                            heading()
                        },
                ) {
                    Text(
                        text = stringResource(CommonStrings.common_thread),
                        style = ElementTheme.typography.fontBodyLgMedium,
                    )
                    Text(
                        text = roomName ?: stringResource(CommonStrings.common_no_room_name),
                        style = ElementTheme.typography.fontBodySmRegular,
                        fontStyle = FontStyle.Italic.takeIf { roomName == null },
                        color = ElementTheme.colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    )
}

@PreviewsDayNight
@Composable
internal fun ThreadTopBarPreview() = ElementPreview {
    @Composable
    fun AThreadTopBar(
        roomName: String? = "Room name",
        roomAvatarData: AvatarData = anAvatarData(
            name = "Room name",
            size = AvatarSize.TimelineRoom,
        ),
        isTombstoned: Boolean = false,
        heroes: ImmutableList<AvatarData> = persistentListOf(),
    ) = ThreadTopBar(
        roomName = roomName,
        roomAvatarData = roomAvatarData,
        isTombstoned = isTombstoned,
        heroes = heroes,
        onBackClick = {},
    )
    Column {
        AThreadTopBar()
        HorizontalDivider()
        AThreadTopBar(
            heroes = aMatrixUserList().map { it.getAvatarData(AvatarSize.TimelineRoom) }.toImmutableList(),
        )
        HorizontalDivider()
        AThreadTopBar(
            roomName = null,
        )
        HorizontalDivider()
        AThreadTopBar(
            roomAvatarData = anAvatarData(
                name = "Room name",
                url = "https://some-avatar.jpg",
                size = AvatarSize.TimelineRoom,
            ),
        )
        HorizontalDivider()
        AThreadTopBar(
            isTombstoned = true,
        )
    }
}
