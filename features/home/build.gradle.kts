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
    namespace = "io.element.android.features.home.impl"

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
    implementation(projects.appconfig)
    implementation(projects.libraries.core)
    implementation(projects.appicon.element)
    implementation(projects.libraries.androidutils)
    implementation(project(":libraries:featureflag"))
    implementation(projects.libraries.matrixui)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.testtags)
    implementation(projects.libraries.uiCommon)
    implementation(projects.libraries.uiStrings)
    implementation(project(":libraries:dateformatter"))
    implementation(project(":libraries:eventformatter"))
    implementation(project(":libraries:indicator"))
    implementation(project(":libraries:deeplink"))
    implementation(project(":libraries:fullscreenintent"))
    implementation(project(":libraries:permissions"))
    implementation(project(":libraries:preferences"))
    implementation(project(":libraries:push"))
    implementation(project(":features:announcement"))
    implementation(project(":features:invite"))
    implementation(project(":features:networkmonitor"))
    implementation(project(":features:logout"))
    implementation(project(":features:leaveroom"))
    implementation(project(":features:rageshake"))
    implementation(project(":services:analytics"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.haze)
    implementation(libs.haze.materials)
    implementation(project(":features:reportroom"))
    implementation(project(":features:rolesandpermissions"))
    implementation(projects.libraries.previewutils)
    testImplementation(project(":features:announcement"))
    testImplementation(project(":features:invite"))
    testImplementation(project(":features:logout"))
    testImplementation(project(":features:networkmonitor"))
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:featureflag"))
    testImplementation(project(":libraries:dateformatter"))
    testImplementation(project(":libraries:eventformatter"))
    testImplementation(project(":libraries:indicator"))
    testImplementation(project(":libraries:permissions"))
    testImplementation(project(":libraries:preferences"))
    testImplementation(project(":libraries:session-storage"))
    testImplementation(project(":libraries:push"))
    testImplementation(project(":services:analytics"))
    testImplementation(project(":services:toolbox"))
}
