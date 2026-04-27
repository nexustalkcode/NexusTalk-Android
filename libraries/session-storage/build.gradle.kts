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
    alias(libs.plugins.sqldelight)
}

setupDependencyInjection()

android {
    // session-storage 的 SQLDelight 生成类会跟 namespace 绑定。
    // 这里必须沿用 impl 的 namespace，避免 SessionDatabase 生成包名变化后把 DI/Metro 解析打坏。
    namespace = "io.element.android.libraries.sessionstorage.impl"

    sourceSets {
        getByName("main").java.setSrcDirs(
            listOf(
                "src/main/kotlin",
                "api/src/main/kotlin",
                "impl/src/main/kotlin",
                "test/src/main/kotlin",
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
    implementation(libs.coroutines.core)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.core)
    implementation(projects.libraries.encryptedDb)
    implementation(libs.sqldelight.driver.android)
    implementation(libs.sqlcipher)
    implementation(libs.sqlite)
    implementation(projects.libraries.di)
    implementation(libs.sqldelight.coroutines)
    testImplementation(libs.sqldelight.driver.jvm)
}

sqldelight {
    databases {
        create("SessionDatabase") {
            // 旧 SQLDelight 定义仍保留在 impl 目录，第一阶段只收 Gradle 边界，因此这里显式补回源目录。
            srcDirs("impl/src/main/sqldelight")
            schemaOutputDirectory = File("src/main/sqldelight/databases")
            verifyMigrations = true
        }
    }
}
