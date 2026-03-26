/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import io.element.android.features.login.impl.changeserver.ChangeServerPresenter
import io.element.android.features.login.impl.changeserver.ChangeServerState
import io.element.android.features.login.impl.screens.onboarding.classic.LoginWithClassicPresenter
import io.element.android.features.login.impl.screens.onboarding.classic.LoginWithClassicState
import io.element.android.libraries.architecture.Presenter

/**
 * 登录模块依赖注入配置
 *
 * 使用 Metro 依赖注入框架的模块接口。
 * 定义登录功能中 Presenter 的依赖绑定关系。
 *
 * @see ChangeServerPresenter 更改服务器 Presenter
 * @see LoginWithClassicPresenter 经典登录 Presenter
 */
@ContributesTo(AppScope::class)
@BindingContainer
interface LoginModule {
    /**
     * 绑定更改服务器 Presenter
     *
     * @param presenter ChangeServerPresenter 实例
     * @return 绑定后的 Presenter 接口
     */
    @Binds
    fun bindChangeServerPresenter(presenter: ChangeServerPresenter): Presenter<ChangeServerState>

    /**
     * 绑定经典登录 Presenter
     *
     * @param presenter LoginWithClassicPresenter 实例
     * @return 绑定后的 Presenter 接口
     */
    @Binds
    fun bindLoginWithClassicPresenter(presenter: LoginWithClassicPresenter): Presenter<LoginWithClassicState>
}
