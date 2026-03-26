/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.deactivation.api

import io.element.android.libraries.architecture.SimpleFeatureEntryPoint

/**
 * 账户停用功能入口点接口
 *
 * 定义账户停用功能的入口点，继承自 SimpleFeatureEntryPoint。
 * 该接口是账户停用功能模块的公共 API，用于导航到账户停用界面。
 */
interface AccountDeactivationEntryPoint : SimpleFeatureEntryPoint
