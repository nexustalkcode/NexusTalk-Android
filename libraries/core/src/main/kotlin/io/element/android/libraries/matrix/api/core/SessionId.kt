/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.core

/**
 * 当前登录用户的会话 ID。
 * 继续复用 UserId 语义，但把类型别名放到 core，避免基础库继续依赖 matrix api。
 */
typealias SessionId = UserId
