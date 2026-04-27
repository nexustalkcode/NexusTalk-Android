/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

@file:Suppress("UnstableApiUsage")

import extension.allFeaturesApi
import extension.setupDependencyInjection
import extension.testCommonDependencies

plugins {
    id("io.element.android-compose-library")
    id("kotlin-parcelize")
}

android {
    namespace = "io.element.android.appnav"
}

setupDependencyInjection()

dependencies {
    allFeaturesApi(project)

    implementation(projects.libraries.core)
    implementation(project(":libraries:accountselect"))
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.architecture)
    implementation(project(":libraries:deeplink"))
    implementation(project(":libraries:featureflag"))
    implementation(project(":libraries:matrix"))
    implementation(project(":libraries:oidc"))
    implementation(project(":libraries:preferences"))
    implementation(project(":libraries:push"))
    implementation(project(":libraries:pushproviders"))
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.matrixui)
    implementation(project(":libraries:matrixmedia"))
    implementation(projects.libraries.uiCommon)
    implementation(projects.libraries.uiStrings)
    implementation(project(":features:login"))

    implementation(libs.coil)

    implementation(project(":features:announcement"))
    implementation(project(":features:ftue"))
    implementation(project(":features:ftue"))
    implementation(project(":features:linknewdevice"))
    implementation(project(":features:share"))

    implementation(project(":services:apperror"))
    implementation(project(":services:appnavstate"))
    implementation(project(":services:analytics"))

    testImplementation(project(":features:login"))
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:oidc"))
    testImplementation(project(":libraries:preferences"))
    testImplementation(project(":libraries:push"))
    testImplementation(project(":libraries:pushproviders"))
    testImplementation(project(":features:forward"))
    testImplementation(project(":features:messages"))
    testImplementation(project(":features:networkmonitor"))
    testImplementation(project(":features:rageshake"))
    testImplementation(project(":services:appnavstate"))
    testImplementation(project(":services:analytics"))
    testImplementation(project(":services:toolbox"))
}
