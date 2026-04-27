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
    // API 源码里显式引用的是 features.rageshake.api.R，根模块继续沿用 api namespace 以保持资源包名稳定。
    namespace = "io.element.android.features.rageshake.api"

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
    implementation(projects.libraries.androidutils)
    implementation(project(":libraries:matrix"))
    implementation(projects.libraries.uiStrings)
    implementation(projects.appconfig)
    implementation(project(":features:enterprise"))
    implementation(project(":features:viewfolder"))
    implementation(project(":services:toolbox"))
    implementation(projects.libraries.core)
    implementation(projects.libraries.network)
    implementation(project(":libraries:preferences"))
    implementation(project(":libraries:session-storage"))
    api(libs.squareup.seismic)
    implementation(libs.androidx.datastore.preferences)
    implementation(platform(libs.network.okhttp.bom))
    implementation(libs.network.okhttp.okhttp)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    testImplementation(project(":features:enterprise"))
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:session-storage"))
    testImplementation(project(":features:viewfolder"))
    testImplementation(project(":libraries:preferences"))
    testImplementation(project(":services:toolbox"))
    testImplementation(libs.network.mockwebserver)
}
