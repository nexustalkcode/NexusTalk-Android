/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.invite.api.declineandblock

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.element.android.features.invite.api.InviteData
import io.element.android.libraries.architecture.FeatureEntryPoint

/**
 * 拒绝并封禁入口点接口
 *
 * 定义了拒绝邀请并可选封禁用户功能的入口点。
 * 继承自 FeatureEntryPoint，用于在应用中导航到拒绝并封禁界面。
 *
 * @property parentNode 父节点
 * @property buildContext 构建上下文
 * @property inviteData 邀请数据
 * @return Node 创建的节点
 */
fun interface DeclineInviteAndBlockEntryPoint : FeatureEntryPoint {
    /**
     * 创建拒绝并封禁节点
     *
     * @param parentNode 父节点
     * @param buildContext 构建上下文
     * @param inviteData 邀请数据
     * @return 创建的节点
     */
    fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        inviteData: InviteData,
    ): Node
}
