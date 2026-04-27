/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.x.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import io.element.android.features.call.api.ElementCallEntryPoint
import io.element.android.features.call.impl.ForegroundIncomingCallObserver
import io.element.android.features.call.impl.utils.ActiveCallManager
import io.element.android.features.api.MigrationEntryPoint
import io.element.android.features.enterprise.api.EnterpriseService
import io.element.android.features.lockscreen.api.LockScreenEntryPoint
import io.element.android.features.lockscreen.api.LockScreenService
import io.element.android.features.rageshake.api.reporter.BugReporter
import io.element.android.libraries.core.meta.BuildMeta
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.element.android.libraries.di.identifiers.SentrySdkDsn
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.matrix.api.platform.InitPlatformService
import io.element.android.libraries.matrix.api.tracing.TracingService
import io.element.android.libraries.preferences.api.store.AppPreferencesStore
import io.element.android.services.appnavstate.api.AppForegroundStateService
import io.element.android.services.analytics.api.AnalyticsService

/**
 * 应用绑定接口。
 *
 * 定义了应用级别各种服务的访问方法。
 * 通过 @ContributesTo 注解贡献到 AppScope，
 * 允许其他组件注入并获取所需的服务实例。
 *
 * 提供的服务包括：
 * - SnackbarDispatcher：消息提示分发器
 * - TracingService：日志追踪服务
 * - InitPlatformService：平台初始化服务
 * - BugReporter：错误报告器
 * - LockScreenService：锁屏服务
 * - AppPreferencesStore：应用偏好设置存储
 * - MigrationEntryPoint：迁移入口点
 * - LockScreenEntryPoint：锁屏入口点
 * - AnalyticsService：分析服务
 * - EnterpriseService：企业服务
 * - FeatureFlagService：功能标志服务
 * - BuildMeta：构建元数据
 * - SentrySdkDsn：Sentry DSN 配置
 */
@ContributesTo(AppScope::class)
interface AppBindings {
    /**
     * 提供 SnackbarDispatcher 实例。
     *
     * SnackbarDispatcher 用于在应用中显示临时消息提示（Snackbars），
     * 支持向用户反馈操作结果或显示简短信息。
     */
    fun snackbarDispatcher(): SnackbarDispatcher

    /**
     * 提供 TracingService 实例。
     *
     * TracingService 用于应用内部的日志追踪和诊断功能，
     * 帮助开发者记录和分析应用运行时的行为信息。
     */
    fun tracingService(): TracingService

    /**
     * 提供 InitPlatformService 实例。
     *
     * InitPlatformService 负责在应用启动时初始化平台相关的功能和配置，
     * 确保应用能在当前平台上正常运行。
     */
    fun platformService(): InitPlatformService

    /**
     * 提供 BugReporter 实例。
     *
     * BugReporter 用于收集应用崩溃信息和用户反馈，
     * 并将这些信息提交到错误追踪系统以便分析和修复问题。
     */
    fun bugReporter(): BugReporter

    /**
     * 提供 LockScreenService 实例。
     *
     * LockScreenService 管理应用锁屏功能，包括锁屏状态的设置、
     * 验证逻辑以及锁屏相关的事件处理。
     */
    fun lockScreenService(): LockScreenService

    /**
     * 提供 AppPreferencesStore 实例。
     *
     * AppPreferencesStore 是应用偏好设置的持久化存储接口，
     * 负责读写用户的个性化配置和功能设置。
     */
    fun preferencesStore(): AppPreferencesStore

    /**
     * 提供 MigrationEntryPoint 实例。
     *
     * MigrationEntryPoint 是数据迁移功能的导航入口，
     * 负责处理应用版本升级时的数据迁移和兼容性处理。
     */
    fun migrationEntryPoint(): MigrationEntryPoint

    /**
     * 提供 LockScreenEntryPoint 实例。
     *
     * LockScreenEntryPoint 是锁屏界面的导航入口点，
     * 提供访问锁屏相关功能和屏幕的途径。
     */
    fun lockScreenEntryPoint(): LockScreenEntryPoint

    /**
     * 提供 AnalyticsService 实例。
     *
     * AnalyticsService 负责收集和分析用户行为数据，
     * 帮助团队了解应用使用情况并优化用户体验。
     */
    fun analyticsService(): AnalyticsService

    /**
     * 提供 EnterpriseService 实例。
     *
     * EnterpriseService 管理企业环境下的特殊功能和配置，
     * 支持企业级安全策略和单点登录等功能。
     */
    fun enterpriseService(): EnterpriseService

    /**
     * 提供 FeatureFlagService 实例。
     *
     * FeatureFlagService 管理功能开关（Feature Flags），
     * 允许动态控制功能的启用和禁用，支持渐进式发布和 A/B 测试。
     */
    fun featureFlagService(): FeatureFlagService

    fun appForegroundStateService(): AppForegroundStateService

    fun foregroundIncomingCallObserver(): ForegroundIncomingCallObserver

    fun activeCallManager(): ActiveCallManager

    fun elementCallEntryPoint(): ElementCallEntryPoint

    /**
     * 提供 BuildMeta 实例。
     *
     * BuildMeta 包含应用的构建元数据信息，
     * 如版本号、构建类型、Git 提交信息等。
     */
    fun buildMeta(): BuildMeta

    /**
     * 提供 SentrySdkDsn 配置。
     *
     * SentrySdkDsn 是 Sentry 错误监控服务的 DSN（Data Source Name）配置，
     * 用于将应用错误和崩溃报告发送到 Sentry 平台。
     * 如果未配置 Sentry，此值可能为空。
     */
    fun sentrySdkDsn(): SentrySdkDsn?
}
