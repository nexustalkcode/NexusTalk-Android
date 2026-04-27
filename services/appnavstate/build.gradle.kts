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
    namespace = "io.element.android.services.appnavstate"

    sourceSets {
        getByName("main").java.setSrcDirs(
            listOf(
                // 第一阶段继续复用原子模块源码目录，优先收敛 Gradle 模块边界。
                "src/main/kotlin",
                "api/src/main/kotlin",
                "impl/src/main/kotlin",
                // 这里先沿用 analytics 试点的策略，把 test 模块里的 fake 并入主源码集，保持对外测试辅助可见性。
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
    // appnavstate 只使用 matrix 的 API 类型，继续收紧到 api 模块可以避免把 matrix 实现层重新卷入依赖环。
    implementation(project(":libraries:matrix:api"))

    implementation(libs.coroutines.core)
    implementation(libs.androidx.corektx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.startup)

    testImplementation(project(":libraries:matrix:test"))
}
