import extension.setupDependencyInjection
import extension.testCommonDependencies

/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

plugins {
    id("io.element.android-compose-library")
    id("kotlin-parcelize")
}

setupDependencyInjection()

android {
    // API 源码里显式引用的是 features.analytics.api.R，根模块继续沿用 api namespace 以保持资源包名稳定。
    namespace = "io.element.android.features.analytics.api"

    sourceSets {
        getByName("main").java.setSrcDirs(
            listOf(
                "src/main/kotlin",
                "api/src/main/kotlin",
                "impl/src/main/kotlin",
            )
        )

        getByName("main").res.setSrcDirs(
            listOf(
                "api/src/main/res",
                "impl/src/main/res",
            )
        )

        getByName("test").java.setSrcDirs(
            listOf(
                "src/test/kotlin",
                "impl/src/test/kotlin",
            )
        )
    }
}

dependencies {
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.uiStrings)
    implementation(projects.libraries.core)
    api(project(":services:analytics"))
    implementation(projects.appconfig)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.browser)
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":services:analytics"))
}
