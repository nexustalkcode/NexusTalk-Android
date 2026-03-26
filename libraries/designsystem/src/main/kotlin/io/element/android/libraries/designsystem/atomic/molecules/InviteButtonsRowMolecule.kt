/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 邀请按钮行分子组件
 *
 * 用于显示邀请操作的按钮行组件。
 * 包含拒绝和接受两个按钮，水平排列。
 * 常用于房间邀请、好友请求等场景的操作按钮。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.OutlinedButton
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 邀请按钮行组件
 *
 * 创建一个包含拒绝和接受按钮的水平行布局。
 * 两个按钮宽度相等，水平间距为 12dp。
 *
 * @param onAcceptClick () -> Unit 点击接受按钮时的回调函数
 * @param onDeclineClick () -> Unit 点击拒绝按钮时的回调函数
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param declineText String 拒绝按钮文本，默认为"拒绝"
 * @param acceptText String 接受按钮文本，默认为"接受"
 *
 * @return Unit
 *
 * @see [OutlinedButton] 边框按钮组件（拒绝）
 * @see [Button] 实心按钮组件（接受）
 * @see [ButtonSize] 按钮尺寸规格
 *
 * @example
 * ```kotlin
 * InviteButtonsRowMolecule(
 *     onAcceptClick = { /* 处理接受 */ },
 *     onDeclineClick = { /* 处理拒绝 */ }
 * )
 * ```
 */
@Composable
fun InviteButtonsRowMolecule(
    onAcceptClick: () -> Unit,
    onDeclineClick: () -> Unit,
    modifier: Modifier = Modifier,
    declineText: String = stringResource(CommonStrings.action_decline),
    acceptText: String = stringResource(CommonStrings.action_accept),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = spacedBy(12.dp)
    ) {
        OutlinedButton(
            text = declineText,
            onClick = onDeclineClick,
            size = ButtonSize.MediumLowPadding,
            modifier = Modifier.weight(1f),
        )
        Button(
            text = acceptText,
            onClick = onAcceptClick,
            size = ButtonSize.MediumLowPadding,
            modifier = Modifier.weight(1f),
        )
    }
}
