/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding.classic

/**
 * “使用 Element Classic 登录”页面可能触发的用户事件。
 */
sealed interface LoginWithClassicEvent {
    /** 刷新来自 Element Classic 的会话数据。 */
    data object RefreshData : LoginWithClassicEvent
    /** 进入登录确认态。 */
    data object StartLoginWithClassic : LoginWithClassicEvent
    /** 确认真正执行登录。 */
    data object DoLoginWithClassic : LoginWithClassicEvent
    /** 关闭当前对话框。 */
    data object CloseDialog : LoginWithClassicEvent
}
