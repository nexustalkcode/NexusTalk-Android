import extension.setupDependencyInjection

/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

plugins {
    id("io.element.android-compose-library")
}

android {
    namespace = "io.element.android.libraries.fullscreenintent"

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

setupDependencyInjection()

dependencies {
    implementation(projects.libraries.core)
    implementation(projects.libraries.architecture)
    implementation(project(":libraries:permissions"))
    implementation(project(":libraries:permissions"))
    implementation(project(":libraries:preferences"))
    implementation(project(":services:toolbox"))
    implementation(libs.androidx.datastore.preferences)

    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:permissions"))
    testImplementation(project(":libraries:preferences"))
    testImplementation(projects.libraries.testtags)
    testImplementation(project(":services:toolbox"))
}
