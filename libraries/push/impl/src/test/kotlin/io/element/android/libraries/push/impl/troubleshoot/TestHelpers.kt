/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.push.impl.troubleshoot

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import io.element.android.libraries.troubleshoot.api.test.NotificationTroubleshootNavigator
import io.element.android.libraries.troubleshoot.api.test.NotificationTroubleshootTest
import io.element.android.libraries.troubleshoot.api.test.NotificationTroubleshootTestState
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope

/**
 * 这些 helper 只服务于 push 模块自己的通知排障单测，
 * 这样单测就不需要再回头依赖 `libraries:troubleshoot`，从而拆掉项目级循环依赖。
 */
class FakeNotificationTroubleshootNavigator(
    private val openIgnoredUsersResult: () -> Unit = {
        error("openIgnoredUsersResult should be provided in tests")
    },
) : NotificationTroubleshootNavigator {
    override fun navigateToBlockedUsers() = openIgnoredUsersResult()
}

context(testScope: TestScope)
suspend fun NotificationTroubleshootTest.runAndTestState(
    validate: suspend TurbineTestContext<NotificationTroubleshootTestState>.() -> Unit,
) {
    testScope.backgroundScope.launch {
        run(this)
    }
    state.test(validate = validate)
}
