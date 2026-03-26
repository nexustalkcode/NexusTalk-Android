/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.ftue.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import io.element.android.features.ftue.impl.sessionverification.choosemode.ChooseSelfVerificationModePresenter
import io.element.android.features.ftue.impl.sessionverification.choosemode.ChooseSelfVerificationModeState
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.di.SessionScope

/**
 * FTUE 模块依赖注入配置接口
 *
 * 该接口用于配置 FTUE 功能模块的依赖注入绑定。
 * 使用 @ContributesTo 注解将其添加到 SessionScope，意味着这些依赖绑定仅在用户会话期间有效。
 *
 * 主要功能：
 * - 绑定 ChooseSelfVerificationModePresenter 到 Presenter 接口
 */
@ContributesTo(SessionScope::class)
@BindingContainer
interface FtueModule {
    /**
     * 绑定自验证方式选择 Presenter
     *
     * 将 ChooseSelfVerificationModePresenter 绑定到通用的 Presenter 接口，
     * 使得可以按类型注入任意 Presenter 实现。
     *
     * @param presenter ChooseSelfVerificationModePresenter 实例
     * @return 绑定到 ChooseSelfVerificationModeState 的 Presenter
     */
    @Binds
    fun bindChooseSelfVerificationMethodPresenter(presenter: ChooseSelfVerificationModePresenter): Presenter<ChooseSelfVerificationModeState>
}
