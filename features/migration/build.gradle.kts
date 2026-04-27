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
}

setupDependencyInjection()

android {
    // 先沿用 api 的 namespace，避免第一阶段放大 import / 包名 / 资源引用改动面。
    namespace = "io.element.android.features.migration.impl"

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

dependencies {
    implementation(projects.libraries.architecture)
    implementation(project(":features:announcement"))
    implementation(projects.libraries.androidutils)
    implementation(project(":libraries:preferences"))
    implementation(libs.androidx.datastore.preferences)
    implementation(project(":features:rageshake"))
    implementation(projects.libraries.designsystem)
    implementation(project(":libraries:matrix"))
    implementation(project(":libraries:session-storage"))
    implementation(projects.libraries.uiStrings)
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:session-storage"))
    testImplementation(project(":libraries:preferences"))
    testImplementation(project(":features:announcement"))
    testImplementation(project(":features:rageshake"))
}
