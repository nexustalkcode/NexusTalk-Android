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

android {
    namespace = "io.element.android.libraries.pushstore"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
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
        getByName("test").java.setSrcDirs(
            listOf(
                "src/test/kotlin",
                "impl/src/test/kotlin",
            )
        )
        getByName("androidTest").java.setSrcDirs(
            listOf(
                "src/androidTest/kotlin",
                "impl/src/androidTest/kotlin",
            )
        )
    }
}

setupDependencyInjection()

dependencies {
    implementation(libs.coroutines.core)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.core)
    implementation(project(":libraries:matrix"))
    implementation(project(":libraries:preferences"))
    implementation(libs.androidx.corektx)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:preferences"))

    androidTestImplementation(libs.coroutines.test)
    androidTestImplementation(libs.test.core)
    androidTestImplementation(libs.test.junit)
    androidTestImplementation(libs.test.truth)
    androidTestImplementation(libs.test.runner)
}
