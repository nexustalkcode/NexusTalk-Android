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
    namespace = "io.element.android.features.messages.impl"

    sourceSets.getByName("main").manifest.srcFile("impl/src/main/AndroidManifest.xml")

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
    implementation(projects.libraries.designsystem)
    implementation(project(":libraries:matrix"))
    implementation(project(":libraries:mediaviewer"))
    implementation(project(":libraries:preferences"))
    api(project(":libraries:textcomposer"))
    implementation(projects.appconfig)
    implementation(project(":features:call"))
    implementation(project(":features:enterprise"))
    implementation(project(":features:forward"))
    implementation(project(":features:location"))
    implementation(project(":features:poll"))
    implementation(project(":features:roomcall"))
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.core)
    implementation(projects.libraries.matrixui)
    implementation(project(":libraries:matrixmedia"))
    implementation(project(":libraries:textcomposer"))
    implementation(projects.libraries.uiStrings)
    implementation(project(":libraries:dateformatter"))
    implementation(project(":libraries:eventformatter"))
    implementation(project(":libraries:mediapickers"))
    implementation(project(":libraries:featureflag"))
    implementation(project(":libraries:mediaupload"))
    implementation(project(":libraries:permissions"))
    implementation(project(":libraries:recentemojis"))
    implementation(project(":libraries:roomselect"))
    implementation(project(":libraries:voiceplayer"))
    implementation(project(":libraries:voicerecorder"))
    implementation(project(":libraries:mediaplayer"))
    implementation(project(":libraries:push"))
    implementation(projects.libraries.uiUtils)
    implementation(projects.libraries.testtags)
    implementation(project(":features:networkmonitor"))
    implementation(project(":services:analytics"))
    implementation(project(":services:appnavstate"))
    implementation(project(":services:toolbox"))
    implementation(libs.coil.compose)
    implementation(libs.datetime)
    implementation(libs.jsoup)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.sigpwned.emoji4j)
    implementation(libs.vanniktech.blurhash)
    implementation(libs.telephoto.zoomableimage)
    implementation(libs.matrix.emojibase.bindings)
    implementation(project(":features:knockrequests"))
    implementation(project(":features:roommembermoderation"))
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:dateformatter"))
    testImplementation(project(":libraries:push"))
    testImplementation(project(":features:call"))
    testImplementation(project(":features:forward"))
    testImplementation(project(":features:knockrequests"))
    testImplementation(project(":features:location"))
    testImplementation(project(":features:networkmonitor"))
    testImplementation(project(":services:analytics"))
    testImplementation(project(":services:toolbox"))
    testImplementation(project(":libraries:featureflag"))
    testImplementation(project(":libraries:mediaupload"))
    testImplementation(project(":libraries:mediapickers"))
    testImplementation(project(":libraries:permissions"))
    testImplementation(project(":libraries:preferences"))
    testImplementation(project(":libraries:voicerecorder"))
    testImplementation(project(":libraries:mediaplayer"))
    testImplementation(project(":libraries:mediaviewer"))
    testImplementation(projects.libraries.testtags)
    testImplementation(project(":features:poll"))
    testImplementation(project(":libraries:eventformatter"))
    testImplementation(project(":libraries:recentemojis"))
}
