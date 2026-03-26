/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.preferences

import io.element.android.libraries.architecture.Presenter

/**
 * 摇一摇偏好设置 Presenter 接口
 *
 * 继承自 Presenter 接口，负责呈现摇一摇偏好设置功能的状态。
 * 用于管理摇一摇功能的启用/禁用和灵敏度设置。
 *
 * @see RageshakePreferencesState 摇一摇偏好设置状态
 */
interface RageshakePreferencesPresenter : Presenter<RageshakePreferencesState>
