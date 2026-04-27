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
    namespace = "io.element.android.libraries.mediaplayer"

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
    }
}

setupDependencyInjection()

dependencies {
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.coroutines.core)

    implementation(project(":libraries:audio"))
    implementation(projects.libraries.core)
    implementation(projects.libraries.di)
    implementation(project(":libraries:matrix"))

    testImplementation(project(":libraries:audio"))
    testImplementation(libs.coroutines.core)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.test.truth)
}
