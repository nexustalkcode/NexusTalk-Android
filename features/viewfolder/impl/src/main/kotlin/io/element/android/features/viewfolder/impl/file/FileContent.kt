/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.viewfolder.impl.file

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.androidutils.system.copyToClipboard
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.ImmutableList

/**
 * 文件内容组件
 *
 * 使用 Jetpack Compose 实现文件内容的展示组件。
 * 支持显示行号、点击复制行内容、根据着色模式显示不同颜色。
 *
 * @param lines 文件内容的行列表
 * @param colorationMode 着色模式
 * @param modifier 修饰符
 */
@Composable
internal fun FileContent(
    lines: ImmutableList<String>,
    colorationMode: ColorationMode,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
    ) {
        if (lines.isEmpty()) {
            item {
                Spacer(Modifier.size(80.dp))
                Text(
                    text = stringResource(CommonStrings.common_empty_file),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            itemsIndexed(
                items = lines,
            ) { index, line ->
                LineRow(
                    lineNumber = index + 1,
                    line = line,
                    colorationMode = colorationMode,
                )
            }
        }
    }
}

/**
 * 行组件
 *
 * 渲染文件中的单行内容，包含行号和行内容。
 * 点击整行可复制内容到剪贴板。
 *
 * @param lineNumber 行号
 * @param line 行内容
 * @param colorationMode 着色模式
 */
@Composable
private fun LineRow(
    lineNumber: Int,
    line: String,
    colorationMode: ColorationMode,
) {
    val context = LocalContext.current
    val toastMessage = stringResource(CommonStrings.common_line_copied_to_clipboard)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                context.copyToClipboard(
                    text = line,
                    toastMessage = toastMessage,
                )
            })
    ) {
        Text(
            modifier = Modifier
                .widthIn(min = 36.dp)
                .padding(horizontal = 4.dp),
            text = "$lineNumber",
            textAlign = TextAlign.End,
            color = ElementTheme.colors.textSecondary,
            style = ElementTheme.typography.fontBodyMdMedium,
        )
        val color = ElementTheme.colors.textSecondary
        val width = 0.5.dp.value
        Text(
            modifier = Modifier
                .weight(1f)
                .drawWithContent {
                    // Using .height(IntrinsicSize.Min) on the Row does not work well inside LazyColumn
                    drawLine(
                        color = color,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = width
                    )
                    drawContent()
                }
                .padding(horizontal = 4.dp),
            text = line,
            color = line.toColor(colorationMode),
            style = ElementTheme.typography.fontBodyMdRegular
        )
    }
}

/**
 * 将行内容转换为颜色
 *
 * 根据着色模式确定行的显示颜色：
 * - Logcat 模式：根据第 32 个字符确定颜色（V/D/I/W/E/A）
 * - RustLogs 模式：根据第 33 个字符确定颜色（TRACE/DEBUG/INFO/WARN/ERROR）
 * - None 模式：使用默认前景色
 *
 * @param colorationMode 着色模式
 * @return 行内容的显示颜色
 */
@Composable
private fun String.toColor(colorationMode: ColorationMode): Color {
    return when (colorationMode) {
        ColorationMode.Logcat -> when (getOrNull(31)) {
            'D' -> colorDebug
            'I' -> colorInfo
            'W' -> colorWarning
            'E' -> colorError
            'A' -> colorError
            else -> ElementTheme.colors.textPrimary
        }
        ColorationMode.RustLogs -> when (getOrNull(32)) {
            'E' -> ElementTheme.colors.textPrimary
            'G' -> colorDebug
            'O' -> colorInfo
            'N' -> colorWarning
            'R' -> colorError
            else -> ElementTheme.colors.textPrimary
        }
        ColorationMode.None -> ElementTheme.colors.textPrimary
    }
}

/** 调试级别颜色 */
private val colorDebug = Color(0xFF299999)
/** 信息级别颜色 */
private val colorInfo = Color(0xFFABC023)
/** 警告级别颜色 */
private val colorWarning = Color(0xFFBBB529)
/** 错误级别颜色 */
private val colorError = Color(0xFFFF6B68)
