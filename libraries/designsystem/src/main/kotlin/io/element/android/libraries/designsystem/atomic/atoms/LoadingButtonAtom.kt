/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 加载按钮原子组件
 *
 * 一个占位用的加载状态按钮组件，在异步操作进行中时显示加载指示器。
 * 按钮宽度自动填充父容器，禁用交互并显示进度动画。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.atoms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 加载按钮组件
 *
 * 创建一个显示加载状态的按钮组件。
 * 按钮禁用交互，显示进度指示器，并显示"Loading..."文本。
 *
 * @param modifier Modifier 修饰符，用于自定义按钮的布局和样式，默认为 Modifier
 *
 * @return Unit
 *
 * @see [Button] 基础按钮组件
 * @see [CommonStrings.common_loading] 加载文本资源
 */
@Composable
fun LoadingButtonAtom(
    modifier: Modifier = Modifier,
) = Button(
    modifier = modifier.fillMaxWidth(),
    enabled = false,
    showProgress = true,
    text = stringResource(CommonStrings.common_loading),
    onClick = {},
)
