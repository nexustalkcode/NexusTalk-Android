/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.detection

import io.element.android.libraries.architecture.Presenter

/**
 * 摇一摇检测 Presenter 接口
 *
 * 继承自 Presenter 接口，负责呈现摇一摇检测功能的状态。
 * 用于检测用户摇晃设备来触发问题报告流程。
 *
 * @see RageshakeDetectionState 摇一摇检测状态
 */
interface RageshakeDetectionPresenter : Presenter<RageshakeDetectionState>
