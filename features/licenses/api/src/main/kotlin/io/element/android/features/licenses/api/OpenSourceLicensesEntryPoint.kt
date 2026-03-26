/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.licenses.api

import io.element.android.libraries.architecture.SimpleFeatureEntryPoint

/**
 * 开源许可证功能入口点接口
 *
 * 定义开源许可证查看功能的入口接口，
 * 继承自 SimpleFeatureEntryPoint，提供轻量级的功能入口。
 *
 * @see DefaultOpenSourcesLicensesEntryPoint 默认实现
 */
interface OpenSourceLicensesEntryPoint : SimpleFeatureEntryPoint
