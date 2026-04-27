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
    // 先沿用 api 的 namespace，避免第一阶段放大 import / 包名 / 资源引用改动面。
    namespace = "io.element.android.features.share.impl"

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
    implementation(project(":libraries:matrix"))
    implementation(projects.appconfig)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.core)
    implementation(project(":libraries:featureflag"))
    implementation(projects.libraries.matrixui)
    implementation(projects.libraries.designsystem)
    implementation(project(":libraries:mediaupload"))
    implementation(project(":libraries:roomselect"))
    implementation(projects.libraries.uiStrings)
    implementation(projects.libraries.testtags)
    implementation(project(":services:appnavstate"))
    api(libs.statemachine)
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:mediaupload"))
    testImplementation(project(":libraries:preferences"))
    testImplementation(project(":libraries:roomselect"))
    testImplementation(project(":services:appnavstate"))
}
