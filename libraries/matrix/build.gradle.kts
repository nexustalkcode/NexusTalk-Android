import config.BuildTimeConfig
import extension.buildConfigFieldStr
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
    alias(libs.plugins.kotlin.serialization)
}

android {
    // 根模块现在只承载 matrix 的实现层；
    // API 契约继续由 :libraries:matrix:api 提供，并通过依赖透传给外部使用者。
    // 这样可以避免根模块和 api 子模块同时把同一批 API 类打进最终 dex。
    namespace = "io.element.android.libraries.matrix"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigFieldStr(
            name = "CLIENT_URI",
            value = BuildTimeConfig.URL_WEBSITE ?: "https://app.nexustalk.space"
        )
        buildConfigFieldStr(
            name = "LOGO_URI",
            value = BuildTimeConfig.URL_LOGO ?: "https://app.nexustalk.space/mobile-icon.png"
        )
        buildConfigFieldStr(
            name = "TOS_URI",
            value = BuildTimeConfig.URL_ACCEPTABLE_USE ?: "https://app.nexustalk.space/acceptable-use-policy-terms"
        )
        buildConfigFieldStr(
            name = "POLICY_URI",
            value = BuildTimeConfig.URL_POLICY ?: "https://app.nexustalk.space/privacy"
        )
    }

    sourceSets {
        getByName("main").manifest.srcFile("impl/src/main/AndroidManifest.xml")
        getByName("main").java.setSrcDirs(
            listOf(
                "src/main/kotlin",
                "impl/src/main/kotlin",
                // 先把原 test 模块里的 fake 并入主源码集，保持其他模块未来切到 :libraries:matrix 时仍可复用这些辅助类型。
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
    releaseImplementation(libs.matrix.sdk)
    if (file("${rootDir.path}/libraries/rustsdk/matrix-rust-sdk.aar").exists()) {
        println("\nNote: Using local binary of the Rust SDK.\n")
        debugImplementation(projects.libraries.rustsdk)
    } else {
        debugImplementation(libs.matrix.sdk)
    }

    api(project(":libraries:matrix:api"))
    api(projects.libraries.architecture)

    implementation(projects.appconfig)
    implementation(projects.libraries.androidutils)
    api(projects.libraries.core)
    implementation(projects.libraries.di)
    implementation(project(":libraries:featureflag"))
    implementation(projects.libraries.network)
    implementation(project(":libraries:workmanager"))
    implementation(project(":services:analytics"))
    // 前台态契约已下沉到 core，matrix 不再需要为了这个 watcher 反向依赖整个 appnavstate service。
    implementation(project(":services:toolbox"))
    implementation(project(":libraries:session-storage"))
    implementation(libs.matrix.analytics.events)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.test)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.serialization.json)
    implementation(libs.kotlinx.collections.immutable)
    implementation("net.java.dev.jna:jna:5.18.1@aar")

    testImplementation(project(":libraries:featureflag"))
    testImplementation(projects.libraries.previewutils)
    testImplementation(project(":libraries:session-storage"))
    testImplementation(project(":libraries:workmanager"))
    testImplementation(project(":services:analytics"))
    testImplementation(project(":services:toolbox"))
}
