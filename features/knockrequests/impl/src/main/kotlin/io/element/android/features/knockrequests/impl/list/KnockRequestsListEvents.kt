/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.impl.list

import io.element.android.features.knockrequests.impl.data.KnockRequestPresentable

/**
 * 敲门请求列表页可能触发的用户事件。
 */
sealed interface KnockRequestsListEvents {
    /** 接受单条敲门请求。 */
    data class Accept(val knockRequest: KnockRequestPresentable) : KnockRequestsListEvents
    /** 拒绝单条敲门请求。 */
    data class Decline(val knockRequest: KnockRequestPresentable) : KnockRequestsListEvents
    /** 拒绝并封禁单条敲门请求。 */
    data class DeclineAndBan(val knockRequest: KnockRequestPresentable) : KnockRequestsListEvents
    /** 接受全部敲门请求。 */
    data object AcceptAll : KnockRequestsListEvents
    /** 重置当前动作状态。 */
    data object ResetCurrentAction : KnockRequestsListEvents
    /** 重试当前动作。 */
    data object RetryCurrentAction : KnockRequestsListEvents
    /** 确认当前动作。 */
    data object ConfirmCurrentAction : KnockRequestsListEvents
}
