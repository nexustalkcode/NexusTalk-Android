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
    // mediaviewer 主源码里显式引用的是 impl.R，因此这里必须沿用 impl 的 namespace，
    // 否则第一阶段只收 Gradle 边界时就会把所有资源引用打断。
    namespace = "io.element.android.libraries.mediaviewer.impl"

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
                "test/src/test/kotlin",
            )
        )
    }
}

dependencies {
    implementation(projects.libraries.core)
    implementation(projects.libraries.architecture)
    implementation(project(":libraries:matrix"))
    implementation(libs.coroutines.core)
    implementation(libs.coil.compose)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.telephoto.zoomableimage)
    implementation(libs.vanniktech.blurhash)
    implementation(libs.telephoto.flick)
    implementation(project(":features:enterprise"))
    implementation(project(":features:viewfolder"))
    implementation(projects.libraries.androidutils)
    implementation(project(":libraries:audio"))
    implementation(project(":libraries:dateformatter"))
    implementation(projects.libraries.di)
    implementation(projects.libraries.designsystem)
    implementation(project(":libraries:featureflag"))
    implementation(project(":libraries:matrixmedia"))
    implementation(projects.libraries.uiStrings)
    implementation(project(":libraries:voiceplayer"))
    implementation(project(":services:toolbox"))
    testImplementation(project(":features:enterprise"))
    testImplementation(project(":libraries:audio"))
    testImplementation(project(":libraries:dateformatter"))
    testImplementation(project(":libraries:featureflag"))
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(projects.libraries.matrixui)
    testImplementation(project(":services:toolbox"))
    testImplementation(libs.coroutines.core)
}
