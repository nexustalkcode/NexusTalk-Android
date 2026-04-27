/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.pushproviders.firebase.troubleshoot

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import io.element.android.libraries.troubleshoot.api.test.NotificationTroubleshootNavigator
import io.element.android.libraries.troubleshoot.api.test.NotificationTroubleshootTest
import io.element.android.libraries.troubleshoot.api.test.NotificationTroubleshootTestState
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope

/**
 * 这些 helper 只服务 firebase push provider 自己的排障测试，
 * 放在本模块 test 源集里可以去掉对 `libraries:troubleshoot:test` 的依赖。
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
