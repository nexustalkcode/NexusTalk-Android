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
    namespace = "io.element.android.features.createroom.impl"

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
    implementation(project(":libraries:matrix"))
    implementation(projects.libraries.core)
    implementation(projects.libraries.matrixui)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.uiStrings)
    implementation(projects.libraries.androidutils)
    implementation(project(":libraries:deeplink"))
    implementation(project(":libraries:mediapickers"))
    implementation(project(":libraries:mediaupload"))
    implementation(project(":libraries:permissions"))
    implementation(projects.libraries.previewutils)
    implementation(project(":libraries:usersearch"))
    implementation(project(":services:analytics"))
    implementation(libs.coil.compose)
    implementation(project(":libraries:featureflag"))
    implementation(project(":features:invitepeople"))
    testImplementation(project(":services:analytics"))
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:mediapickers"))
    testImplementation(project(":libraries:mediaupload"))
    testImplementation(project(":libraries:permissions"))
    testImplementation(project(":libraries:usersearch"))
    testImplementation(project(":features:startchat"))
    testImplementation(project(":libraries:featureflag"))
}
