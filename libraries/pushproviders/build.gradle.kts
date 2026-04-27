import config.BuildTimeConfig
import extension.setupDependencyInjection

/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2026 HONOR.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:Suppress("UnstableApiUsage")

plugins {
    id("io.element.android-library")
    alias(libs.plugins.kotlin.serialization)
}

setupDependencyInjection()

android {
    namespace = "io.element.android.libraries.pushproviders"

    sourceSets {
        getByName("main").manifest.srcFile("src/main/AndroidManifest.xml")
        getByName("main").java.setSrcDirs(
            listOf(
                "src/main/kotlin",
                "api/src/main/kotlin",
                "firebase/src/main/kotlin",
                "unifiedpush/src/main/kotlin",
                "test/src/main/kotlin",
            )
        )
        getByName("main").res.setSrcDirs(
            listOf(
                "firebase/src/main/res",
                "unifiedpush/src/main/res",
            )
        )
        getByName("test").java.setSrcDirs(
            listOf(
                "src/test/kotlin",
                "firebase/src/test/kotlin",
                "unifiedpush/src/test/kotlin",
            )
        )
    }

    buildTypes {
        getByName("release") {
            consumerProguardFiles("firebase/consumer-proguard-rules.pro")
            resValue("string", "google_app_id", BuildTimeConfig.GOOGLE_APP_ID_RELEASE)
        }
        getByName("debug") {
            resValue("string", "google_app_id", BuildTimeConfig.GOOGLE_APP_ID_DEBUG)
        }
        register("nightly") {
            consumerProguardFiles("firebase/consumer-proguard-rules.pro")
            matchingFallbacks += listOf("release")
            resValue("string", "google_app_id", BuildTimeConfig.GOOGLE_APP_ID_NIGHTLY)
        }
    }
}

dependencies {
    implementation(libs.androidx.corektx)
    implementation(platform(libs.network.okhttp.bom))
    implementation(libs.network.okhttp.okhttp)
    implementation(platform(libs.network.retrofit.bom))
    implementation(libs.network.retrofit)
    implementation(libs.serialization.json)

    implementation(project(":features:enterprise"))
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.core)
    implementation(projects.libraries.di)
    implementation(project(":libraries:matrix:api"))
    implementation(projects.libraries.network)
    implementation(project(":libraries:pushstore"))
    // pushproviders 主源码仍然会实现通知排障测试接口，
    // 但这些接口只属于 troubleshoot 的 API 边界，不应该把整个 impl 一起拉进来，
    // 否则会形成 push -> pushproviders -> troubleshoot -> push 的编译环。
    implementation(project(":libraries:troubleshoot:api"))
    implementation(projects.libraries.uiStrings)
    implementation(project(":services:toolbox"))

    api(platform(libs.google.firebase.bom))
    api("com.google.firebase:firebase-messaging") {
        exclude(group = "com.google.firebase", module = "firebase-core")
        exclude(group = "com.google.firebase", module = "firebase-analytics")
    }
    api(libs.unifiedpush)

    testImplementation(libs.kotlinx.collections.immutable)
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:push"))
    testImplementation(project(":libraries:pushstore"))
    testImplementation(project(":libraries:session-storage"))
    testImplementation(project(":features:enterprise"))
    testImplementation(project(":services:toolbox"))
}
