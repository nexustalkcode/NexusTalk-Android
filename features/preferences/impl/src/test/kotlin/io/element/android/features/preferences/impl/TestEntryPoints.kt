/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl

import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import io.element.android.libraries.troubleshoot.api.NotificationTroubleShootEntryPoint
import io.element.android.libraries.troubleshoot.api.PushHistoryEntryPoint

/**
 * 这两个 fake 只被 preferences 自己的单测使用，
 * 直接下沉到当前模块可以去掉对 `libraries:troubleshoot:test` 的额外测试依赖。
 */
class FakeNotificationTroubleShootEntryPoint : NotificationTroubleShootEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: NotificationTroubleShootEntryPoint.Callback,
    ): Node = error("createNode should be provided in tests")
}

class FakePushHistoryEntryPoint : PushHistoryEntryPoint {
    override fun createNode(
        parentNode: Node,
        buildContext: BuildContext,
        callback: PushHistoryEntryPoint.Callback,
    ): Node = error("createNode should be provided in tests")
}
