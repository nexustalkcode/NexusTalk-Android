import config.BuildTimeConfig
import extension.buildConfigFieldStr
import extension.testCommonDependencies

/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2024 New Vector Ltd.
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
    namespace = "io.element.android.libraries.matrix.api"

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
}

dependencies {
    implementation(projects.libraries.di)
    implementation(projects.libraries.androidutils)
    api(projects.libraries.core)
    implementation(libs.matrix.analytics.events)
    implementation(libs.serialization.json)
    api(project(":libraries:session-storage"))
    implementation(libs.coroutines.core)
    api(projects.libraries.architecture)

   
    testImplementation(project(":libraries:matrix:test"))
}
