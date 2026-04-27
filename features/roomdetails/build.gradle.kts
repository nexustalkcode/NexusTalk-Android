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
    namespace = "io.element.android.features.roomdetails.impl"

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
    implementation(project(":libraries:matrix"))
    implementation(projects.appconfig)
    implementation(projects.libraries.core)
    implementation(projects.libraries.matrixui)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.uiStrings)
    implementation(projects.libraries.androidutils)
    implementation(project(":libraries:mediapickers"))
    implementation(project(":libraries:mediaupload"))
    implementation(project(":libraries:mediaviewer"))
    implementation(project(":libraries:featureflag"))
    implementation(project(":libraries:permissions"))
    implementation(project(":libraries:preferences"))
    implementation(projects.libraries.testtags)
    api(project(":libraries:usersearch"))
    api(project(":services:apperror"))
    implementation(libs.coil.compose)
    implementation(project(":features:call"))
    implementation(project(":features:startchat"))
    implementation(project(":features:leaveroom"))
    implementation(project(":features:userprofile"))
    implementation(project(":services:analytics"))
    implementation(project(":features:poll"))
    implementation(project(":features:messages"))
    implementation(project(":features:roomcall"))
    implementation(project(":features:knockrequests"))
    implementation(project(":features:verifysession"))
    implementation(project(":features:reportroom"))
    implementation(project(":features:roommembermoderation"))
    implementation(project(":features:rolesandpermissions"))
    implementation(project(":features:securityandprivacy"))
    implementation(project(":features:roomdetailsedit"))
    implementation(project(":features:invitepeople"))
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:mediaupload"))
    testImplementation(project(":libraries:mediapickers"))
    testImplementation(project(":libraries:mediaviewer"))
    testImplementation(project(":libraries:permissions"))
    testImplementation(project(":libraries:preferences"))
    testImplementation(project(":libraries:usersearch"))
    testImplementation(project(":libraries:featureflag"))
    testImplementation(project(":features:call"))
    testImplementation(project(":features:rolesandpermissions"))
    testImplementation(project(":features:securityandprivacy"))
    testImplementation(project(":features:roomdetailsedit"))
    testImplementation(project(":features:knockrequests"))
    testImplementation(project(":features:messages"))
    testImplementation(project(":features:poll"))
    testImplementation(project(":features:reportroom"))
    testImplementation(project(":features:startchat"))
    testImplementation(project(":features:verifysession"))
    testImplementation(project(":services:analytics"))
}
