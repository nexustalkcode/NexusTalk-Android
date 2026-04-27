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
    namespace = "io.element.android.services.toolbox"

    sourceSets {
        getByName("main").java.setSrcDirs(
            listOf(
                // 第一阶段先复用原子模块源码目录，优先收敛 Gradle 模块边界。
                "src/main/kotlin",
                "api/src/main/kotlin",
                "impl/src/main/kotlin",
                // 与前两个 service 试点保持一致，暂时将 test fake 并入主源码集，保证跨模块测试辅助不失效。
                "test/src/main/kotlin",
            )
        )
    }
}

setupDependencyInjection()

dependencies {
    implementation(projects.libraries.di)
    implementation(libs.androidx.corektx)
}
