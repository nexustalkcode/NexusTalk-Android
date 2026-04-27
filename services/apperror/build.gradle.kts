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
    namespace = "io.element.android.services.apperror"

    sourceSets {
        getByName("main").java.setSrcDirs(
            listOf(
                // 第一阶段先复用原子模块源码目录，优先收敛 Gradle 模块边界。
                "src/main/kotlin",
                "api/src/main/kotlin",
                "impl/src/main/kotlin",
                // 先沿用前面 service 试点的策略，把 test fake 并入主源码集，确保跨模块测试辅助不中断。
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
    implementation(projects.libraries.core)
    implementation(projects.libraries.di)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.uiStrings)
    implementation(project(":services:toolbox"))

    implementation(libs.coroutines.core)
    implementation(libs.androidx.corektx)

    testImplementation(project(":services:toolbox"))
}
