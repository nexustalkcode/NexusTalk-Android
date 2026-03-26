/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.logout.test

import io.element.android.features.logout.api.LogoutUseCase
import io.element.android.tests.testutils.lambda.lambdaError
import io.element.android.tests.testutils.simulateLongTask

/**
 * 虚假的退出登录用例（用于测试）
 *
 * 实现了 LogoutUseCase 接口，
 * 用于测试环境中，当需要模拟退出登录操作时使用。
 * 通过设置 logoutLambda 来控制退出登录的行为。
 *
 * @property logoutLambda 自定义的退出登录逻辑Lambda，默认会抛出错误
 */
class FakeLogoutUseCase(
    var logoutLambda: (Boolean) -> Unit = { lambdaError() }
) : LogoutUseCase {
    /**
     * 退出所有已登录的用户会话（测试用）
     *
     * @param ignoreSdkError 是否忽略 SDK 错误
     */
    override suspend fun logoutAll(ignoreSdkError: Boolean) = simulateLongTask {
        logoutLambda(ignoreSdkError)
    }
}
