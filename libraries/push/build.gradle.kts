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
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

android {
    // 继续沿用原 impl 的 namespace，先避免本轮单模块收敛把大量 R 引用一并改动。
    namespace = "io.element.android.libraries.push.impl"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        getByName("main").apply {
            java.setSrcDirs(
                listOf(
                    "src/main/kotlin",
                    "api/src/main/kotlin",
                    "impl/src/main/kotlin",
                    "test/src/main/kotlin",
                )
            )
            res.setSrcDirs(
                listOf(
                    "src/main/res",
                    "impl/src/main/res",
                )
            )
        }
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
    implementation(libs.androidx.corektx)
    implementation(libs.androidx.datastore.preferences)
    implementation(platform(libs.network.retrofit.bom))
    implementation(libs.network.retrofit)
    implementation(libs.serialization.json)
    implementation(libs.coil)

    implementation(libs.sqldelight.driver.android)
    implementation(libs.sqlcipher)
    implementation(libs.sqlite)
    implementation(libs.sqldelight.coroutines)
    implementation(projects.libraries.encryptedDb)

    implementation(projects.appconfig)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.core)
    implementation(project(":libraries:dateformatter"))
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.di)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.network)
    // matrix 已合并到根模块，这里直接依赖根模块，避免继续保留旧 api 坐标。
    implementation(project(":libraries:matrix"))
    implementation(projects.libraries.matrixui)
    implementation(project(":libraries:matrixmedia"))
    implementation(project(":features:networkmonitor"))
    implementation(project(":libraries:preferences"))
    implementation(project(":libraries:session-storage"))
    implementation(projects.libraries.uiStrings)
    implementation(project(":libraries:workmanager"))
    // push 主源码里的通知排障测试只需要 troubleshoot 的 API 契约，
    // 这里显式依赖 api 子模块，避免把根模块 impl 一起拉进来后再次形成循环依赖。
    implementation(project(":libraries:troubleshoot:api"))
    implementation(project(":features:enterprise"))
    implementation(project(":features:lockscreen"))
    implementation(project(":libraries:featureflag"))
    implementation(project(":libraries:pushproviders"))
    implementation(project(":libraries:pushstore"))

    implementation(project(":services:analytics"))
    implementation(project(":services:appnavstate"))
    implementation(project(":services:toolbox"))

    testImplementation(libs.coil.test)
    testImplementation(libs.coroutines.test)
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:matrixmedia"))
    testImplementation(project(":libraries:preferences"))
    testImplementation(project(":libraries:session-storage"))
    testImplementation(project(":libraries:pushproviders"))
    testImplementation(project(":libraries:pushstore"))
    testImplementation(project(":libraries:workmanager"))
    testImplementation(project(":features:call"))
    testImplementation(project(":features:enterprise"))
    testImplementation(project(":features:lockscreen"))
    testImplementation(project(":features:networkmonitor"))
    testImplementation(project(":services:appnavstate"))
    testImplementation(project(":services:toolbox"))
    testImplementation(project(":libraries:featureflag"))
    testImplementation(libs.test.turbine)
    testImplementation(libs.kotlinx.collections.immutable)
}

sqldelight {
    databases {
        create("PushDatabase") {
            // 单模块收敛后，SQLDelight 源文件仍然留在旧 impl 目录里，
            // 这里需要显式补回源目录，否则生成任务会认为数据库定义不存在。
            srcDirs("impl/src/main/sqldelight")
            schemaOutputDirectory = File("src/main/sqldelight/databases")
        }
    }
}
