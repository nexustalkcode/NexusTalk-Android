/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.crypto.historyvisible

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.appconfig.LearnMoreConfig
import io.element.android.libraries.designsystem.atomic.molecules.ComposerAlertLevel
import io.element.android.libraries.designsystem.atomic.molecules.ComposerAlertMolecule
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.text.stringWithLink
import io.element.android.libraries.ui.strings.CommonStrings

/**
 * 历史可见性状态视图
 *
 * 这是一个 Composable 函数，用于渲染历史可见性警告提示。
 * 当房间设置为历史可见（Shared或WorldReadable）且已加密时，
 * 此组件会向用户显示一个提示框，提醒他们聊天记录将对新成员可见。
 *
 * 组件功能：
 * - 显示信息级别的警告提示
 * - 包含"了解更多"链接，引导用户到帮助文档
 * - 提供"关闭"按钮，允许用户确认并关闭提示
 *
 * @param state 历史可见性状态，包含是否显示警告和事件处理函数
 * @param onLinkClick 链接点击回调，用于处理"了解更多"链接的点击事件
 * @param modifier 可选的修饰符，用于自定义组件样式和布局
 *
 * @see HistoryVisibleState 历史可见性状态数据类
 * @see HistoryVisibleEvent 历史可见性事件
 */
@Composable
fun HistoryVisibleStateView(
    state: HistoryVisibleState,
    onLinkClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!state.showAlert) {
        return
    }
    ComposerAlertMolecule(
        modifier = modifier,
        avatar = null,
        showIcon = true,
        level = ComposerAlertLevel.Info,
        content = stringWithLink(
            textRes = CommonStrings.crypto_history_visible,
            url = LearnMoreConfig.HISTORY_VISIBLE_URL,
            onLinkClick = { url -> onLinkClick(url, true) },
        ),
        submitText = stringResource(CommonStrings.action_dismiss),
        onSubmitClick = { state.eventSink(HistoryVisibleEvent.Acknowledge) },
    )
}

@PreviewsDayNight
@Composable
internal fun HistoryVisibleStateViewPreview(
    @PreviewParameter(HistoryVisibleStateProvider::class) state: HistoryVisibleState,
) = ElementPreview {
    HistoryVisibleStateView(
        state = state,
        onLinkClick = { _, _ -> },
    )
}
