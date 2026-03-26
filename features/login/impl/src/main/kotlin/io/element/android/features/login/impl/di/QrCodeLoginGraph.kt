/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import io.element.android.libraries.architecture.NodeFactoriesBindings

/**
 * 二维码登录依赖图
 *
 * 使用 Metro 框架的 GraphExtension 扩展的依赖图接口。
 * 定义二维码登录模块的依赖注入配置。
 *
 * @property Factory 工厂接口，用于创建依赖图实例
 * @see QrCodeLoginScope 二维码登录作用域
 * @see NodeFactoriesBindings 节点工厂绑定接口
 */
@GraphExtension(QrCodeLoginScope::class)
interface QrCodeLoginGraph : NodeFactoriesBindings {
    /**
     * 依赖图工厂接口
     *
     * 用于创建 QrCodeLoginGraph 实例的工厂方法。
     */
    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        /**
         * 创建二维码登录依赖图实例
         *
         * @return QrCodeLoginGraph 实例
         */
        fun create(): QrCodeLoginGraph
    }
}
