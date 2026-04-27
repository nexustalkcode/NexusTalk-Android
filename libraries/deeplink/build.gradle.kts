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
    namespace = "io.element.android.libraries.deeplink"

    sourceSets {
        getByName("main").java.setSrcDirs(
            listOf(
                // 第一阶段先复用原子模块源码目录，优先收敛 Gradle 模块边界。
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
    implementation(project(":libraries:matrix"))
    implementation(projects.libraries.di)
    implementation(projects.libraries.core)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.uiStrings)
    implementation(project(":services:toolbox"))
    implementation(libs.androidx.corektx)

    testImplementation(project(":libraries:matrix:test"))
}
