/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

import org.gradle.api.JavaVersion
import org.gradle.jvm.toolchain.JavaLanguageVersion

/**
 * versionCode 比较敏感，因为同时存在 App Bundle 与 APK。
 * Play 商店允许的 versionCode 上限（仅供参考）：
 * 2_100_000_000
 *
 * 另请注意：在 app/build.gradle.kts 中，versionCode 会乘以 10：
 * ```
 * output.versionCode.set((output.versionCode.orNull ?: 0) * 10 + abiCode)
 * ```
 * 应用版本采用类 CalVer 方式。versionCode 计算方式如下：
 * - 年份 2 位
 * - 月份 2 位
 * - 当月发版序号 1（或 2）位
 * 注意：versionCode 必须大于以往各次发版算出的值，因此内部用 4 位年份参与计算。
 * 例如 2025 年 1 月的首次发版：
 * - 版本名：25.01.0
 * - versionCode：20250100a（即 202_501_00a），其中 `a` 表示架构代码（ABI）
 */

/**
 * 版本年份，2 位数字。
 * 请勿手动修改；由发布脚本更新。
 */
private const val versionYear = 26

/**
 * 版本月份，2 位数字。取值须在 [1,12]。
 * 请勿手动修改；由发布脚本更新。
 */
private const val versionMonth = 3

/**
 * 当月发版序号。取值须在 [0,99]。
 * 请勿手动修改；由发布脚本更新。
 */
private const val versionReleaseNumber = 25

object Versions {
    /**
     * 将写入 Android Manifest 的基础 versionCode。
     * 构建 APK 时会在该值上于构建期叠加 ABI 代码；AAB 的 ABI 代码为 0。
     * 具体算法见上文注释。
     */
    const val VERSION_CODE = (2000 + versionYear) * 10_000 + versionMonth * 100 + versionReleaseNumber
    val VERSION_NAME = "$versionYear.${versionMonth.toString().padStart(2, '0')}.$versionReleaseNumber"

    /**
     * 编译用 SDK 版本。新版 Android 发布后需同步更新。
     * 更新 COMPILE_SDK 时请同时更新 BUILD_TOOLS_VERSION。
     */
    const val COMPILE_SDK = 36

    /**
     * Build Tools 版本。须与 COMPILE_SDK 保持一致。
     * 该值会被发布脚本使用。
     */
    @Suppress("unused")
    private const val BUILD_TOOLS_VERSION = "36.0.0"

    /**
     * 目标 SDK 版本。建议与 COMPILE_SDK 保持同步。
     */
    const val TARGET_SDK = 36

    /**
     * FOSS 构建的最低 SDK 版本。
     */
    private const val MIN_SDK_FOSS = 24

    /**
     * 企业版构建的最低 SDK 版本。
     */
    private const val MIN_SDK_ENTERPRISE = 33

    /**
     * 将写入 Android Manifest 的 minSdkVersion。
     */
    val minSdk = if (isEnterpriseBuild) MIN_SDK_ENTERPRISE else MIN_SDK_FOSS

    /**
     * 编译使用的 Java 版本。
     * 需要升级 Java 时修改此值。
     */
    private const val JAVA_VERSION = 21

    val javaVersion: JavaVersion = JavaVersion.toVersion(JAVA_VERSION)
    val javaLanguageVersion: JavaLanguageVersion = JavaLanguageVersion.of(JAVA_VERSION)

    // 校验常量，避免带着错误配置发布
    init {
        require(versionMonth in 1..12) { "versionMonth 必须在 [1,12] 内" }
        require(versionReleaseNumber in 0..99) { "versionReleaseNumber 必须在 [0,99] 内" }
        require(BUILD_TOOLS_VERSION.startsWith(COMPILE_SDK.toString())) { "更新 COMPILE_SDK 时请同时更新 BUILD_TOOLS_VERSION" }
    }
}
