/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.impl.banner

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.knockrequests.impl.data.KnockRequestPresentable
import io.element.android.features.knockrequests.impl.data.aKnockRequestPresentable
import kotlinx.collections.immutable.toImmutableList

/**
 * 为敲门请求横幅预览提供样例状态。
 *
 * 覆盖了单条请求、多条请求、错误态、不可接受态以及长文案场景，
 * 便于在 Compose Preview 中检查布局和截断表现。
 */
class KnockRequestsBannerStateProvider : PreviewParameterProvider<KnockRequestsBannerState> {
    override val values: Sequence<KnockRequestsBannerState>
        get() = sequenceOf(
            aKnockRequestsBannerState(),
            aKnockRequestsBannerState(
                knockRequests = listOf(
                    aKnockRequestPresentable(
                        reason = "A very long reason that should probably be truncated, " +
                            "but could be also expanded so you can see it over the lines, wow," +
                            "very amazing reason, I know, right, I'm so good at writing reasons."
                    )
                )
            ),
            aKnockRequestsBannerState(
                knockRequests = listOf(
                    aKnockRequestPresentable(),
                    aKnockRequestPresentable(displayName = "Alice")
                )
            ),
            aKnockRequestsBannerState(
                knockRequests = listOf(
                    aKnockRequestPresentable(),
                    aKnockRequestPresentable(displayName = "Alice"),
                    aKnockRequestPresentable(displayName = "Bob"),
                    aKnockRequestPresentable(displayName = "Charlie")
                )
            ),
            aKnockRequestsBannerState(
                canAccept = false
            ),
            aKnockRequestsBannerState(
                displayAcceptError = true
            ),
            aKnockRequestsBannerState(
                knockRequests = listOf(
                    aKnockRequestPresentable(
                        displayName = "A_very_long_display_name_so_that_the_text_can_be_displayed_on_multiple_lines"
                    )
                )
            ),
        )
}

/**
 * 构造一份敲门请求横幅样例状态。
 *
 * @param knockRequests 当前待展示的敲门请求列表。
 * @param displayAcceptError 是否显示接受失败提示。
 * @param canAccept 当前是否允许直接接受请求。
 * @param isVisible 横幅是否可见。
 * @param eventSink 横幅事件的分发函数。
 */
fun aKnockRequestsBannerState(
    knockRequests: List<KnockRequestPresentable> = listOf(aKnockRequestPresentable()),
    displayAcceptError: Boolean = false,
    canAccept: Boolean = true,
    isVisible: Boolean = true,
    eventSink: (KnockRequestsBannerEvents) -> Unit = {}
) = KnockRequestsBannerState(
    knockRequests = knockRequests.toImmutableList(),
    displayAcceptError = displayAcceptError,
    canAccept = canAccept,
    isVisible = isVisible,
    eventSink = eventSink,
)
