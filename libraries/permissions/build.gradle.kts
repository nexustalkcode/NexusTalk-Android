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
    namespace = "io.element.android.libraries.permissions"

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
                    "api/src/main/kotlin",
                    "impl/src/main/kotlin",
                    "noop/src/main/kotlin",
                    "test/src/main/kotlin",
                )
            )
            res.setSrcDirs(
                listOf(
                    "src/main/res",
                    "api/src/main/res",
                    "impl/src/main/res",
                )
            )
        }
        getByName("test").java.setSrcDirs(
            listOf(
                "src/test/kotlin",
                "impl/src/test/kotlin",
                "noop/src/test/kotlin",
            )
        )
    }
}

setupDependencyInjection()

dependencies {
    implementation(libs.accompanist.permission)
    implementation(libs.androidx.datastore.preferences)

    implementation(projects.libraries.core)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.architecture)
    implementation(project(":libraries:matrix"))
    implementation(projects.libraries.matrixui)
    implementation(project(":libraries:push"))
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.uiStrings)
    implementation(project(":libraries:preferences"))
    // permissions 的通知权限排障测试直接实现了 troubleshoot API 层里的测试契约，
    // 这里显式补回 api 依赖，避免 architecture 中旧拷贝删除后出现未解析引用。
    implementation(project(":libraries:troubleshoot:api"))
    implementation(project(":services:toolbox"))

    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:preferences"))
    testImplementation(project(":libraries:troubleshoot:api"))
    testImplementation(project(":services:toolbox"))
}
