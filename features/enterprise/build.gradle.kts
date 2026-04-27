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

setupDependencyInjection()

android {
    // impl-foss 源码实际位于 enterprise.impl 包下，沿用 impl namespace 以保持包结构稳定。
    namespace = "io.element.android.features.enterprise.impl"

    sourceSets {
        getByName("main").java.setSrcDirs(
            listOf(
                "src/main/kotlin",
                "api/src/main/kotlin",
                "impl-foss/src/main/kotlin",
                "test/src/main/kotlin",
            )
        )
        getByName("test").java.setSrcDirs(
            listOf(
                "src/test/kotlin",
                "impl-foss/src/test/kotlin",
            )
        )
    }
}

dependencies {
    implementation(projects.libraries.compound)
    implementation(projects.libraries.architecture)
    implementation(project(":libraries:matrix"))

    testImplementation(project(":libraries:matrix:test"))
}
