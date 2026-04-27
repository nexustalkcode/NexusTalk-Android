/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.impl.data

/**
 * 敲门请求相关异常集合。
 */
sealed class KnockRequestsException : Exception() {
    /** 批量接受时仅部分请求成功。 */
    data object AcceptAllPartiallyFailed : KnockRequestsException()
    /** 要操作的敲门请求已经不存在。 */
    data object KnockRequestNotFound : KnockRequestsException()
}
