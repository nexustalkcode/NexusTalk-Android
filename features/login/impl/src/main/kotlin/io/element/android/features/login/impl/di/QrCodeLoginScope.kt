/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.di

/**
 * 二维码登录作用域
 *
 * Metro 依赖注入框架中的作用域标记类。
 * 用于限定二维码登录相关依赖的生命周期。
 * 私有的构造函数防止直接实例化。
 *
 * @see QrCodeLoginBindings 二维码登录绑定接口
 * @see QrCodeLoginGraph 二维码登录依赖图
 */
abstract class QrCodeLoginScope private constructor()
