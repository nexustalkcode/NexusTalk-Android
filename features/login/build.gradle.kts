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
    alias(libs.plugins.kotlin.serialization)
}

setupDependencyInjection()

android {
    // 先沿用 api 的 namespace，避免第一阶段放大 import / 包名 / 资源引用改动面。
    namespace = "io.element.android.features.login.impl"

    sourceSets.getByName("main").manifest.srcFile("impl/src/main/AndroidManifest.xml")

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
    implementation(projects.appconfig)
    implementation(project(":features:enterprise"))
    implementation(project(":features:rageshake"))
    implementation(projects.libraries.core)
    implementation(projects.libraries.androidutils)
    implementation(project(":libraries:featureflag"))
    implementation(project(":libraries:matrix"))
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.testtags)
    implementation(projects.libraries.uiStrings)
    implementation(project(":libraries:permissions"))
    implementation(project(":libraries:session-storage"))
    implementation(projects.libraries.qrcode)
    implementation(project(":libraries:oidc"))
    implementation(projects.libraries.uiUtils)
    implementation(project(":libraries:wellknown"))
    implementation(libs.androidx.browser)
    implementation(libs.androidx.webkit)
    implementation(libs.serialization.json)
    testImplementation(project(":features:enterprise"))
    testImplementation(project(":libraries:featureflag"))
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:oidc"))
    testImplementation(project(":libraries:permissions"))
    testImplementation(project(":libraries:session-storage"))
    testImplementation(project(":libraries:wellknown"))
    testImplementation(libs.androidx.camera.camera2)
    testImplementation(libs.androidx.camera.lifecycle)
}
