package io.element.android.services.analyticsproviders.posthog

import io.element.android.services.analyticsproviders.BuildConfig as RootBuildConfig

object BuildConfig {
    @JvmField val POSTHOG_HOST: String = RootBuildConfig.POSTHOG_HOST
    @JvmField val POSTHOG_APIKEY: String = RootBuildConfig.POSTHOG_APIKEY
}
