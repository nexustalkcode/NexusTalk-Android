/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.services.analyticsproviders.test

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent
import im.vector.app.features.analytics.itf.VectorAnalyticsScreen
import im.vector.app.features.analytics.plan.SuperProperties
import im.vector.app.features.analytics.plan.UserProperties
import io.element.android.services.analyticsproviders.api.AnalyticsProvider
import io.element.android.services.analyticsproviders.api.AnalyticsTransaction

private fun missingFakeAnalyticsProviderLambda(methodName: String): Nothing {
    error("FakeAnalyticsProvider.$methodName should be provided in tests")
}

class FakeAnalyticsProvider(
    override val name: String = "FakeAnalyticsProvider",
    // 测试辅助 fake 已并入单模块源码边界，默认行为不能再隐式依赖 tests:testutils 的 lambdaError。
    // 这里直接抛出清晰异常，确保调用方在真正需要断言行为时显式注入对应 lambda。
    private val initLambda: () -> Unit = { missingFakeAnalyticsProviderLambda("init") },
    private val stopLambda: () -> Unit = { missingFakeAnalyticsProviderLambda("stop") },
    private val captureLambda: (VectorAnalyticsEvent) -> Unit = { missingFakeAnalyticsProviderLambda("capture") },
    private val screenLambda: (VectorAnalyticsScreen) -> Unit = { missingFakeAnalyticsProviderLambda("screen") },
    private val updateUserPropertiesLambda: (UserProperties) -> Unit = { missingFakeAnalyticsProviderLambda("updateUserProperties") },
    private val updateSuperPropertiesLambda: (SuperProperties) -> Unit = { missingFakeAnalyticsProviderLambda("updateSuperProperties") },
    private val trackErrorLambda: (Throwable) -> Unit = { missingFakeAnalyticsProviderLambda("trackError") },
) : AnalyticsProvider {
    override fun init() = initLambda()

    override fun stop() = stopLambda()

    override fun capture(event: VectorAnalyticsEvent) = captureLambda(event)

    override fun screen(screen: VectorAnalyticsScreen) = screenLambda(screen)

    override fun updateUserProperties(userProperties: UserProperties) = updateUserPropertiesLambda(userProperties)

    override fun trackError(throwable: Throwable) = trackErrorLambda(throwable)

    override fun updateSuperProperties(updatedProperties: SuperProperties) = updateSuperPropertiesLambda(updatedProperties)

    override fun startTransaction(name: String, operation: String?, description: String?): AnalyticsTransaction? = null
}
