/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

/**
 * 图标标题副标题分子组件
 *
 * 用于显示图标、标题和副标题的组合组件。
 * 图标位于顶部中央，标题和副标题依次垂直排列。
 * 适用于空状态页面、成功提示、错误提示等场景。
 *
 * @author Element Creations Ltd.
 * @version 1.0.0
 * @since 2025-01-01
 */
package io.element.android.libraries.designsystem.atomic.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.atomic.atoms.BetaLabel
import io.element.android.libraries.designsystem.components.BigIcon
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text

/**
 * 图标标题副标题组件
 *
 * 创建一个包含图标、标题和可选副标题的组合组件。
 * 图标采用 BigIcon.Style 样式，标题使用加粗字重，副标题使用常规字重。
 * 支持可选的 Beta 标签显示。
 *
 * @param title String 标题文本内容
 * @param subTitle String? 副标题文本内容，可为 null
 * @param iconStyle BigIcon.Style 大图标的样式配置
 * @param modifier Modifier 修饰符，用于自定义组件的布局和样式，默认为 Modifier
 * @param showBetaLabel Boolean 是否在标题旁显示 Beta 标签，默认为 false
 *
 * @return Unit
 *
 * @see [BigIcon] 大图标组件
 * @see [BetaLabel] Beta 标签组件
 * @see [ElementTheme.typography.fontHeadingMdBold] 标题文本样式
 *
 * @example
 * ```kotlin
 * IconTitleSubtitleMolecule(
 *     title = "操作成功",
 *     subTitle = "您的更改已保存",
 *     iconStyle = BigIcon.Style.Success
 * )
 * ```
 */
@Composable
fun IconTitleSubtitleMolecule(
    title: String,
    subTitle: String?,
    iconStyle: BigIcon.Style,
    modifier: Modifier = Modifier,
    showBetaLabel: Boolean = false,
) {
    Column(modifier) {
        BigIcon(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = iconStyle,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            itemVerticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .semantics {
                        heading()
                    },
                textAlign = TextAlign.Center,
                style = ElementTheme.typography.fontHeadingMdBold,
                color = ElementTheme.colors.textPrimary,
            )
            if (showBetaLabel) {
                BetaLabel()
            }
        }
        if (subTitle != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = subTitle,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary,
            )
        }
    }
}

/**
 * IconTitleSubtitleMolecule 预览组件
 *
 * 用于在设计预览中展示 IconTitleSubtitleMolecule 组件的默认状态。
 * 此预览函数支持日夜两种主题模式。
 */
@PreviewsDayNight
@Composable
internal fun IconTitleSubtitleMoleculePreview() = ElementPreview {
    IconTitleSubtitleMolecule(
        iconStyle = BigIcon.Style.Default(CompoundIcons.Chat()),
        title = "Title",
        subTitle = "Subtitle",
    )
}
