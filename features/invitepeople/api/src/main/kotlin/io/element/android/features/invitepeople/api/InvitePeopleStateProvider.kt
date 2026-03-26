/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invitepeople.api

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction

/**
 * 邀请人员状态预览参数提供者
 *
 * 实现Compose的PreviewParameterProvider接口，用于在预览中提供各种状态的InvitePeopleState。
 * 主要用于开发阶段的UI预览，支持多种场景状态的展示。
 *
 * @see InvitePeopleState 页面状态定义
 */
class InvitePeopleStateProvider : PreviewParameterProvider<InvitePeopleState> {
    /**
     * 预览状态序列
     *
     * 提供多种不同的状态用于UI预览，包括：默认状态、可邀请状态、搜索激活状态、发送中状态等。
     */
    override val values: Sequence<InvitePeopleState>
        get() = sequenceOf(
            aPreviewInvitePeopleState(),
            aPreviewInvitePeopleState(canInvite = true),
            aPreviewInvitePeopleState(isSearchActive = true),
            aPreviewInvitePeopleState(sendInvitesAction = AsyncAction.Loading),
        )
}

/**
 * 预览用的邀请人员状态数据类
 *
 * 实现InvitePeopleState接口，用于创建预览状态。
 *
 * @property canInvite 是否可以发送邀请
 * @property isSearchActive 搜索是否激活
 * @property sendInvitesAction 发送邀请操作状态
 * @property eventSink 事件处理函数
 */
private data class PreviewInvitePeopleState(
    override val canInvite: Boolean,
    override val isSearchActive: Boolean,
    override val sendInvitesAction: AsyncAction<Unit>,
    override val eventSink: (InvitePeopleEvents) -> Unit,
) : InvitePeopleState

/**
 * 创建预览用的邀请人员状态
 *
 * 辅助函数，用于快速创建不同配置的预览状态。
 *
 * @param canInvite 是否可以发送邀请，默认false
 * @param isSearchActive 搜索是否激活，默认false
 * @param sendInvitesAction 发送邀请操作状态，默认未初始化
 * @param eventSink 事件处理函数，默认空函数
 * @return 配置好的预览状态
 */
private fun aPreviewInvitePeopleState(
    canInvite: Boolean = false,
    isSearchActive: Boolean = false,
    sendInvitesAction: AsyncAction<Unit> = AsyncAction.Uninitialized,
    eventSink: (InvitePeopleEvents) -> Unit = {},
) = PreviewInvitePeopleState(
    canInvite = canInvite,
    isSearchActive = isSearchActive,
    sendInvitesAction = sendInvitesAction,
    eventSink = eventSink
)
