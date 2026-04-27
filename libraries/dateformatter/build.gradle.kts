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
    namespace = "io.element.android.libraries.dateformatter"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    kotlin {
        compilerOptions {
            optIn = listOf(
                "kotlin.time.ExperimentalTime"
            )
        }
    }

    sourceSets {
        getByName("main").apply {
            java.setSrcDirs(
                listOf(
                    "src/main/kotlin",
                    "api/src/main/kotlin",
                    "impl/src/main/kotlin",
                    "test/src/main/kotlin",
                )
            )
            res.setSrcDirs(
                listOf(
                    "src/main/res",
                    "impl/src/main/res",
                )
            )
        }
        getByName("test").java.setSrcDirs(
            listOf(
                "src/test/kotlin",
                "api/src/test/kotlin",
                "impl/src/test/kotlin",
            )
        )
    }
}

setupDependencyInjection()

dependencies {
    implementation(projects.libraries.core)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.di)
    implementation(projects.libraries.uiStrings)
    implementation(project(":services:toolbox"))

    implementation(libs.datetime)

    testImplementation(project(":services:toolbox"))
}
