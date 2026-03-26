/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securityandprivacy.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.securityandprivacy.api.SecurityAndPrivacyEntryPoint
import io.element.android.libraries.architecture.createNode
import io.element.android.libraries.di.RoomScope

/**
 * 安全与隐私功能的默认实现
 *
 * 实现 SecurityAndPrivacyEntryPoint 接口，提供创建安全与隐私设置节点的功能。
 * 使用 @ContributesBinding 注解将其绑定到 RoomScope，以便依赖注入系统使用。
 *
 * @see SecurityAndPrivacyEntryPoint 安全与隐私入口点接口
 */
@ContributesBinding(RoomScope::class)
class DefaultSecurityAndPrivacyEntryPoint : SecurityAndPrivacyEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: SecurityAndPrivacyEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<SecurityAndPrivacyFlowNode>(buildContext, listOf(callback))
    }
}
