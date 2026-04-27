/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.impl.banner

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.knockrequests.api.banner.KnockRequestsBannerRenderer
import io.element.android.libraries.di.RoomScope

@ContributesBinding(RoomScope::class)
/**
 * 默认的敲门请求横幅渲染器。
 *
 * 负责把 [KnockRequestsBannerPresenter] 产出的状态桥接到 [KnockRequestsBannerView]。
 */
class DefaultKnockRequestsBannerRenderer(
    private val presenter: KnockRequestsBannerPresenter,
) : KnockRequestsBannerRenderer {
    /**
     * 渲染当前房间的敲门请求横幅。
     *
     * @param modifier 应用于横幅根节点的修饰符。
     * @param onViewRequestsClick 点击“查看请求”后的回调。
     */
    @Composable
    override fun View(modifier: Modifier, onViewRequestsClick: () -> Unit) {
        val state = presenter.present()
        KnockRequestsBannerView(
            state = state,
            onViewRequestsClick = onViewRequestsClick,
        )
    }
}
