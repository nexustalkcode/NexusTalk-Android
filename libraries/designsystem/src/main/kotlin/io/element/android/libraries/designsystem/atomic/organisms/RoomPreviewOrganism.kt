/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 房间预览有机体组件
 *
 * 用于显示房间预览信息的复合组件。
 * 包含头像、标题、副标题、可选成员数和描述。
 * 适用于房间邀请、房间链接预览等场景。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 房间预览组件
 *
 * 创建一个垂直居中排列的房间预览信息组件。
 * 按顺序包含：头像、标题、副标题、可选的成员数、可选的描述。
 *
 * @param avatar @Composable () -> Unit 头像组件
 * @param title @Composable () -> Unit 标题组件
 * @param subtitle @Composable () -> Unit 副标题组件
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param description @Composable (() -> Unit)? 可选的描述组件，默认为 null
 * @param memberCount @Composable (() -> Unit)? 可选的成员数组件，默认为 null
 *
 * @return Unit
 *
 * @see [RoomPreviewTitleAtom] 标题组件
 * @see [RoomPreviewSubtitleAtom] 副标题组件
 * @see [MembersCountMolecule] 成员数组件
 *
 * @example
 * ```kotlin
 * RoomPreviewOrganism(
 *     avatar = { Avatar(avatarData) },
 *     title = { RoomPreviewTitleAtom(title = "房间名称") },
 *     subtitle = { RoomPreviewSubtitleAtom(subtitle = "房间描述") }
 * )
 * ```
 */
@Composable
fun RoomPreviewOrganism(
    avatar: @Composable () -> Unit,
    title: @Composable () -> Unit,
    subtitle: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    description: @Composable (() -> Unit)? = null,
    memberCount: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        avatar()
        Spacer(modifier = Modifier.height(16.dp))
        title()
        Spacer(modifier = Modifier.height(8.dp))
        subtitle()
        if (memberCount != null) {
            Spacer(modifier = Modifier.height(8.dp))
            memberCount()
        }
        if (description != null) {
            Spacer(modifier = Modifier.height(16.dp))
            description()
        }
    }
}
