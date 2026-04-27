package io.element.android.services.analyticsproviders.sentry

import io.element.android.services.analyticsproviders.BuildConfig as RootBuildConfig

object BuildConfig {
    @JvmField val SENTRY_DSN: String = RootBuildConfig.SENTRY_DSN
    @JvmField val SDK_SENTRY_DSN: String = RootBuildConfig.SDK_SENTRY_DSN
}
