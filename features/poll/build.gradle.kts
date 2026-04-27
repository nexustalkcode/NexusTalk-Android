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
    // API 源码显式引用的是 features.poll.api.R，根模块沿用 api namespace 保持资源包名稳定。
    namespace = "io.element.android.features.poll.api"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        getByName("main").java.setSrcDirs(
            listOf(
                "src/main/kotlin",
                "api/src/main/kotlin",
                "impl/src/main/kotlin",
                "test/src/main/kotlin",
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
    implementation(projects.libraries.uiStrings)
    implementation(project(":libraries:matrix"))
    implementation(projects.libraries.core)
    implementation(projects.libraries.matrixui)
    implementation(project(":services:analytics"))
    implementation(project(":libraries:textcomposer"))
    implementation(project(":libraries:dateformatter"))
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":services:analytics"))
    testImplementation(project(":features:messages"))
    testImplementation(project(":libraries:dateformatter"))
}
