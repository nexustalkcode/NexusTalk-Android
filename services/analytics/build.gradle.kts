import ModulesConfig
import config.AnalyticsConfig
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
}

private val analyticsEnabled = ModulesConfig.analyticsConfig is AnalyticsConfig.Enabled

android {
    namespace = "io.element.android.services.analytics"

    sourceSets {
        getByName("main").java.setSrcDirs(
            listOf(
                // 第一阶段先复用原有子模块源码目录，优先完成 Gradle 模块收敛，避免大规模移动文件。
                "src/main/kotlin",
                "api/src/main/kotlin",
                "impl/src/main/kotlin",
                "noop/src/main/kotlin",
                "compose/src/main/kotlin",
                // 这里暂时把原 test 模块里的 fake 并入主源码集，保证其他模块改成依赖 :services:analytics 后仍可直接复用这些测试替身。
                "test/src/main/kotlin",
            )
        )
        getByName("test").java.setSrcDirs(
            listOf(
                "src/test/kotlin",
                "impl/src/test/kotlin",
                "noop/src/test/kotlin",
            )
        )

        if (analyticsEnabled) {
            // impl/noop 的具体 Metro 绑定拆到独立源码目录里，避免两套实现同时向同一接口注册绑定。
            getByName("main").java.srcDir("src/enabled/kotlin")
        } else {
            getByName("main").java.srcDir("src/disabled/kotlin")
        }
    }
}

setupDependencyInjection()

dependencies {
    api(project(":services:analyticsproviders"))
    api(project(":services:toolbox"))

    implementation(project(":libraries:androidutils"))
    implementation(project(":libraries:architecture"))
    implementation(project(":libraries:core"))
    implementation(project(":libraries:di"))
    implementation(project(":libraries:preferences"))
    implementation(project(":libraries:session-storage"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coroutines.core)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.turbine)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.molecule.runtime)
    testImplementation(project(":libraries:matrix:test"))
    testImplementation(project(":libraries:session-storage"))
    testImplementation(project(":services:analyticsproviders"))
    testImplementation(project(":services:appnavstate"))
    testImplementation(project(":services:toolbox"))
}
