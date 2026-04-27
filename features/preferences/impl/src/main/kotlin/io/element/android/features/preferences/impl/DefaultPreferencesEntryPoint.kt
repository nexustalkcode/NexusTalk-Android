/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.preferences.api.PreferencesEntryPoint
import io.element.android.libraries.architecture.createNode

@ContributesBinding(AppScope::class)
/**
 * 默认的设置入口实现。
 */
class DefaultPreferencesEntryPoint : PreferencesEntryPoint {
    /**
     * 创建设置流程节点。
     */
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: PreferencesEntryPoint.Params,
        callback: PreferencesEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<PreferencesFlowNode>(
            buildContext = buildContext,
            plugins = listOf(params, callback)
        )
    }
}

/**
 * 把公开入口层的初始目标映射为设置流程内部导航目标。
 */
internal fun PreferencesEntryPoint.InitialTarget.toNavTarget() = when (this) {
    is PreferencesEntryPoint.InitialTarget.Root -> PreferencesFlowNode.NavTarget.Root
    is PreferencesEntryPoint.InitialTarget.NotificationSettings -> PreferencesFlowNode.NavTarget.NotificationSettings
    is PreferencesEntryPoint.InitialTarget.LockScreenSettings -> PreferencesFlowNode.NavTarget.LockScreenSettings
    is PreferencesEntryPoint.InitialTarget.AdvancedSettings -> PreferencesFlowNode.NavTarget.AdvancedSettings
    is PreferencesEntryPoint.InitialTarget.About -> PreferencesFlowNode.NavTarget.About
    is PreferencesEntryPoint.InitialTarget.BlockedUsers -> PreferencesFlowNode.NavTarget.BlockedUsers
    is PreferencesEntryPoint.InitialTarget.SignOut -> PreferencesFlowNode.NavTarget.SignOut
    is PreferencesEntryPoint.InitialTarget.UserProfile -> PreferencesFlowNode.NavTarget.UserProfile(matrixUser = matrixUser)
    is PreferencesEntryPoint.InitialTarget.UserQrCode -> PreferencesFlowNode.NavTarget.UserQrCode(matrixUser = matrixUser)
    PreferencesEntryPoint.InitialTarget.NotificationTroubleshoot -> PreferencesFlowNode.NavTarget.TroubleshootNotifications
}
