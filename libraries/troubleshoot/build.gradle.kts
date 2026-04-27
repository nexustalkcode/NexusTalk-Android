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
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "io.element.android.libraries.troubleshoot"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        getByName("main").apply {
            java.setSrcDirs(
                listOf(
                    "src/main/kotlin",
                    "impl/src/main/kotlin",
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
                "impl/src/test/kotlin",
            )
        )
    }
}

setupDependencyInjection()

dependencies {
    // 根模块现在只承载 troubleshoot 的 impl 与装配逻辑，
    // 对外暴露的 entry point / test 契约继续通过单独的 api 子模块提供，
    // 这样 push 与 pushproviders 可以只依赖 api，避免再次回到 impl 形成依赖环。
    api(project(":libraries:troubleshoot:api"))
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.di)
    // matrix 已合并到根模块，这里直接依赖根模块，避免继续保留旧 api 坐标。
    implementation(project(":libraries:matrix"))
    implementation(projects.libraries.uiStrings)
    implementation(project(":libraries:push"))
    implementation(project(":services:analytics"))

    implementation(libs.androidx.corektx)
    implementation(libs.coroutines.core)

    testImplementation(project(":services:analytics"))
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:push"))
}
