/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.knockrequests.impl.list

import androidx.compose.runtime.Immutable
import io.element.android.features.knockrequests.api.KnockRequestPermissions
import io.element.android.features.knockrequests.impl.data.KnockRequestPresentable
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import kotlinx.collections.immutable.ImmutableList

/**
 * 敲门请求列表状态数据类
 *
 * 表示敲门请求列表界面的当前状态，包含请求列表和操作状态。
 *
 * @property knockRequests 敲门请求列表的异步数据
 * @property currentAction 当前正在执行的操作
 * @property asyncAction 异步操作的状态
 * @property permissions 敲门请求操作权限
 * @property eventSink 事件处理函数
 */
data class KnockRequestsListState(
    val knockRequests: AsyncData<ImmutableList<KnockRequestPresentable>>,
    val currentAction: KnockRequestsAction,
    val asyncAction: AsyncAction<Unit>,
    val permissions: KnockRequestPermissions,
    val eventSink: (KnockRequestsListEvents) -> Unit,
) {
    /** 是否可以接受所有请求 */
    val canAcceptAll = permissions.canAccept && knockRequests is AsyncData.Success && knockRequests.data.size > 1
}

/**
 * 敲门请求操作密封接口
 *
 * 定义对敲门请求可执行的各种操作。
 */
@Immutable
sealed interface KnockRequestsAction {
    /** 无操作 */
    data object None : KnockRequestsAction
    /**
     * 接受请求
     * @param knockRequest 要接受的敲门请求
     */
    data class Accept(val knockRequest: KnockRequestPresentable) : KnockRequestsAction
    /**
     * 拒绝请求
     * @param knockRequest 要拒绝的敲门请求
     */
    data class Decline(val knockRequest: KnockRequestPresentable) : KnockRequestsAction
    /**
     * 拒绝并封禁
     * @param knockRequest 要拒绝并封禁的敲门请求
     */
    data class DeclineAndBan(val knockRequest: KnockRequestPresentable) : KnockRequestsAction
    /** 接受所有请求 */
    data object AcceptAll : KnockRequestsAction
}
