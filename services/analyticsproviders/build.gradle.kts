import config.BuildTimeConfig
import extension.buildConfigFieldStr
import extension.readLocalProperty
import extension.setupDependencyInjection

/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

plugins {
    id("io.element.android-library")
}

setupDependencyInjection()

android {
    namespace = "io.element.android.services.analyticsproviders"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigFieldStr(
            name = "POSTHOG_HOST",
            value = BuildTimeConfig.SERVICES_POSTHOG_HOST.takeIf { isEnterpriseBuild } ?: "",
        )
        buildConfigFieldStr(
            name = "POSTHOG_APIKEY",
            value = BuildTimeConfig.SERVICES_POSTHOG_APIKEY.takeIf { isEnterpriseBuild } ?: "",
        )
        buildConfigFieldStr(
            name = "SENTRY_DSN",
            value = if (isEnterpriseBuild) {
                BuildTimeConfig.SERVICES_SENTRY_DSN
            } else {
                System.getenv("ELEMENT_ANDROID_SENTRY_DSN")
                    ?: readLocalProperty("services.analyticsproviders.sentry.dsn")
            } ?: "",
        )
        buildConfigFieldStr(
            name = "SDK_SENTRY_DSN",
            value = if (isEnterpriseBuild) {
                BuildTimeConfig.SERVICES_SENTRY_DSN_RUST
            } else {
                System.getenv("ELEMENT_SDK_SENTRY_DSN")
                    ?: readLocalProperty("services.analyticsproviders.sdk.sentry.dsn")
            } ?: "",
        )
    }

    sourceSets {
        getByName("main").manifest.srcFile("src/main/AndroidManifest.xml")
        getByName("main").java.setSrcDirs(
            listOf(
                "src/main/kotlin",
                "api/src/main/kotlin",
                "posthog/src/main/kotlin",
                "sentry/src/main/kotlin",
                "test/src/main/kotlin",
            )
        )
        getByName("test").java.setSrcDirs(
            listOf(
                "src/test/kotlin",
                "posthog/src/test/kotlin",
                "sentry/src/test/kotlin",
            )
        )
    }
}

dependencies {
    api(libs.matrix.analytics.events)

    implementation(libs.posthog) {
        exclude("com.android.support", "support-annotations")
    }
    implementation(libs.sentry)

    implementation(projects.libraries.core)
    implementation(projects.libraries.di)
    implementation(project(":libraries:matrix:api"))
    implementation(project(":services:appnavstate"))

    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":services:appnavstate"))
}
