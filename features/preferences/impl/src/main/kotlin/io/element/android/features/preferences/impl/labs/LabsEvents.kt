/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.labs

import io.element.android.libraries.featureflag.ui.model.FeatureUiModel

/**
 * 实验室功能页面事件密封接口
 *
 * 定义实验室功能页面中可能发生的各种用户交互事件。
 */
sealed interface LabsEvents {
    /** 切换功能启用状态 */
    data class ToggleFeature(val feature: FeatureUiModel) : LabsEvents
}
