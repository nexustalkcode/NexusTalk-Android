/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.core

import java.io.Serializable

/**
 * UserId 本质上只是一个通用 Matrix 标识值对象。
 * 下沉到 core 后，依赖它的基础库不必再为了这个轻量类型反向依赖 matrix api。
 */
@JvmInline
value class UserId(val value: String) : Serializable {
    override fun toString(): String = value

    val extractedDisplayName: String
        get() = value
            .removePrefix("@")
            .substringBefore(":")

    val domainName: String?
        get() = value.substringAfter(":").takeIf { it.isNotEmpty() }
}
