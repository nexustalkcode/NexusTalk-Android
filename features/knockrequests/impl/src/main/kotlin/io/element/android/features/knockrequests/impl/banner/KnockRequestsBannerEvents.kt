/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.impl.banner

/**
 * 敲门请求横幅上可能触发的用户事件。
 */
sealed interface KnockRequestsBannerEvents {
    /**
     * 接受当前横幅主展示的单条敲门请求。
     */
    data object AcceptSingleRequest : KnockRequestsBannerEvents

    /**
     * 关闭当前横幅。
     */
    data object Dismiss : KnockRequestsBannerEvents
}
