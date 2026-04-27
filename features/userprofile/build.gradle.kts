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

setupDependencyInjection()

android {
    // shared 源码里显式引用的是 shared.R，这里沿用 shared 的 namespace，避免资源导入全面改写。
    namespace = "io.element.android.features.userprofile.shared"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        getByName("main").java.setSrcDirs(
            listOf(
                "src/main/kotlin",
                "api/src/main/kotlin",
                "impl/src/main/kotlin",
                "shared/src/main/kotlin",
            )
        )
        getByName("main").res.setSrcDirs(
            listOf(
                "shared/src/main/res",
            )
        )
        getByName("test").java.setSrcDirs(
            listOf(
                "src/test/kotlin",
                "impl/src/test/kotlin",
                "shared/src/test/kotlin",
            )
        )
    }
}

dependencies {
    implementation(projects.libraries.architecture)
    // UserProfile shared 里的分享链接归一化逻辑会读取 Matrix permalink 域名配置。
    implementation(projects.appconfig)
    implementation(projects.libraries.core)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.uiStrings)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.matrixui)
    implementation(projects.libraries.testtags)
    implementation(project(":libraries:matrix"))
    implementation(project(":libraries:mediaviewer"))
    implementation(project(":libraries:featureflag"))
    implementation(project(":libraries:permissions"))
    implementation(project(":libraries:preferences"))
    implementation(project(":features:call"))
    implementation(project(":features:enterprise"))
    implementation(project(":features:startchat"))
    implementation(project(":features:verifysession"))
    implementation(project(":services:analytics"))
    implementation(project(":services:apperror"))
    implementation(libs.coil.compose)

    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:mediaviewer"))
    testImplementation(project(":libraries:featureflag"))
    testImplementation(project(":features:call"))
    testImplementation(project(":features:enterprise"))
    testImplementation(project(":features:startchat"))
    testImplementation(project(":features:verifysession"))
}
