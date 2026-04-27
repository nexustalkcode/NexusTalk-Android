import config.BuildTimeConfig
import extension.buildConfigFieldStr
import extension.readLocalProperty
import extension.testCommonDependencies
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
    // API 源码显式引用的是 features.location.api.BuildConfig / R，根模块沿用 api namespace 保持包名稳定。
    namespace = "io.element.android.features.location.api"

    sourceSets.getByName("main").manifest.srcFile("impl/src/main/AndroidManifest.xml")

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigFieldStr(
            name = "MAPTILER_BASE_URL",
            value = BuildTimeConfig.SERVICES_MAPTILER_BASE_URL ?: "https://api.maptiler.com/maps"
        )
        buildConfigFieldStr(
            name = "MAPTILER_API_KEY",
            value = if (isEnterpriseBuild) {
                BuildTimeConfig.SERVICES_MAPTILER_APIKEY
            } else {
                System.getenv("ELEMENT_ANDROID_MAPTILER_API_KEY")
                    ?: readLocalProperty("services.maptiler.apikey")
            }
                ?: ""
        )
        buildConfigFieldStr(
            name = "MAPTILER_LIGHT_MAP_ID",
            value = if (isEnterpriseBuild) {
                BuildTimeConfig.SERVICES_MAPTILER_LIGHT_MAPID
            } else {
                System.getenv("ELEMENT_ANDROID_MAPTILER_LIGHT_MAP_ID")
                    ?: readLocalProperty("services.maptiler.lightMapId")
            }
                ?: "basic-v2"
        )
        buildConfigFieldStr(
            name = "MAPTILER_DARK_MAP_ID",
            value = if (isEnterpriseBuild) {
                BuildTimeConfig.SERVICES_MAPTILER_DARK_MAPID
            } else {
                System.getenv("ELEMENT_ANDROID_MAPTILER_DARK_MAP_ID")
                    ?: readLocalProperty("services.maptiler.darkMapId")
            }
                ?: "basic-v2-dark"
        )
    }

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
                "test/src/main/kotlin",
            )
        )

        getByName("main").res.setSrcDirs(
            listOf(
                "api/src/main/res",
                "impl/src/main/res",
            )
        )

        getByName("test").java.setSrcDirs(
            listOf(
                "src/test/kotlin",
                "api/src/test/kotlin",
                "impl/src/test/kotlin",
            )
        )
    }
}

dependencies {
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.core)
    implementation(project(":libraries:matrix"))
    implementation(projects.libraries.matrixui)
    implementation(project(":libraries:textcomposer"))
    implementation(projects.libraries.uiStrings)
    implementation(libs.coil.compose)
    implementation(projects.libraries.maplibreCompose)
    implementation(projects.libraries.di)
    implementation(projects.libraries.androidutils)
    implementation(project(":services:analytics"))
    implementation(libs.accompanist.permission)
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(projects.libraries.testtags)
    testImplementation(project(":services:analytics"))
    testImplementation(project(":features:messages"))
}
