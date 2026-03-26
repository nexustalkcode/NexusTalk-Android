/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.appconfig

/**
 * Element Call 配置 (ElementCall Configuration)
 *
 * 此对象包含与Element Call视频通话功能相关的配置项。
 * Element Call是Element的集成视频通话解决方案。
 */
object ElementCallConfig {
    /**
     * 呼叫响铃的默认持续时间（秒），超过此时间后呼叫会自动挂断。
     * 当用户收到来电但未接听时，呼叫会在此时间后自动取消。
     */
    const val RINGING_CALL_DURATION_SECONDS = 90
}
