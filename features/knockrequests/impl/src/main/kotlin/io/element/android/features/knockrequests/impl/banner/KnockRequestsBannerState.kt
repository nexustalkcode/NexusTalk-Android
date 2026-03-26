/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.impl.banner

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import io.element.android.features.knockrequests.impl.R
import io.element.android.features.knockrequests.impl.data.KnockRequestPresentable
import kotlinx.collections.immutable.ImmutableList

/**
 * 敲门请求横幅状态数据类
 *
 * 表示房间列表中敲门请求横幅的当前状态。
 *
 * @property isVisible 横幅是否可见
 * @property knockRequests 敲门请求列表
 * @property displayAcceptError 是否显示接受错误
 * @property canAccept 是否可以接受请求
 * @property eventSink 事件处理函数
 */
data class KnockRequestsBannerState(
    val isVisible: Boolean,
    val knockRequests: ImmutableList<KnockRequestPresentable>,
    val displayAcceptError: Boolean,
    val canAccept: Boolean,
    val eventSink: (KnockRequestsBannerEvents) -> Unit,
) {
    /** 副标题，显示单个请求者的 ID */
    val subtitle = knockRequests.singleOrNull()?.userId?.value
    /** 敲门原因 */
    val reason = knockRequests.singleOrNull()?.reason

    /**
     * 获取格式化的标题文本
     *
     * @return String 格式化的标题
     */
    @Composable
    fun formattedTitle(): String {
        return when (knockRequests.size) {
            0 -> ""
            1 -> stringResource(R.string.screen_room_single_knock_request_title, knockRequests.first().getBestName())
            else -> {
                val firstRequest = knockRequests.first()
                val otherRequestsCount = knockRequests.size - 1
                pluralStringResource(
                    id = R.plurals.screen_room_multiple_knock_requests_title,
                    count = otherRequestsCount,
                    firstRequest.getBestName(),
                    otherRequestsCount
                )
            }
        }
    }
}
