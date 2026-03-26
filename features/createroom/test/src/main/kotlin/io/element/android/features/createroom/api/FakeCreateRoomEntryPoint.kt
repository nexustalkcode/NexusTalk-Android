/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createroom.api

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.element.android.tests.testutils.lambda.lambdaError

/**
 * 虚假的创建房间入口点实现
 *
 * 用于测试环境中的 Mock 实现。
 * 任何调用 createNode 的操作都会抛出错误，用于验证是否正确绑定了真实实现。
 */
class FakeCreateRoomEntryPoint : CreateRoomEntryPoint {
    /**
     * 创建节点（未实现）
     *
     * 此方法在测试中不应该被调用，如果被调用会抛出错误
     */
    override fun createNode(
        isSpace: Boolean,
        parentNode: Node,
        buildContext: BuildContext,
        callback: CreateRoomEntryPoint.Callback,
        addPeopleCallback: CreateRoomEntryPoint.AddPeopleCallback?,
    ): Node = lambdaError()
}
