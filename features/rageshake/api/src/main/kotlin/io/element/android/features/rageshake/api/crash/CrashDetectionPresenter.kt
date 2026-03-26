/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.rageshake.api.crash

import io.element.android.libraries.architecture.Presenter

/**
 * 崩溃检测 Presenter 接口
 *
 * 继承自 Presenter 接口，负责呈现崩溃检测功能的状态。
 * 用于检测应用是否发生了崩溃，并提供相应的用户界面状态。
 *
 * @see CrashDetectionState 崩溃检测状态
 */
interface CrashDetectionPresenter : Presenter<CrashDetectionState>
