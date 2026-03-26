/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.ui.components.MatrixUserHeader
import io.element.android.libraries.matrix.ui.components.MatrixUserWithNullProvider

/**
 * 用户首选项组件
 *
 * 显示用户信息头部，包括头像、名称和二维码按钮。
 *
 * @param user Matrix 用户信息
 * @param modifier 修饰符
 * @param onQrCodeClick 二维码点击回调
 * @param avatarSize 头像大小
 */
@Composable
fun UserPreferences(
    user: MatrixUser?,
    modifier: Modifier = Modifier,
    onQrCodeClick: (() -> Unit)? = null,
    showChevron: Boolean = false,
    avatarSize: Dp? = null,
) {
    MatrixUserHeader(
        modifier = modifier,
        matrixUser = user,
        onQrCodeClick = onQrCodeClick,
        showChevron = showChevron,
        avatarSize = avatarSize,
    )
}

@PreviewsDayNight
@Composable
internal fun UserPreferencesPreview(@PreviewParameter(MatrixUserWithNullProvider::class) matrixUser: MatrixUser?) = ElementPreview {
    UserPreferences(matrixUser)
}
