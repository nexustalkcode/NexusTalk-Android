/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.impl.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.element.android.features.invite.api.SeenInvitesStore
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.element.android.features.invite.impl.SeenInvitesStoreFactory
import io.element.android.features.invite.impl.acceptdecline.AcceptDeclineInvitePresenter
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.di.SessionScope
import io.element.android.libraries.matrix.api.MatrixClient

@ContributesTo(SessionScope::class)
@BindingContainer
/**
 * 邀请模块
 *
 * 定义了邀请功能的依赖注入绑定。
 * 提供接受/拒绝邀请 Presenter 和已查看邀请存储的依赖注入。
 */
interface InviteModule {
    /**
     * 绑定接受/拒绝邀请 Presenter
     *
     * @param presenter 接受/拒绝邀请 Presenter 实例
     * @return Presenter 实例
     */
    @Binds
    fun bindAcceptDeclinePresenter(presenter: AcceptDeclineInvitePresenter): Presenter<AcceptDeclineInviteState>

    companion object {
        /**
         * 提供已查看邀请存储
         *
         * @param factory 已查看邀请存储工厂
         * @param matrixClient Matrix 客户端
         * @return SeenInvitesStore 实例
         */
        @Provides
        fun providesSeenInvitesStore(
            factory: SeenInvitesStoreFactory,
            matrixClient: MatrixClient,
        ): SeenInvitesStore {
            return factory.getOrCreate(
                matrixClient.sessionId,
                matrixClient.sessionCoroutineScope,
            )
        }
    }
}
