import extension.buildConfigFieldStr
import extension.readLocalProperty
import extension.setupDependencyInjection
import extension.testCommonDependencies
import extension.ValidateElementCallEmbeddedDistTask

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

setupDependencyInjection()

android {
    // 先沿用 api 的 namespace，避免第一阶段放大 import / 包名 / 资源引用改动面。
    namespace = "io.element.android.features.call.impl"

    sourceSets.getByName("main").manifest.srcFile("impl/src/main/AndroidManifest.xml")

    buildFeatures {
        buildConfig = true
    }
    
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    
    
    defaultConfig {
        buildConfigFieldStr(
            name = "SENTRY_DSN",
            value = System.getenv("ELEMENT_CALL_SENTRY_DSN")
                ?: readLocalProperty("features.call.sentry.dsn")
                ?: ""
        )
        buildConfigFieldStr(
            name = "POSTHOG_USER_ID",
            value = System.getenv("ELEMENT_CALL_POSTHOG_USER_ID")
                ?: readLocalProperty("features.call.posthog.userid")
                ?: ""
        )
        buildConfigFieldStr(
            name = "POSTHOG_API_HOST",
            value = System.getenv("ELEMENT_CALL_POSTHOG_API_HOST")
                ?: readLocalProperty("features.call.posthog.api.host")
                ?: ""
        )
        buildConfigFieldStr(
            name = "POSTHOG_API_KEY",
            value = System.getenv("ELEMENT_CALL_POSTHOG_API_KEY")
                ?: readLocalProperty("features.call.posthog.api.key")
                ?: ""
        )
        buildConfigFieldStr(
            name = "RAGESHAKE_URL",
            value = System.getenv("ELEMENT_CALL_RAGESHAKE_URL")
                ?: readLocalProperty("features.call.regeshake.url")
                ?: ""
        )
    }

    sourceSets {
        getByName("main").java.setSrcDirs(
            listOf(
                "src/main/kotlin",
                "api/src/main/kotlin",
                "impl/src/main/kotlin",
                "test/src/main/kotlin",
            )
        )

        getByName("main").res.setSrcDirs(
            listOf(
                "impl/src/main/res",
            )
        )

        getByName("main") {
            // Element Call 是一个嵌入式 Web 应用，运行时 WebViewAssetLoader 会从 APK assets 中读取 element-call/index.html。
            // 这里把 Gradle 生成目录注册为 assets 源，确保下方复制任务产出的文件会被一起打进最终应用包。
            assets.srcDir(layout.buildDirectory.dir("generated/element-call-assets"))
        }

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
    implementation(projects.libraries.core)
    implementation(project(":libraries:matrix"))
    implementation(projects.appconfig)
    implementation(project(":features:enterprise"))
    implementation(projects.libraries.androidutils)
    implementation(project(":libraries:audio"))
    implementation(projects.libraries.designsystem)
    implementation(project(":libraries:featureflag"))
    implementation(project(":libraries:matrixmedia"))
    implementation(projects.libraries.network)
    implementation(project(":libraries:preferences"))
    implementation(project(":libraries:push"))
    implementation(projects.libraries.uiStrings)
    implementation(project(":services:analytics"))
    implementation(project(":services:appnavstate"))
    implementation(project(":services:toolbox"))
    implementation(libs.androidx.webkit)
    implementation(libs.coil.compose)
    implementation(libs.network.retrofit)
    implementation(libs.serialization.json)
    testImplementation(project(":libraries:featureflag"))
    testImplementation(project(":libraries:preferences"))
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:matrixmedia"))
    testImplementation(project(":libraries:push"))
    testImplementation(project(":services:analytics"))
    testImplementation(project(":services:appnavstate"))
    testImplementation(project(":services:toolbox"))
}

val elementCallDistPath = rootProject.projectDir.resolve("element-call/dist").canonicalFile.absolutePath
val elementCallDistIndexPath = rootProject.projectDir.resolve("element-call/dist/index.html").canonicalFile.absolutePath

tasks.register<ValidateElementCallEmbeddedDistTask>("validateElementCallEmbeddedDist") {
    indexHtmlPath.set(elementCallDistIndexPath)
}

val copyElementCallEmbeddedAssets by tasks.registering(Copy::class) {
    // 先校验 dist/index.html 是否存在，避免生成一个缺首页的 assets 目录，导致视频通话运行时才白屏或 404。
    dependsOn("validateElementCallEmbeddedDist")
    from(elementCallDistPath)
    into(layout.buildDirectory.dir("generated/element-call-assets/element-call"))
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named("preBuild").configure {
    // Android assets 合并前必须先准备好嵌入式 Element Call 文件，否则 APK 中会缺少 element-call/index.html。
    dependsOn(copyElementCallEmbeddedAssets)
}
