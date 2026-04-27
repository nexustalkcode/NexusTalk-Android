import config.BuildTimeConfig
import extension.buildConfigFieldStr
import extension.setupDependencyInjection
import extension.testCommonDependencies

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
    // 先沿用 api 的 namespace，避免第一阶段放大 import / 包名 / 资源引用改动面。
    namespace = "io.element.android.features.preferences.impl"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    
    buildFeatures {
        buildConfig = true
    }
    
    defaultConfig {
        buildConfigFieldStr(
            name = "URL_COPYRIGHT",
            value = BuildTimeConfig.URL_COPYRIGHT ?: "https://element.io/copyright",
        )
        buildConfigFieldStr(
            name = "URL_ACCEPTABLE_USE",
            value = BuildTimeConfig.URL_ACCEPTABLE_USE ?: "https://element.io/acceptable-use-policy-terms",
        )
        buildConfigFieldStr(
            name = "URL_PRIVACY",
            value = BuildTimeConfig.URL_PRIVACY ?: "https://element.io/privacy",
        )
    }

    sourceSets {
        getByName("main").java.setSrcDirs(
            listOf(
                "src/main/kotlin",
                "api/src/main/kotlin",
                "impl/src/main/kotlin",
            )
        )

        getByName("main").res.setSrcDirs(
            listOf(
                "impl/src/main/res",
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

dependencies {
    implementation(projects.libraries.architecture)
    api(project(":libraries:matrix"))
    implementation(projects.libraries.androidutils)
    implementation(projects.appconfig)
    implementation(projects.libraries.core)
    implementation(project(":libraries:matrix"))
    implementation(projects.libraries.designsystem)
    implementation(project(":libraries:featureflag"))
    implementation(projects.libraries.network)
    implementation(project(":libraries:pushstore"))
    implementation(project(":libraries:indicator"))
    implementation(project(":libraries:preferences"))
    implementation(project(":libraries:troubleshoot"))
    implementation(projects.libraries.testtags)
    implementation(projects.libraries.uiStrings)
    implementation(projects.libraries.matrixui)
    implementation(projects.libraries.qrcode)
    implementation(project(":libraries:mediapickers"))
    implementation(project(":libraries:mediaupload"))
    implementation(project(":libraries:permissions"))
    implementation(project(":libraries:push"))
    implementation(project(":libraries:pushproviders"))
    implementation(projects.libraries.uiUtils)
    implementation(project(":libraries:fullscreenintent"))
    implementation(projects.libraries.uiCommon)
    implementation(project(":features:rageshake"))
    implementation(project(":features:lockscreen"))
    implementation(project(":features:analytics"))
    implementation(project(":features:enterprise"))
    implementation(project(":features:licenses"))
    implementation(project(":features:logout"))
    implementation(project(":features:deactivation"))
    implementation(project(":features:home"))
    implementation(project(":features:invite"))
    implementation(project(":services:analytics"))
    implementation(project(":services:appnavstate"))
    implementation(project(":services:toolbox"))
    implementation(libs.datetime)
    implementation(libs.coil.compose)
    implementation(libs.color.picker)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.showkase)
    implementation(platform(libs.network.okhttp.bom))
    implementation(libs.network.okhttp)
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:featureflag"))
    testImplementation(project(":libraries:mediapickers"))
    testImplementation(project(":libraries:mediaupload"))
    testImplementation(project(":libraries:permissions"))
    testImplementation(project(":libraries:preferences"))
    testImplementation(project(":libraries:push"))
    testImplementation(project(":libraries:pushstore"))
    testImplementation(project(":libraries:roomselect"))
    testImplementation(project(":features:deactivation"))
    testImplementation(project(":features:enterprise"))
    testImplementation(project(":features:invite"))
    testImplementation(project(":features:licenses"))
    testImplementation(project(":features:lockscreen"))
    testImplementation(project(":features:rageshake"))
    testImplementation(project(":features:logout"))
    testImplementation(project(":libraries:indicator"))
    testImplementation(project(":libraries:pushproviders"))
    testImplementation(project(":libraries:session-storage"))
    testImplementation(project(":services:appnavstate"))
    testImplementation(project(":services:analytics"))
    testImplementation(project(":services:toolbox"))
}
