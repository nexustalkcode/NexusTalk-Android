/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package extension

import ModulesConfig
import config.AnalyticsConfig
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.closureOf
import org.gradle.kotlin.dsl.project

private fun DependencyHandlerScope.implementation(dependency: Any) = dependencies.add("implementation", dependency)
private fun DependencyHandlerScope.testImplementation(dependency: Any) = dependencies.add("testImplementation", dependency)
private fun DependencyHandlerScope.testReleaseImplementation(dependency: Any) = dependencies.add("testReleaseImplementation", dependency)
internal fun DependencyHandler.implementation(dependency: Any) = add("implementation", dependency)

// Implementation + config block
private fun DependencyHandlerScope.implementation(
    dependency: Any,
    config: Action<ExternalModuleDependency>
) = dependencies.add("implementation", dependency, closureOf<ExternalModuleDependency> { config.execute(this) })

private fun DependencyHandlerScope.androidTestImplementation(dependency: Any) = dependencies.add("androidTestImplementation", dependency)

private fun DependencyHandlerScope.debugImplementation(dependency: Any) = dependencies.add("debugImplementation", dependency)
private fun DependencyHandlerScope.releaseImplementation(dependency: Any) = dependencies.add("releaseImplementation", dependency)

/**
 * Dependencies used for unit tests.
 */
fun DependencyHandlerScope.testCommonDependencies(
    libs: LibrariesForLibs,
    includeTestComposeView: Boolean = false,
) {
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.molecule.runtime)
    testImplementation(libs.test.appyx.junit)
    testImplementation(libs.test.arch.core)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.robolectric)
    testImplementation(libs.test.truth)
    testImplementation(libs.test.turbine)
    testImplementation(project(":tests:testutils"))
    if (includeTestComposeView) {
        testImplementation(libs.androidx.compose.ui.test.junit)
        testReleaseImplementation(libs.androidx.compose.ui.test.manifest)
    }
}

/**
 * Dependencies used by all the modules
 */
fun DependencyHandlerScope.commonDependencies(libs: LibrariesForLibs) {
    implementation(libs.timber)
}

/**
 * Dependencies used by all the modules with composable items
 */
fun DependencyHandlerScope.composeDependencies(libs: LibrariesForLibs) {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.kotlinx.collections.immutable)
}

fun DependencyHandlerScope.allLibrariesImpl() {
    implementation(project(":libraries:androidutils"))
    implementation(project(":libraries:deeplink"))
    implementation(project(":libraries:designsystem"))
    // matrix 已合并到根模块，这里统一走 :libraries:matrix，避免 app 装配阶段继续引用已删除的 impl 子模块。
    implementation(project(":libraries:matrix"))
    implementation(project(":libraries:matrixui"))
    implementation(project(":libraries:matrixmedia"))
    implementation(project(":libraries:network"))
    implementation(project(":libraries:core"))
    implementation(project(":libraries:eventformatter"))
    implementation(project(":libraries:indicator"))
    implementation(project(":libraries:permissions"))
    implementation(project(":libraries:audio"))
    implementation(project(":libraries:push"))
    implementation(project(":libraries:featureflag"))
    implementation(project(":libraries:pushstore"))
    implementation(project(":libraries:preferences"))
    implementation(project(":libraries:architecture"))
    implementation(project(":libraries:dateformatter"))
    implementation(project(":libraries:di"))
    implementation(project(":libraries:session-storage"))
    implementation(project(":libraries:mediapickers"))
    implementation(project(":libraries:mediaupload"))
    implementation(project(":libraries:usersearch"))
    implementation(project(":libraries:textcomposer"))
    implementation(project(":libraries:accountselect"))
    implementation(project(":libraries:roomselect"))
    implementation(project(":libraries:cryptography"))
    implementation(project(":libraries:voiceplayer"))
    implementation(project(":libraries:voicerecorder"))
    implementation(project(":libraries:mediaplayer"))
    implementation(project(":libraries:mediaviewer"))
    implementation(project(":libraries:troubleshoot"))
    implementation(project(":libraries:fullscreenintent"))
    implementation(project(":libraries:wellknown"))
    implementation(project(":libraries:oidc"))
    implementation(project(":libraries:workmanager"))
    implementation(project(":libraries:recentemojis"))
}

fun DependencyHandlerScope.allServicesImpl() {
    implementation(project(":services:analytics"))
    when (ModulesConfig.analyticsConfig) {
        AnalyticsConfig.Disabled -> Unit
        is AnalyticsConfig.Enabled -> {
            if (ModulesConfig.analyticsConfig.withPosthog || ModulesConfig.analyticsConfig.withSentry) {
                implementation(project(":services:analyticsproviders"))
            }
        }
    }

    implementation(project(":services:apperror"))
    implementation(project(":services:appnavstate"))
    implementation(project(":services:toolbox"))
}

fun DependencyHandlerScope.allEnterpriseImpl(project: Project) = addAll(
    project = project,
    modulePrefix = ":enterprise:features",
    moduleSuffix = ":impl",
)

fun DependencyHandlerScope.allFeaturesImpl(project: Project) {
    addTopLevelFeatureRoots(project)
    addAll(
        project = project,
        modulePrefix = ":features",
        moduleSuffix = ":impl",
    )
}

fun DependencyHandlerScope.allFeaturesApi(project: Project) {
    addTopLevelFeatureRoots(project)
    addAll(
        project = project,
        modulePrefix = ":features",
        moduleSuffix = ":api",
    )
}

private fun DependencyHandlerScope.addAll(
    project: Project,
    modulePrefix: String,
    moduleSuffix: String,
) {
    val subProjects = project.rootProject.subprojects.filter { it.path.startsWith(modulePrefix) && it.path.endsWith(moduleSuffix) }
    for (p in subProjects) {
        add("implementation", p)
    }
}

private fun DependencyHandlerScope.addTopLevelFeatureRoots(project: Project) {
    val topLevelFeatureRoots = project.rootProject.subprojects.filter { candidate ->
        candidate.path.startsWith(":features:") &&
            candidate.path.count { it == ':' } == 2
    }
    for (p in topLevelFeatureRoots) {
        add("implementation", p)
    }
}
