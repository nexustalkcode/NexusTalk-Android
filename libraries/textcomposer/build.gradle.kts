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
    id("kotlin-parcelize")
}

android {
    namespace = "io.element.android.libraries.textcomposer"

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    sourceSets {
        getByName("main").java.setSrcDirs(
            listOf(
                "src/main/kotlin",
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

setupDependencyInjection()

dependencies {
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.uiStrings)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.core)
    implementation(project(":libraries:matrix"))
    implementation(projects.libraries.matrixui)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.testtags)
    implementation(projects.libraries.uiUtils)
    implementation(project(":libraries:recentemojis"))
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.matrix.emojibase.bindings)

    releaseApi(libs.matrix.richtexteditor)
    releaseApi(libs.matrix.richtexteditor.compose)
    if (file("${rootDir.path}/libraries/textcomposer/lib/library-compose.aar").exists()) {
        println("\nNote: Using local binaries of the Rich Text Editor.\n")
        debugApi(project(":libraries:textcomposer:lib"))
    } else {
        debugApi(libs.matrix.richtexteditor)
        debugApi(libs.matrix.richtexteditor.compose)
    }

    testImplementation(project(":libraries:matrix:test"))
}
