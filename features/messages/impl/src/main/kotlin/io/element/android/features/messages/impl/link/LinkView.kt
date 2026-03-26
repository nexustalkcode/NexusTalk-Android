/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.link

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.core.extensions.ensureEndsLeftToRight
import io.element.android.libraries.core.extensions.filterDirectionOverrides
import io.element.android.libraries.designsystem.components.dialogs.ConfirmationDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.wysiwyg.link.Link

/**
 * 链接视图组件
 *
 * 负责显示链接处理的确认对话框。
 * 当用户点击的链接未通过安全检查时，会显示确认对话框让用户决定是否继续打开链接。
 *
 * @param state 当前链接状态，包含链接点击的异步操作状态
 * @param onLinkValid 当链接确认后调用的回调，传入确认的链接
 * @param modifier 视图修饰符
 */
@Composable
fun LinkView(
    state: LinkState,
    onLinkValid: (Link) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state.linkClick) {
        AsyncAction.Uninitialized,
        AsyncAction.Loading,
        is AsyncAction.Failure -> Unit
        is AsyncAction.Confirming -> {
            if (state.linkClick is ConfirmingLinkClick) {
                ConfirmationDialog(
                    modifier = modifier,
                    title = stringResource(CommonStrings.dialog_confirm_link_title),
                    content = stringResource(
                        CommonStrings.dialog_confirm_link_message,
                        state.linkClick.link.text.ensureEndsLeftToRight(),
                        state.linkClick.link.url.filterDirectionOverrides(),
                    ),
                    submitText = stringResource(CommonStrings.action_continue),
                    onSubmitClick = {
                        state.eventSink(LinkEvents.Confirm)
                    },
                    onDismiss = {
                        state.eventSink(LinkEvents.Cancel)
                    },
                )
            }
        }
        is AsyncAction.Success -> {
            val latestOnLinkValid by rememberUpdatedState(onLinkValid)
            LaunchedEffect(state.linkClick.data) {
                latestOnLinkValid(state.linkClick.data)
                state.eventSink(LinkEvents.Cancel)
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun LinkViewPreview(@PreviewParameter(LinkStateProvider::class) state: LinkState) = ElementPreview {
    LinkView(
        state = state,
        onLinkValid = {},
    )
}
