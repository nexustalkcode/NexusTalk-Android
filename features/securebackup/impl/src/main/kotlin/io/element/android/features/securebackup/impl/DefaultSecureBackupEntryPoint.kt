/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.securebackup.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import io.element.android.features.securebackup.api.SecureBackupEntryPoint
import io.element.android.libraries.architecture.createNode

/**
 * 安全备份功能默认入口点实现
 *
 * 实现了 [SecureBackupEntryPoint] 接口，作为安全备份功能的入口点。
 * 负责创建和管理安全备份流程的根节点 [SecureBackupFlowNode]。
 */
@ContributesBinding(AppScope::class)
class DefaultSecureBackupEntryPoint : SecureBackupEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        params: SecureBackupEntryPoint.Params,
        callback: SecureBackupEntryPoint.Callback,
    ): Node {
        return parentNode.createNode<SecureBackupFlowNode>(
            buildContext = buildContext,
            plugins = listOf(params, callback)
        )
    }
}
